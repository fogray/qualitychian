package com.inspur.fabric.sdk.client;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.inspur.fabric.channel.IFabricChannelService;
import com.inspur.fabric.client.BlockDecoder;
import com.inspur.fabric.moniter.IMoniterService;
import com.inspur.fabric.sdk.base.BaseChannel;
import com.inspur.fabric.sdk.base.FabricManager;
import com.inspur.pub.cache.CacheUtil;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loushang.waf.ComponentFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/25
 */
public class BlockUtil {
    private static final Log log = LogFactory.getLog(BlockUtil.class);
    private static BlockUtil instance = null;
    public static BlockUtil getInstance(){
        if (instance == null){
            synchronized (BlockUtil.class){
                if(instance == null){
                    instance = new BlockUtil();
                }
            }
        }
        instance.updateChannels();
        return instance;
    }

    private static final IFabricChannelService fabricChannelService = (IFabricChannelService) ComponentFactory.getBean("fabricChannelService");

    private static final IMoniterService moniterService = (IMoniterService) ComponentFactory.getBean("moniterService");

    private List<String> channelNames = new ArrayList<String>();

    private BlockUtil(){
        updateChannels();
        for (String channelName : channelNames){
            try {
                BaseChannel.getInstance(channelName,"peerOrg1Admin", FabricManager.getConfig().getOrgNames());
            } catch (Exception e) {
                log.error("BlockUtil constructor init channel "+channelName+" error:",e);
            }
        }
    }

    private void updateChannels(){
        channelNames = new ArrayList<String>();
        List<Map<String, Object>> list = fabricChannelService.getValidChannels();
        for(Map<String, Object> map : list){
            channelNames.add((String)map.get("CHANNEL_NAME"));
        }
    }

    public Channel getChannel(String channelName) {
        Channel channel = null;
        try {
            channel = BaseChannel.getInstance(channelName, "peerOrg1Admin", FabricManager.getConfig().getOrgNames())
                    .getChannel();
        } catch (Exception e) {
            log.error("BlockUtil getChannel error:",e);
        }

        return channel;
    }

    /**
     * 获取某通道的区块高度
     * @param channelName 通道名称
     * @return
     */
    public long getBlockHeight(String channelName){
        long blockHeight = 0;
        try {
            Channel channel = getChannel(channelName);
            if (channel == null) {
                return blockHeight;
            }
            blockHeight += channel.queryBlockchainInfo().getHeight();
        }catch (Exception e){
            log.error("BlockUtil getBlockHeight error:", e);
        }
        return blockHeight;
    }

    /**
     * 获取区块高度（总）
     * @return
     */
    public long getBlockHeight(){
        long blockHeight = 0;
        try {
            for(String channelName : channelNames){
                long temp = getBlockHeight(channelName);
                blockHeight += temp;
            }
        }catch (Exception e){
            log.error("BlockUtil getBlockHeight error:",e);
        }
        return blockHeight;
    }

    /**
     * 获取某通道交易总数
     * @param channelName
     * @return
     */
    public long getTxCount(String channelName){
        long txCount = 0;
        try {
            Channel channel = getChannel(channelName);
            if (channel == null) {
                return txCount;
            }
            long blockHeight = channel.queryBlockchainInfo().getHeight();
            for (int i=0; i<blockHeight; i++){
                txCount += channel.queryBlockByNumber(i).getEnvelopeCount();
            }
        }catch (Exception e){
            log.error("BlockUtil getTxCount error:", e);
        }
        return txCount;
    }

