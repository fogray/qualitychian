package com.inspur.fabric.sdk.client;

import com.inspur.fabric.sdk.base.FabricManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/25
 */
public class DeliveryChaincode extends ChaincodeImpl {
    private static final Log log = LogFactory.getLog(DeliveryChaincode.class);
    private static ConcurrentHashMap<String, DeliveryChaincode> instanceCache = new ConcurrentHashMap<String, DeliveryChaincode>();
    public static DeliveryChaincode getInstance(String channelName, String user){
        String key = user+"-"+channelName;
        if(!instanceCache.containsKey(key)){
            synchronized (CheckChaincode.class){
                if (!instanceCache.containsKey(key)){
                    String chaincodeName = FabricManager.getConfig().getProperty("circulation_chaincode_name");
                    String chaincodePath = FabricManager.getConfig().getProperty("circulation_chaincode_path");
                    String version = FabricManager.getConfig().getProperty("circulation_chaincode_version");
                    instanceCache.put(key, new DeliveryChaincode(chaincodeName,chaincodePath,version,channelName,user));
                }
            }
        }
        return instanceCache.get(key);
    }

    private DeliveryChaincode(String chaincodeName, String chaincodePath, String version, String channelName, String user) {
        super(chaincodeName,chaincodePath,version,channelName,user);
    }

    public String setTraceInfo(String key, String value) throws Exception {
        return invoke(new String[]{"setTraceInfo",key,value});
    }

    public String getTraceInfo(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String traceCode = jo.getString("TRACE_CODE");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"TRACE_CODE_JSON\": {\"$all\":[\""+traceCode+"\"]}}]}}";
        String keys = queryTraceKeys(param);
        JSONArray arr = new JSONArray(keys);
        String key = arr.getString(0);
        return query(new String[]{"getTraceInfo",key});
    }

    public String queryTraceKeys(String query) throws Exception {
        return query(new String[]{"queryTraceKeys",query});
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }

    public String getHistory(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String traceCode = jo.getString("TRACE_CODE");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"TRACE_CODE_JSON\": {\"$all\":[\""+traceCode+"\"]}}]}}";
        String keys = queryTraceKeys(param);
        JSONArray arr = new JSONArray(keys);
        String key = arr.getString(0);
        return query(new String[]{"getHistory",key});
    }

}
