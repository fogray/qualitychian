package com.inspur.fabric.sdk.client;

import com.inspur.fabric.pub.cache.FabricCacheUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/19
 */
public class CheckChaincode extends ChaincodeImpl {
    private static final Log log = LogFactory.getLog(CheckChaincode.class);
    private static ConcurrentHashMap<String, CheckChaincode> instanceCache = new ConcurrentHashMap<>();
    public static CheckChaincode getInstance(String user){
        String key = user;
        if(!instanceCache.containsKey(key)){
            synchronized (CheckChaincode.class){
                if (!instanceCache.containsKey(key)){
                    String chaincodeName = "qc_check_cc";
                    String chaincodePath = "qccheckcc";
                    String channelName = FabricCacheUtil.getChainCodeChannel(chaincodeName);
                    String version = FabricCacheUtil.getChaincodeVersion(chaincodeName);
                    if (channelName==null || "".equals(channelName) || version==null || "".equals(version)){
                        throw new RuntimeException("chaincode "+chaincodeName+" does not exist!");
                    }
                    instanceCache.put(key, new CheckChaincode(chaincodeName,chaincodePath,version,channelName,user));
                }
            }
        }
        return instanceCache.get(key);
    }

    private CheckChaincode(String chaincodeName, String chaincodePath, String version, String channelName, String user){
        super(chaincodeName,chaincodePath,version,channelName,user);
    }

    public String setCheckInfo(String key,String value) throws Exception {
        return invoke(new String[]{"setCheckInfo",key,value});
    }

    public String getCheckInfo(String key) throws Exception {
        return query(new String[]{"getCheckInfo",key});
    }

    public String getCheckInfoByParam(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String batchCode = jo.getString("BATCH_ID");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"BATCH_ID\": \""+batchCode+"\"}]}}";
        String keys = queryCheckKeys(param);
        JSONArray arr = new JSONArray(keys);
        JSONArray resArr = new JSONArray();
        for (int i=0; i<arr.length(); i++){
            JSONObject resJo = new JSONObject();
            String key = arr.getString(i);
            String checkJson = query(new String[]{"getCheckInfo",key});
            JSONObject checkJo = new JSONObject(checkJson);
            resJo.put("key",key);
            resJo.put("data",checkJo);
            resArr.put(resJo);
        }
        return resArr.toString();
    }

    public String queryCheckKeys(String query) throws Exception {
        return query(new String[]{"queryCheckKeys", query});
    }

    public String deleteCheckInfo(String key) throws Exception {
        return invoke(new String[]{"deleteCheckInfo", key});
    }

    public String getHistory(String key) throws Exception {
        return query(new String[]{"getHistory", key});
    }

}
