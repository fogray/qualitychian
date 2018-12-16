package com.inspur.fabric.client;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.loushang.waf.ComponentFactory;

import com.google.gson.Gson;
import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.moniter.IMoniterService;
import com.inspur.fabric.pub.cache.FabricCacheUtil;
import com.inspur.pub.cache.CacheUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/1/16
 */
public class MoniterUtil {
    private static final Log log = LogFactory.getLog(MoniterUtil.class);

    private static ConcurrentHashMap<String, MoniterUtil> instanceCache = new ConcurrentHashMap<String, MoniterUtil>();
    private static final IMoniterService moniterService = (IMoniterService) ComponentFactory.getBean("moniterService");
    
    private ClientHelper clientHelper;
    private ClientConfig config;

    private static MoniterUtil instance;
    public static MoniterUtil getInstance(){
        if(null == instance){
            synchronized (MoniterUtil.class){
                if(null == instance){
                    try {
                        instance = new MoniterUtil();
                    } catch (Exception e) {
                        log.error("MoniterUtil--getInstance-->",e);
                        instance = null;
                    }
                }
            }
        }
        return instance;
    }
    public static MoniterUtil getInstance(String channelName){
    	String key = channelName;
        if(!instanceCache.containsKey(key)){
            synchronized (MoniterUtil.class){
                if (!instanceCache.containsKey(key)){
                    try {
                        new MoniterUtil(channelName);
                    } catch (Exception e) {
                        log.error("MoniterUtil--getInstance-->channelName="+channelName,e);
                        instance = null;
                    }
                }
            }
        }
        return instanceCache.get(key);
    }

    private HFClient client;
    private Map<String,Channel> channelMap = new HashMap<String,Channel>();
    public MoniterUtil() throws Exception{
        if(log.isDebugEnabled()){
            log.debug("MoniterUtil--begin");
        }
        clientHelper = new ClientHelper();
        config = ClientConfig.getConfig();
        File loadFile = new File(this.getClass().getResource("/").getPath()+"/fabric.properties").getAbsoluteFile();
        FileInputStream configProps = new FileInputStream(loadFile);
        Properties properties = new Properties();
        properties.load(configProps);
        String[] channels = properties.getProperty("channels").split(",");
        for(int i=0; i<channels.length; i++){
            String channelName = channels[i];
            try {
                Channel c = clientHelper.getChannel("peerOrg1", channelName);
                channelMap.put(channelName,c);
            } catch(Exception e) {
                log.error("MoniterUtil 实例化channel["+channelName+"]中出现错误", e);
            }
        }
        this.client = clientHelper.getHFClient();
//        this.channel = channelMap.get("trace1");
        BaseOrg org = clientHelper.getOrg("peerOrg1");
        this.client.setUserContext(org.getPeerAdmin());
        if(log.isDebugEnabled()){
            log.debug("MoniterUtil--end");
        }
    }
    public MoniterUtil(String channelName) throws Exception{
        if(log.isDebugEnabled()){
            log.debug("MoniterUtil--begin");
        }
        clientHelper = new ClientHelper();
        config = ClientConfig.getConfig();
        File loadFile = new File(this.getClass().getResource("/").getPath()+"/fabric.properties").getAbsoluteFile();
        FileInputStream configProps = new FileInputStream(loadFile);
        Properties properties = new Properties();
        properties.load(configProps);
        Channel c = clientHelper.getChannel("peerOrg1", channelName);
        channelMap.put(channelName,c);
        this.client = clientHelper.getHFClient();
//        this.channel = channelMap.get("trace1");
        BaseOrg org = clientHelper.getOrg("peerOrg1");
        this.client.setUserContext(org.getPeerAdmin());
        instanceCache.put(channelName, this);
        if(log.isDebugEnabled()){
            log.debug("MoniterUtil--end");
        }
    }
    
    /**
     * 获取组织数
     * @return
     */
    public int getOrgCount(){
        return config.getOrgsCollection().size();
    }

    /**
     * 获取节点数
     * @return
     */
    public int getPeerCount(){
        int peerCount = 0;
        Collection<BaseOrg> orgs = config.getOrgsCollection();
        for(BaseOrg org : orgs){
            peerCount += org.getPeerNames().size();
        }
        return peerCount;
    }

    /**
     * 获取通道数
     * @return
     */
    public int getChannelCount(){
        int count = 0;
        try {
            count = channelMap.keySet().size();
        }catch (Exception e){
            log.error("MoniterUtil--getChannelCount--err-->",e);
        }
        return count;
    }

