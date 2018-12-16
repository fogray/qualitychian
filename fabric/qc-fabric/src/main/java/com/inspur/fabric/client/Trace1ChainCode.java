package com.inspur.fabric.client;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.fabric.base.BaseOrg;

import net.sf.json.JSONArray;

/**
 * 类型1（trace_code_type=1）生产溯源信息上链（格力、家电、家居、建材等除阿胶、浪潮信息外的溯源数据）
 * @author yanghaiyong
 * @description
 * @date 2017/12/26
 */
public class Trace1ChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(Trace1ChainCode.class);
    
    private static ConcurrentHashMap<String, Trace1ChainCode> instanceCache = new ConcurrentHashMap<String, Trace1ChainCode>();
    private String channelName;
    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;
    public static Trace1ChainCode getInstance(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
        String key = peerOrg+channelName+chaincodeName+chaincodePath+chaincodeVer;
        if(!instanceCache.containsKey(key)){
            synchronized (Trace1ChainCode.class){
                if (!instanceCache.containsKey(key)){
                    new Trace1ChainCode(peerOrg, channelName, chaincodeName, chaincodePath, chaincodeVer);
                }
            }
        }
        return instanceCache.get(key);
    }
    
    public Trace1ChainCode(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
    	this.channelName = channelName;
    	this.chaincodeName = chaincodeName;
    	this.chaincodePath = chaincodePath;
    	this.chaincodeVersion = chaincodeVer;
        init(peerOrg);
    }
    @Override
    protected void init(String peerOrg) {
        try {
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(this.chaincodeName,this.chaincodePath,this.chaincodeVersion);
            
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
            
            String key = peerOrg + this.channelName + this.chaincodeName + this.chaincodePath + this.chaincodeVersion;
            instanceCache.put(key, this);
        } catch (Exception e) {
            log.error("Trace1ChainCode--init--err-->",e);
        }
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo", key,value,"trace1", "1"});
        }catch (Exception e){
            log.error("Trace1ChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
//            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
//            String json = queryTraceKeys(param);
//            if(log.isDebugEnabled()){
//                log.debug("Trace1ChainCode--getTraceInfo--json-->"+json);
//            }
//            JSONArray arr = JSONArray.fromObject(json);
//            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo", key});
        } catch (Exception e) {
            log.error("Trace1ChainCode--getTraceInfo--err-->",e);
        }
        return res;
    }

    /**
     * 根据质量链码查询生产溯源信息
     * @param traceKeysParam   queryTraceKeys(traceKeysParam)
     * @return
     */
    public String getTraceInfoForType1(String traceCode){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+traceCode+"\"}}, {\"END\": {\"$gte\": \""+traceCode+"\"}}]}}";
            String json = queryTraceKeys(param);
            if(log.isDebugEnabled()){
                log.debug("Trace1ChainCode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = JSONArray.fromObject(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo", key1});
        } catch (Exception e) {
            log.error("Trace1ChainCode--getTraceInfo--err-->",e);
        }
        return res;
    }
    
    public String queryTraceKeys(String param) {
        String res = "[]";
        try {
            res = query(new String[]{"queryTraceKeys",param});
        }catch (Exception e){
            log.error("Trace1ChainCode--queryTraceKeys--err-->",e);
        }
        return res;
    }

    public String getTraceHistory(String key){
        String res = "";
        try {
            res = query(new String[]{"getHistory",key});
        }catch (Exception e){
            log.error("Trace1ChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }
}
