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
public class Trace2Chaincode extends ChaincodeImpl {
    private static final Log log = LogFactory.getLog(Trace2Chaincode.class);
    private static ConcurrentHashMap<String, Trace2Chaincode> instanceCache = new ConcurrentHashMap<String, Trace2Chaincode>();
    public static Trace2Chaincode getInstance(String chaincodeName, String chaincodePath, String user){
        String key = user+"-"+chaincodeName;
        if(!instanceCache.containsKey(key)){
            synchronized (CheckChaincode.class){
                if (!instanceCache.containsKey(key)){
                    String channelName = FabricCacheUtil.getChainCodeChannel(chaincodeName);
                    String version = FabricCacheUtil.getChaincodeVersion(chaincodeName);
                    if (channelName==null || "".equals(channelName) || version==null || "".equals(version)){
                        throw new RuntimeException("chaincode "+chaincodeName+" does not exist!");
                    }
                    instanceCache.put(key, new Trace2Chaincode(chaincodeName,chaincodePath,version,channelName,user));
                }
            }
        }
        return instanceCache.get(key);
    }
    private Trace2Chaincode(String chaincodeName, String chaincodePath, String version, String channelName, String user) {
        super(chaincodeName, chaincodePath, version, channelName, user);
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo",key,value,"trace1", "2"});
        }catch (Exception e){
            log.error("Trace2ChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfo(String key) {
        String res = "";
        try {
            res = query(new String[]{"getTraceInfo",key});
        } catch(Exception e) {
            log.error("Trace2ChainCode--getTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfoByCode(String productCode, String barCode) {
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\":[{\"PRODUCT_CODE\":\""+productCode+"\"},{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\""+barCode+"\"]}}}}]}}";
            String keys = queryTraceKeys(param);
            JSONArray arr = new JSONArray(keys);
            String key = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key});
        } catch(Exception e) {
            log.error("Trace2ChainCode--getTraceInfo--err-->",e);
        }
        return res;
    }

    public String searchCode(String code){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\":[{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\"" + code + "\"]}}}}]}}";
            String keys = queryTraceKeys(param);
            JSONArray arr = new JSONArray(keys);
            String key = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key});
        }catch (Exception e){
            log.error("Trace2ChainCode--searchCode--err-->",e);
        }
        return res;
    }

    public String queryTraceKeys(String query) throws Exception {
        return query(new String[]{"queryTraceKeys",query});
    }

    public String getTraceHistory(String key) {
        String res = "";
        try {
//            JSONObject jo = new JSONObject(json);
//            String productCode = jo.getString("PRODUCT_CODE");
//            String barCode = jo.getString("BAR_CODE");
//            String param = "{\"selector\":{\"$and\":[{\"PRODUCT_CODE\":\""+productCode+"\"},{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\""+barCode+"\"]}}}}]}}";
//            String keys = queryTraceKeys(param);
//            JSONArray arr = new JSONArray(keys);
//            String key = arr.getString(0);
            res = query(new String[]{"getHistory", key});
        } catch(Exception e) {
            log.error("Trace2ChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }

    public String getTraceHistoryByCode(String productCode, String barCode) {
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\":[{\"PRODUCT_CODE\":\""+productCode+"\"},{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\""+barCode+"\"]}}}}]}}";
            String keys = queryTraceKeys(param);
            JSONArray arr = new JSONArray(keys);
            String key = arr.getString(0);
            res = query(new String[]{"getHistory", key});
        } catch(Exception e) {
            log.error("Trace2ChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }
    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }

}
