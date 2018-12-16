package com.inspur.fabric.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.fabric.base.BaseOrg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/3/27
 */
public class MaterialChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(MaterialChainCode.class);
    private static ConcurrentHashMap<String, MaterialChainCode> instanceCache = new ConcurrentHashMap<String, MaterialChainCode>();
    private String channelName;
    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;
    
    public static MaterialChainCode getInstance(String peerOrg, String channelName){
    	String key = peerOrg+channelName;
        if(!instanceCache.containsKey(key)){
            synchronized (MaterialChainCode.class){
                if (!instanceCache.containsKey(key)){
                    new MaterialChainCode(peerOrg, channelName);
                }
            }
        }
        return instanceCache.get(key);
    }
    
    public MaterialChainCode(String peerOrg, String channelName){
        init(peerOrg);
    }
    @Override
    protected void init(String peerOrg) {
        try {
        	Properties properties = new Properties();
            File loadFile;
            FileInputStream fis;
            loadFile = new File(this.getClass().getResource("/").getPath()+"/fabric.properties").getAbsoluteFile();
            fis = new FileInputStream(loadFile);
            properties.load(fis);
            this.channelName = properties.getProperty("material_chaincode_name","qc_material_cc");
            this.chaincodePath = properties.getProperty("material_chaincode_path","qcmaterialcc");
            this.chaincodeVersion = properties.getProperty("material_chaincode_version","1.0");
            
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(this.chaincodeName,this.chaincodePath,this.chaincodeVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("MaterialChainCode--init--err-->",e);
        }
    }

    public String setMaterialInfo(String key, String value) throws Exception {
        if(log.isDebugEnabled()){
            log.debug("setMaterialInfo--key-->"+key);
            log.debug("setMaterialInfo--value-->"+value);
        }
        return invoke(new String[]{"setMaterialInfo",key,value});
    }

    public String getMaterialInfo(String json) throws Exception {
        JSONObject jo = JSONObject.fromObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String batchCode = jo.getString("MATERIAL_BATCH");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"MATERIAL_BATCH\": \""+batchCode+"\"}]}}";
        String keys = queryMaterialKeys(param);
        JSONArray arr = JSONArray.fromObject(keys);
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
        JSONObject jo = JSONObject.fromObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String batchCode = jo.getString("MATERIAL_BATCH");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"MATERIAL_BATCH\": \""+batchCode+"\"}]}}";
        String keys = queryMaterialKeys(param);
        JSONArray arr = JSONArray.fromObject(keys);
        String key = arr.getString(0);
        return query(new String[]{"getHistory",key});
    }
}