    /**
     * 根据channel和区块号获取区块信息
     * @param channelName 通道名称
     * @param blockNumber 区块号
     * @return
     */
    public String getBlockInfoByNumber(String channelName, long blockNumber) {
        String res = "";
        Channel channel = getChannel(channelName);
        if (channel == null) {
            return res;
        }
        try {
            JSONObject jo = new JSONObject();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long blockHeight = channel.queryBlockchainInfo().getHeight();
            BlockInfo b = channel.queryBlockByNumber(blockNumber);
            String currentHash = "";
            if(blockNumber ==blockHeight-1){
                currentHash = HexBin.encode(channel.queryBlockchainInfo().getCurrentBlockHash());
            }else{
                BlockInfo nextB = channel.queryBlockByNumber(blockNumber+1);
                currentHash = HexBin.encode(nextB.getPreviousHash());
            }
            String preHash = HexBin.encode(b.getPreviousHash());
            int txCount = b.getEnvelopeCount();
            Date time = b.getEnvelopeInfos().iterator().next().getTimestamp();
            String blockTime = df.format(time);
            Iterator<BlockInfo.EnvelopeInfo> it = b.getEnvelopeInfos().iterator();
            JSONArray txArr = new JSONArray();
            while(it.hasNext()){
                BlockInfo.EnvelopeInfo ei = it.next();
                String txId = ei.getTransactionID();
                JSONObject txjo = new JSONObject();
                txjo.put("TX_ID",txId);
                txjo.put("TX_TIME",df.format(ei.getTimestamp()));
                txArr.put(txjo);
            }
            jo.put("BLOCK_NUMBER",b.getBlockNumber());
            jo.put("CURRENT_HASH",currentHash);
            jo.put("PRE_HASH",preHash);
            jo.put("TX_COUNT",txCount);
            jo.put("BLOCK_TIME",blockTime);
            jo.put("CHANNEL",b.getChannelId());
            jo.put("TX_DATA",txArr);
            res = jo.toString();
        } catch (Exception e) {
            log.error("BlockUtil getBlockInfoByNumber error:", e);
        }
        return res;
    }

    /**
     * 获取最新5个区块信息
     * @return
     */
    public String getLastBlocksInfo(){
        String res = "";
        try{
            List<BlockInfo> list = new ArrayList<>();
            for(String channelName : channelNames){
                Channel channel = getChannel(channelName);
                if (channel == null) {
                    continue;
                }
                long blockHeight = channel.queryBlockchainInfo().getHeight();
                for(long i=blockHeight-1; i>blockHeight-6; i--){
                    BlockInfo blockInfo = channel.queryBlockByNumber(i);
                    list.add(blockInfo);
                }
            }
            Collections.sort(list, new Comparator<BlockInfo>() {
                @Override
                public int compare(BlockInfo o1, BlockInfo o2) {
                    int flag = o2.getEnvelopeInfos().iterator().next().getTimestamp().compareTo(o1.getEnvelopeInfos().iterator().next().getTimestamp());
                    return flag;
                }
            });
            JSONArray ja = new JSONArray();
            for(int i=0; i<5; i++){
                String ch = list.get(i).getChannelId();
                long blockNumber = list.get(i).getBlockNumber();
                String blockInfo = getBlockInfoByNumber(ch,blockNumber);
                JSONObject jo = new JSONObject(blockInfo);
                ja.put(jo);
            }
            res = ja.toString();
        }catch (Exception e){
            log.error("BlockUtil getLastBlocksInfo error:", e);
        }
        return res;
    }

    /**
     * 获取交易所在区块信息
     * @param channelName 通道名称
     * @param txId 交易id
     * @return
     */
    public String getBlockInfo(String channelName,String txId){
        String res = "";

        try {
            Channel channel = getChannel(channelName);
            if (channel == null) {
                return res;
            }
            JSONObject jo = new JSONObject();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long blockHeight = channel.queryBlockchainInfo().getHeight();
            channel = channel.initialize();
            BlockInfo b = channel.queryBlockByTransactionID(txId);
            String currentHash = "";
            long blockNumber = b.getBlockNumber();
            if(blockNumber ==blockHeight-1){
                currentHash = HexBin.encode(channel.queryBlockchainInfo().getCurrentBlockHash());
            }else{
                BlockInfo nextB = channel.queryBlockByNumber(blockNumber+1);
                currentHash = HexBin.encode(nextB.getPreviousHash());
            }
            String preHash = HexBin.encode(b.getPreviousHash());
            int txCount = b.getEnvelopeCount();
            Date time = b.getEnvelopeInfos().iterator().next().getTimestamp();
            String blockTime = df.format(time);
            jo.put("TX_ID",txId);
            jo.put("BLOCK_NO",blockNumber);
            jo.put("BLOCK_HEIGHT",blockHeight);
            jo.put("PRE_HASH",preHash);
            jo.put("CURRENT_HASH",currentHash);
            jo.put("TX_COUNT",txCount);
            jo.put("BLOCK_TIME",blockTime);
            jo.put("CHANNEL",b.getChannelId());
            res = jo.toString();
        } catch (Exception e) {
            log.error("BlockUtil getBlockInfo error:", e);
        }
        return res;
    }

