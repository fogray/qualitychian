package com.inspur.fabric.sdk.client;

import com.inspur.fabric.pub.cache.FabricCacheUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/25
 */
public class Trace1Chaincode extends ChaincodeImpl {
    private static final Log log = LogFactory.getLog(Trace1Chaincode.class);
    private static ConcurrentHashMap<String, Trace1Chaincode> instanceCache = new ConcurrentHashMap<String, Trace1Chaincode>();
    public static Trace1Chaincode getInstance(String chaincodeName, String chaincodePath, String user){
        String key = user+"-"+chaincodeName;
        if(!instanceCache.containsKey(key)){
            synchronized (CheckChaincode.class){
                if (!instanceCache.containsKey(key)){
                    String channelName = FabricCacheUtil.getChainCodeChannel(chaincodeName);
                    String version = FabricCacheUtil.getChaincodeVersion(chaincodeName);
                    if (channelName==null || "".equals(channelName) || version==null || "".equals(version)){
                        throw new RuntimeException("chaincode "+chaincodeName+" does not exist!");
                    }
                    instanceCache.put(key, new Trace1Chaincode(chaincodeName,chaincodePath,version,channelName,user));
                }
            }
        }
        return instanceCache.get(key);
    }
    private Trace1Chaincode(String chaincodeName, String chaincodePath, String version, String channelName, String user) {
        super(chaincodeName, chaincodePath, version, channelName, user);
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo", key,value,"trace1", "1"});
        }catch (Exception e){
            log.error("Trace1Chaincode--setTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
            res = query(new String[]{"getTraceInfo", key});
        } catch (Exception e) {
            log.error("Trace1Chaincode--getTraceInfo--err-->",e);
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
                log.debug("Trace1Chaincode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo", key1});
        } catch (Exception e) {
            log.error("Trace1Chaincode--getTraceInfo--err-->",e);
        }
        return res;
    }

    public String queryTraceKeys(String param) {
        String res = "[]";
        try {
            res = query(new String[]{"queryTraceKeys",param});
        }catch (Exception e){
            log.error("Trace1Chaincode--queryTraceKeys--err-->",e);
        }
        return res;
    }

    public String getTraceHistory(String key){
        String res = "";
        try {
            res = query(new String[]{"getHistory",key});
        }catch (Exception e){
            log.error("Trace1Chaincode--getTraceHistory--err-->",e);
        }
        return res;
    }

    public String getTraceHistoryForType1(String traceCode){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+traceCode+"\"}}, {\"END\": {\"$gte\": \""+traceCode+"\"}}]}}";
            String json = queryTraceKeys(param);
            if(log.isDebugEnabled()){
                log.debug("Trace1Chaincode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getHistory",key1});
        } catch (Exception e) {
            log.error("Trace1Chaincode--getTraceHistoryForType1--err-->",e);
        }
        return res;
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }
}