    /**
     * 获取链码数
     * @return
     */
    public int getChaincodeCount(){
        int chaincodeCount = 0;
        try {
            Set<String> channelNames = channelMap.keySet();
            Iterator<String> iterator = channelNames.iterator();
            while (iterator.hasNext()){
                String channelName = iterator.next();
                Channel channel = getChannel(channelName);
                if (channel == null) {
                    return chaincodeCount;
                }
                Iterator<Peer> it = channel.getPeers().iterator();
                while (it.hasNext()){
                    Peer peer = it.next();
                    if(peer.getName().equals("peer0.org1.chains.cloudchain.cn")){
                        chaincodeCount += channel.queryInstantiatedChaincodes(peer).size();
                    }
                }
            }
        }catch (Exception e){
            log.error("MoniterUtil--getChaincodeCount--err-->",e);
        }
        return chaincodeCount;
    }

    /**
     * 获取区块高度（总）
     * @return
     */
    public long getBlockHeight(){
        long blockHeight = 0;
        try {
            Set<String> channelNames = channelMap.keySet();
            Iterator<String> iterator = channelNames.iterator();
            while (iterator.hasNext()){
                String channelName = iterator.next();
                Channel channel = getChannel(channelName);
                if (channel == null) {
                    return blockHeight;
                }
                blockHeight += channel.queryBlockchainInfo().getHeight();
            }
        }catch (Exception e){
            log.error("MoniterUtil--getBlockHeight--err-->",e);
        }
        return blockHeight;
    }

    /**
     * 获取交易数（总）
     * @return
     */
    public long getTxCount(){
        long txCount = 0;
        try {
            Set<String> channelNames = channelMap.keySet();
            Iterator<String> iterator = channelNames.iterator();
            while (iterator.hasNext()){
                String channelName = iterator.next();
                Channel channel = getChannel(channelName);
                if (channel == null) {
                    continue;
                }
                long blockHeight = channel.queryBlockchainInfo().getHeight();
                for (int i=0; i<blockHeight; i++){
                    txCount += channel.queryBlockByNumber(i).getEnvelopeCount();
                }
            }
        }catch (Exception e){
            log.error("MoniterUtils getTxCount error.", e);
        }

        return txCount;
    }

    /**
     * 获取某通道区块高度
     * @param channelName 通道名称
     * @return
     */
    public long getBlockHeight(String channelName){
        long blockHeight = 0;
        Channel channel = getChannel(channelName);
        if (channel == null) {
            return blockHeight;
        }
        try {
            blockHeight += channel.queryBlockchainInfo().getHeight();
        }catch (Exception e){
            log.error("MoniterUtils getBlockHeight error.", e);
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
            log.error("MoniterUtil getTxCount error.", e);
        }
        return txCount;
    }

    /**
     * 获取某通道内某组织的区块高度
     * @param peerOrg 组织名
     * @param channelName 通道名
     * @return
     */
    public long getOrgBlockHeight(String peerOrg,String channelName){
        long blockHeight = 0;
        try {
            Channel channel = clientHelper.getChannel(peerOrg, channelName);
            blockHeight = channel.queryBlockchainInfo().getHeight();
        }catch (Exception e){
            log.error("MoniterUtil getOrgBlockHeight error.", e);
        }
        return blockHeight;
    }