    /**
     * 根据交易id获取交易信息
     * @param channelName 通道名称
     * @param txId 交易id
     * @return
     */
    public String getTxInfoById(String channelName, String txId){
        String res = "{\"TX_ID\":\""+txId+"\",\"CHANNEL\":\""+channelName+"\"}";
        Channel channel = getChannel(channelName);
        if (channel == null) {
            return res;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            TransactionInfo transactionInfo = channel.queryTransactionByID(txId);
            Map<String,Object> decodeTx = BlockDecoder.decodeTransaction(transactionInfo);
            String json = new Gson().toJson(decodeTx);
            JSONObject jo = new JSONObject(json);
            int endCount = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONArray("endorsements").length();
            String createrMsp = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("header").getJSONObject("signatureHeader").getString("mspId");
            long blockNumber = channel.queryBlockByTransactionID(txId).getBlockNumber();

            long timestamp = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("header").getJSONObject("channelHeader").getJSONObject("timestamp").getLong("seconds_");
            Date time = new Date(timestamp*1000l);
            String txTime = df.format(time);
            JSONArray arr = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONObject("proposalResponsePayload").getJSONObject("extension").getJSONObject("results").getJSONArray("nsRWSet");
            String chaincodeName = arr.getJSONObject(arr.length()-1).getString("namespapce");

            String payloadDetail = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").toString();

            String certContent = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("header").getJSONObject("signatureHeader").getString("idBytes");
            ByteString signature = transactionInfo.getEnvelope().getSignature();
            String sigStr = HexBin.encode(signature.toByteArray());
            ByteString payload = transactionInfo.getEnvelope().getPayload();
            String payloadStr = HexBin.encode(payload.toByteArray());

            CertificateFactory certificatefactory=CertificateFactory.getInstance("X.509");
            InputStream is = new ByteArrayInputStream(certContent.getBytes());
            X509Certificate cert = (X509Certificate)certificatefactory.generateCertificate(is);
            String name = cert.getSubjectDN().getName();
            PublicKey pk = cert.getPublicKey();
            String pkStr = HexBin.encode(pk.getEncoded());
            JSONObject retJo = new JSONObject();
            retJo.put("TX_ID",txId);
            retJo.put("CHANNEL",channelName);
            retJo.put("BLOCK_NUMBER",blockNumber);
            retJo.put("END_COUNT",endCount);
            retJo.put("CREATER_MSP",createrMsp);
            retJo.put("TX_TIME",txTime);
            retJo.put("CHAINCODE_NAME",chaincodeName);
            retJo.put("TX_SIGNATURE",sigStr);
            retJo.put("TX_PAYLOAD",payloadStr);
            retJo.put("TX_PAYLOAD_DETAIL",payloadDetail);
            retJo.put("CREATER_CERT",certContent);
            retJo.put("CREATER",name);
            retJo.put("CREATER_PK",pkStr);
            res = retJo.toString();
        }catch (Exception e){
            log.error("BlockUtil getTxInfoById error:", e);
        }

        return res;
    }

