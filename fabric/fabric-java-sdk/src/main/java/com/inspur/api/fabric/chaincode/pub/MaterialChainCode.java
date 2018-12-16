package com.inspur.api.fabric.chaincode.pub;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/3/27
 */
public class MaterialChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(MaterialChainCode.class);
    private static MaterialChainCode instance;
    public static MaterialChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (MaterialChainCode.class){
                if (null==instance){
                    instance = new MaterialChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public MaterialChainCode(String peerOrg){
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
            String channelName = properties.getProperty("material_chaincode_channel","trace2");
            String chainCodeName = properties.getProperty("material_chaincode_name","qc_material_cc");
            String chainCodePath = properties.getProperty("material_chaincode_path","qcmaterialcc");
            String ccVersion = properties.getProperty("material_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("MaterialChainCode--init--err-->",e);
            instance = null;
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
