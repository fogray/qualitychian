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
public class MaterialChaincode extends ChaincodeImpl {
    private static final Log log = LogFactory.getLog(MaterialChaincode.class);
    private static ConcurrentHashMap<String, MaterialChaincode> instanceCache = new ConcurrentHashMap<String, MaterialChaincode>();
    public static MaterialChaincode getInstance(String channelName, String user){
        String key = user+"-"+channelName;
        if(!instanceCache.containsKey(key)){
            synchronized (CheckChaincode.class){
                if (!instanceCache.containsKey(key)){
                    String chaincodeName = FabricManager.getConfig().getProperty("material_chaincode_name");
                    String chaincodePath = FabricManager.getConfig().getProperty("material_chaincode_path");
                    String version = FabricManager.getConfig().getProperty("material_chaincode_version");
                    instanceCache.put(key, new MaterialChaincode(chaincodeName,chaincodePath,version,channelName,user));
                }
            }
        }
        return instanceCache.get(key);
    }
    private MaterialChaincode(String chaincodeName, String chaincodePath, String version, String channelName, String user) {
        super(chaincodeName,chaincodePath,version,channelName,user);
    }

    public String setMaterialInfo(String key, String value) throws Exception {
        if(log.isDebugEnabled()){
            log.debug("setMaterialInfo--key-->"+key);
            log.debug("setMaterialInfo--value-->"+value);
        }
        return invoke(new String[]{"setMaterialInfo",key,value});
    }

    public String getMaterialInfo(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String batchCode = jo.getString("MATERIAL_BATCH");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"MATERIAL_BATCH\": \""+batchCode+"\"}]}}";
        String keys = queryMaterialKeys(param);
        JSONArray arr = new JSONArray(keys);
        String key = arr.getString(0);
        return query(new String[]{"getMaterialInfo",key});
    }

    public String queryMaterialKeys(String query) throws Exception {
        return query(new String[]{"queryMaterialKeys",query});
    }

    public String deleteMaterialInfo(String key) throws Exception {
        return invoke(new String[]{"deleteMaterialInfo",key});
    }

    public String getHistory(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String batchCode = jo.getString("MATERIAL_BATCH");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"MATERIAL_BATCH\": \""+batchCode+"\"}]}}";
        String keys = queryMaterialKeys(param);
        JSONArray arr = new JSONArray(keys);
        String key = arr.getString(0);
        return query(new String[]{"getHistory",key});
    }


}