    /**
     * 获取最新5个交易信息
     * @return
     */
    public String getLastTxInfo(){
        if(log.isDebugEnabled()){
            log.debug("Block--Util--getLastTxInfo--begin");
        }
        String res = "";
        try{
            List<BlockInfo.EnvelopeInfo> list = new ArrayList<>();
            for(String channelName: channelNames){
                Channel channel = getChannel(channelName);
                if (channel == null) {
                    continue;
                }
                long blockHeight = channel.queryBlockchainInfo().getHeight();
                for(long i=blockHeight-1; i>blockHeight-6; i--){
                    BlockInfo blockInfo = channel.queryBlockByNumber(i);
                    Iterator<BlockInfo.EnvelopeInfo> txIt = blockInfo.getEnvelopeInfos().iterator();
                    while(txIt.hasNext()){
                        BlockInfo.EnvelopeInfo be = txIt.next();
                        list.add(be);
                    }
                }
            }
            Collections.sort(list, new Comparator<BlockInfo.EnvelopeInfo>() {
                @Override
                public int compare(BlockInfo.EnvelopeInfo o1, BlockInfo.EnvelopeInfo o2) {
                    int flag = o2.getTimestamp().compareTo(o1.getTimestamp());
                    return flag;
                }
            });
            JSONArray ja = new JSONArray();
            for(int i=0; i<5; i++){
                String ch = list.get(i).getChannelId();
                String txId = list.get(i).getTransactionID();
                String txInfo = getTxInfoById(ch,txId);
                if(log.isDebugEnabled()){
                    log.debug("getLastTxInfo--ch-->"+ch+"--txId-->"+txId);
                    log.debug("getLastTxInfo--txInfo-->"+txInfo);
                }
                JSONObject jo = new JSONObject(txInfo);
                ja.put(jo);
            }
            res = ja.toString();
        }catch (Exception e){
            log.error("MoniterUtil--getLastTxInfo--error-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取企业的交易数（其实是企业的追溯信息记录数，并非真实的交易数）
     * @param organizeCode 企业编码
     * @return
     */
    public long getTxCountByOrganize(String organizeCode){
        String res = "[]";
        long count = 0;
        try {//从缓存中获取企业名称和产品名称
            Map<String,Object> organizeMap = CacheUtil.getBusiBase(organizeCode);
//            String channelName = FabricCacheUtil.getChainCodeChannel((String)organizeMap.get("CHAINCODE_NAME"));
            String param = "{\"selector\":{\"$and\": [{\"ORGANIZE_CODE\":\""+organizeCode+"\"}]}}";
            res = ChaincodeManager.getInstance().queryTraceKeys(param, organizeMap);
            JSONArray arr = new JSONArray(res);
            count = arr.length();
        }catch (Exception e){
            log.error("BlockUtil getTxCountByOrganize error:",e);
        }
        return count;
    }

    /**
     * 上传区块信息到数据库
     * @param channelName 通道名称
     * @return
     */
    public String uploadBlocksByChannel(String channelName){
        String res = "failed";
        Channel channel = getChannel(channelName);
        if (channel == null) {
            return res;
        }
        try{
            log.error("uploadBlocksByChannel--begin");
            Map<String, Object> blockNumMap = moniterService.getBlockNum(channelName);
            int from = 0;
            if(blockNumMap!=null){
                from = (int)blockNumMap.get("BLOCK_NUM");
                from += 1;
            }
            log.error("uploadBlocksByChannel--from-->"+from);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long blockHeight = channel.queryBlockchainInfo().getHeight();
            for(int i=from;i<blockHeight;i++){
                BlockInfo b = channel.queryBlockByNumber(i);
                String currentHash = "";
                if(i ==blockHeight-1){
                    currentHash = HexBin.encode(channel.queryBlockchainInfo().getCurrentBlockHash());
                }else{
                    BlockInfo nextB = channel.queryBlockByNumber(i+1);
                    currentHash = HexBin.encode(nextB.getPreviousHash());
                }
                String preHash = HexBin.encode(b.getPreviousHash());
                int txCount = b.getEnvelopeCount();
                Date time = b.getEnvelopeInfos().iterator().next().getTimestamp();
                String blockTime = df.format(time);
                Iterator<BlockInfo.EnvelopeInfo> it = b.getEnvelopeInfos().iterator();
                while(it.hasNext()){
                    BlockInfo.EnvelopeInfo ei = it.next();
                    String txId = ei.getTransactionID();
                    TransactionInfo transactionInfo = channel.queryTransactionByID(txId);
                    Date time1 = ei.getTimestamp();
                    String txTime = df.format(time1);
                    Map<String,Object> decodeTx = null;
                    try{
                        decodeTx = BlockDecoder.decodeTransaction(transactionInfo);
                    }catch (Exception e){
                    }
                    Map<String,Object> txMap = new HashMap<>();
                    if (null == decodeTx) {
                        txMap.put("TX_ID",txId);
                        txMap.put("CHANNEL_NAME",channelName);
                        txMap.put("BLOCK_NUM",i);
                        txMap.put("CHAINCODE_NAME","");
                        txMap.put("TX_TIME",txTime);
                        txMap.put("TX_KEY","");
                        txMap.put("TX_VALUE","");
                        txMap.put("END_COUNT",0);
                        txMap.put("CREATOR_MSP","");
                        moniterService.insertTx(txMap);
                        continue;
                    }
                    String json = new Gson().toJson(decodeTx);
                    JSONObject jo = new JSONObject(json);
                    int endCount = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONArray("endorsements").length();
                    String createrMsp = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("header").getJSONObject("signatureHeader").getString("mspId");


                    JSONArray arr = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONObject("proposalResponsePayload").getJSONObject("extension").getJSONObject("results").getJSONArray("nsRWSet");
                    String chaincodeName = arr.getJSONObject(arr.length()-1).getString("namespapce");
                    String key = "";
                    String value = "";
                    try{
                        JSONArray arr1 = arr.getJSONObject(arr.length()-1).getJSONObject("rwset").getJSONArray("writes");
                        key = arr1.getJSONObject(arr1.length()-1).getString("key");
                        value = arr1.getJSONObject(arr1.length()-1).getString("value");
                        if (value.length()>4096) value = "";
                    }catch (Exception e){
                    }

                    txMap.put("TX_ID",txId);
                    txMap.put("CHANNEL_NAME",channelName);
                    txMap.put("BLOCK_NUM",i);
                    txMap.put("CHAINCODE_NAME",chaincodeName);
                    txMap.put("TX_TIME",txTime);
                    txMap.put("TX_KEY",key);
                    txMap.put("TX_VALUE",value);
                    txMap.put("END_COUNT",endCount);
                    txMap.put("CREATOR_MSP",createrMsp);
                    moniterService.insertTx(txMap);
                }
                Map<String, Object> blockMap = new HashMap<>();
                blockMap.put("CHANNEL_NAME",b.getChannelId());
                blockMap.put("BLOCK_NUM",b.getBlockNumber());
                blockMap.put("CURRENT_HASH",currentHash);
                blockMap.put("PRE_HASH",preHash);
                blockMap.put("TX_COUNT",txCount);
                blockMap.put("TIME",blockTime);
                moniterService.insertBlock(blockMap);

            }
            res = "success";
        }catch (Exception e){
            log.error("BlockUtil uploadBlocksByChannel channelName="+channelName+" error:", e);
        }
        return res;
    }

    /**
     * 验证签名
     * @param signature 签名
     * @param payload 原文
     * @param pk 公钥
     * @return failed：验签失败 true：验签成功
     */
    public String checkSignature(String signature, String payload, String pk){
        String res = "failed";
        try{
            Signature sig = Signature.getInstance("SHA256withECDSA");
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(HexBin.decode(pk));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            sig.initVerify(publicKey);
            sig.update(HexBin.decode(payload));
            boolean b = sig.verify(HexBin.decode(signature));
            if(b){
                res = "true";
            }else {
                res = "false";
            }
        }catch (Exception e){
            log.error("BlockUtil checkSignature error:",e);
        }

        return res;
    }

    public String scanCode(String code){
        String res = "";
        try {
            JSONObject jo = new JSONObject();
            //根据质量码获取生成码start
            String start = code.substring(0,6);
            start = start + "0001";
            String generateJson = ChaincodeManager.getInstance().getCodesHistory(start);
            JSONArray generateArray = new JSONArray(generateJson);
            String generateTxId = generateArray.getJSONObject(generateArray.length()-1).getString("tx_id");
            String generateTxInfo = getTxInfoById("trace1",generateTxId);
            JSONObject generateJo = new JSONObject(generateTxInfo);

            //根据质量码可以获取产品编码和企业编码，继而可以获取链码信息
            String chaincodeName = "qc_household_cc";
            String chaincodePath = "qchouseholdcc";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("TRACE_CODE_TYPE","1");
            map.put("CHAINCODE_NAME",chaincodeName);
            map.put("CHAINCODE_PATH",chaincodePath);

            //根据质量码可以从数据库里查到相应的START
            String traceStart = "0000000001";
            String traceJson = ChaincodeManager.getInstance().getTraceHistory(traceStart,map);
            JSONArray traceArray = new JSONArray(traceJson);
            String traceTxId = traceArray.getJSONObject(traceArray.length()-1).getString("tx_id");
            String traceTxInfo = getTxInfoById("trace5",traceTxId);
            JSONObject traceJo = new JSONObject(traceTxInfo);

            String checkJson = ChaincodeManager.getInstance().getCheckHistory("a");
            JSONArray checkArray = new JSONArray(checkJson);
            String checkTxId = checkArray.getJSONObject(checkArray.length()-1).getString("tx_id");
            String checkTxInfo = getTxInfoById("trace2",checkTxId);
            JSONObject checkJo = new JSONObject(checkTxInfo);

            String check1Json = ChaincodeManager.getInstance().getCheckHistory("b");
            JSONArray check1Array = new JSONArray(check1Json);
            String check1TxId = checkArray.getJSONObject(check1Array.length()-1).getString("tx_id");
            String check1TxInfo = getTxInfoById("trace2",check1TxId);
            JSONObject check1Jo = new JSONObject(check1TxInfo);

            JSONArray ja = new JSONArray();
            ja.put(checkJo);
            ja.put(check1Jo);

            jo.put("generate",generateJo);
            jo.put("trace",traceJo);
            jo.put("check",ja);
            res = jo.toString();
        }catch (Exception e){
            log.error("BlockUtil scanCode error:",e);
        }
        return res;
    }

}
