package com.inspur.fabric.client;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.fabric.base.BaseOrg;

/**
 * 类型3(trace_code_type=3)生产溯源信息上链（例如：格力的溯源数据）
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/26
 */
public class Trace3ChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(Trace3ChainCode.class);
    
    private static ConcurrentHashMap<String, Trace3ChainCode> instanceCache = new ConcurrentHashMap<String, Trace3ChainCode>();
    private String channelName;
    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;
    public static Trace3ChainCode getInstance(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
    	String key = peerOrg+channelName+chaincodeName+chaincodePath+chaincodeVer;
        if(!instanceCache.containsKey(key)){
            synchronized (Trace3ChainCode.class){
                if (!instanceCache.containsKey(key)){
                    new Trace3ChainCode(peerOrg, channelName, chaincodeName, chaincodePath, chaincodeVer);
                }
            }
        }
        return instanceCache.get(key);
    }
    
    public Trace3ChainCode(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
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
            log.error("Trace3ChainCode--init--err-->",e);
        }
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo", key, value, "trace1", "3"});
        }catch (Exception e){
            log.error("Trace3ChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }
    
    public String getTraceInfo(String key){
        String res = "";
        try {
//            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
//            String json = query(new String[]{"queryTraceKeys",param});
//            if(log.isDebugEnabled()){
//                log.debug("Trace3ChainCode--getTraceInfo--json-->"+json);
//            }
//            JSONArray arr = new JSONArray(json);
//            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key});
        } catch (Exception e) {
            log.error("Trace3ChainCode getTraceInfo--err-->",e);
        }
        return res;
    }
    public String getTraceHistory(String key){
        String res = "";
        try {
//            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
//            String json = query(new String[]{"queryTraceKeys",param});
//            if(log.isDebugEnabled()){
//                log.debug("Trace3ChainCode--getTraceHistory--json-->"+json);
//            }
//            JSONArray arr = new JSONArray(json);
//            String key1 = arr.getString(0);
            res = query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("Trace3ChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }
    
    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }
}