    /**
     * 获取最新5个区块信息
     * @return
     */
    public String getLastBlocksInfo(){
        String res = "";
        try{
            Set<String> channelNames = channelMap.keySet();
            Iterator<String> iterator = channelNames.iterator();
            List<BlockInfo> list = new ArrayList<>();
            while (iterator.hasNext()){
                String channelName = iterator.next();
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
                JSONObject jo = JSONObject.fromObject(blockInfo);
                ja.add(jo);
            }
            res = ja.toString();
        }catch (Exception e){
            log.error("MoniterUtil getLastBlocksInfo error.", e);
        }
        return res;
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
                currentHash = Hex.encodeHexString(channel.queryBlockchainInfo().getCurrentBlockHash());
            }else{
                BlockInfo nextB = channel.queryBlockByNumber(blockNumber+1);
                currentHash = Hex.encodeHexString(nextB.getPreviousHash());
            }
            String preHash = Hex.encodeHexString(b.getPreviousHash());
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
                txArr.add(txjo);
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
            log.error("MoniterUtil getBlockInfoByNumber error[channel="+channelName+"].", e);
        }
        return res;
    }

    /**
     * 获取交易所在区块信息
     * @param txId 交易id
     * @param channelName 通道名称
     * @return
     */
    public String getBlockInfo(String txId, String channelName){
        String res = "";
        Channel channel = getChannel(channelName);
        if (channel == null) {
            return res;
        }
        try {
            JSONObject jo = new JSONObject();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long blockHeight = channel.queryBlockchainInfo().getHeight();
            channel = channel.initialize();
            BlockInfo b = channel.queryBlockByTransactionID(txId);
            String currentHash = "";
            long blockNumber = b.getBlockNumber();
            if(blockNumber ==blockHeight-1){
                currentHash = Hex.encodeHexString(channel.queryBlockchainInfo().getCurrentBlockHash());
            }else{
                BlockInfo nextB = channel.queryBlockByNumber(blockNumber+1);
                currentHash = Hex.encodeHexString(nextB.getPreviousHash());
            }
            String preHash = Hex.encodeHexString(b.getPreviousHash());
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
           log.error("MoniterUtil getBlockInfo error[channel="+channelName+"].", e);
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
            JSONObject jo = JSONObject.fromObject(json);
            int endCount = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONArray("endorsements").size();
            String createrMsp = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("header").getJSONObject("signatureHeader").getString("mspId");
            long blockNumber = channel.queryBlockByTransactionID(txId).getBlockNumber();

            long timestamp = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("header").getJSONObject("channelHeader").getJSONObject("timestamp").getLong("seconds_");
            Date time = new Date(timestamp*1000l);
            String txTime = df.format(time);
            JSONArray arr = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONObject("proposalResponsePayload").getJSONObject("extension").getJSONObject("results").getJSONArray("nsRWSet");
            String chaincodeName = arr.getJSONObject(arr.size()-1).getString("namespapce");

            JSONObject retJo = new JSONObject();
            retJo.put("TX_ID",txId);
            retJo.put("CHANNEL",channelName);
            retJo.put("BLOCK_NUMBER",blockNumber);
            retJo.put("END_COUNT",endCount);
            retJo.put("CREATER_MSP",createrMsp);
            retJo.put("TX_TIME",txTime);
//            retJo.put("TIMESTAMP",timestamp);
            retJo.put("CHAINCODE_NAME",chaincodeName);
            res = retJo.toString();
        }catch (Exception e){
            log.error("MoniterUtil--getTxInfoById--error-->", e);
        }

        return res;
    }

    /**
     * 获取最新5个交易信息
     * @return
     */
    public String getLastTxInfo(){
        if(log.isDebugEnabled()){
            log.debug("MoniterUtil--getLastTxInfo--begin");
        }
        String res = "";
        try{
            Set<String> channelNames = channelMap.keySet();
            Iterator<String> iterator = channelNames.iterator();
            List<BlockInfo.EnvelopeInfo> list = new ArrayList<>();
            while (iterator.hasNext()){
                String channelName = iterator.next();
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
                JSONObject jo = JSONObject.fromObject(txInfo);
                ja.add(jo);
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
     * @param peerOrg
     * @param organizeCode
     * @return
     */
    public long getTxCountByOrganize(String peerOrg, String organizeCode){
        String res = "[]";
        long count = 0;
        try {//从缓存中获取企业名称和产品名称
			Map<String,Object> organizeMap = CacheUtil.getBusiBase(organizeCode);
			String channelName = FabricCacheUtil.getChainCodeChannel((String)organizeMap.get("CHAINCODE_NAME"));
        	String param = "{\"selector\":{\"$and\": [{\"ORGANIZE_CODE\":\""+organizeCode+"\"}]}}";
        	res = ChaincodeUtil.getInstance().queryTraceKeys(param, channelName, organizeMap);
            JSONArray arr = JSONArray.fromObject(res);
            count = arr.size();
        }catch (Exception e){
            log.error("MoniterUtil--getTxCountByOrganize--error-->",e);
        }
        return count;
    }

    /**
     * 上传区块信息到数据库
     * @param channelName 通道名称
     * @param from 上传起始区块号（判断从哪个区块开始上传）
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
                    currentHash = Hex.encodeHexString(channel.queryBlockchainInfo().getCurrentBlockHash());
                }else{
                    BlockInfo nextB = channel.queryBlockByNumber(i+1);
                    currentHash = Hex.encodeHexString(nextB.getPreviousHash());
                }
                String preHash = Hex.encodeHexString(b.getPreviousHash());
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
                    JSONObject jo = JSONObject.fromObject(json);
                    int endCount = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONArray("endorsements").size();
                    String createrMsp = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("header").getJSONObject("signatureHeader").getString("mspId");


                    JSONArray arr = jo.getJSONObject("transactionEnvelope").getJSONObject("payload").getJSONObject("data").getJSONArray("actions").getJSONObject(0).getJSONObject("payload").getJSONObject("action").getJSONObject("proposalResponsePayload").getJSONObject("extension").getJSONObject("results").getJSONArray("nsRWSet");
                    String chaincodeName = arr.getJSONObject(arr.size()-1).getString("namespapce");
                    String key = "";
                    String value = "";
                    try{
                        JSONArray arr1 = arr.getJSONObject(arr.size()-1).getJSONObject("rwset").getJSONArray("writes");
                        key = arr1.getJSONObject(arr1.size()-1).getString("key");
                        value = arr1.getJSONObject(arr1.size()-1).getString("value");
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
            log.error("uploadBlocks--error-->channelName="+channelName, e);
        }
        return res;
    }
    
    public Channel getChannel(String channelName) {
    	Channel channel = null;
        if (!channelMap.containsKey(channelName)) {
			try {
				channel = clientHelper.getChannel("peerOrg1", channelName);
	            channelMap.put(channelName, channel);
			} catch (Exception e) {
			}
        }
    	return channelMap.get(channelName);
    }
}
