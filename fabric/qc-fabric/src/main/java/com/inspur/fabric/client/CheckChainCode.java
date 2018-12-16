package com.inspur.fabric.client;

import com.inspur.fabric.base.BaseOrg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/3/27
 */
public class CheckChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(CheckChainCode.class);
    private static CheckChainCode instance;
    public static CheckChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (CheckChainCode.class){
                if (null==instance){
                    instance = new CheckChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public CheckChainCode(String peerOrg){
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
            String channelName = properties.getProperty("check_chaincode_channel","trace2");
            String chainCodeName = properties.getProperty("check_chaincode_name","qc_check_cc");
            String chainCodePath = properties.getProperty("check_chaincode_path","qccheckcc");
            String ccVersion = properties.getProperty("check_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("CheckChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String setCheckInfo(String key,String value) throws Exception {
        return invoke(new String[]{"setCheckInfo",key,value});
    }

    public String getCheckInfo(String json) throws Exception {
        JSONObject jo = JSONObject.fromObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String batchCode = jo.getString("BATCH_ID");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"BATCH_ID\": \""+batchCode+"\"}]}}";
        String keys = queryCheckKeys(param);
        JSONArray arr = JSONArray.fromObject(keys);
        String key = arr.getString(0);
        return query(new String[]{"getCheckInfo",key});
    }

    public String queryCheckKeys(String query) throws Exception {
        return query(new String[]{"queryCheckKeys", query});
    }

    public String deleteCheckInfo(String key) throws Exception {
        return invoke(new String[]{"deleteCheckInfo", key});
    }

    public String getHistory(String json) throws Exception {
        JSONObject jo = JSONObject.fromObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String batchCode = jo.getString("BATCH_ID");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"BATCH_ID\": \""+batchCode+"\"}]}}";
        String keys = queryCheckKeys(param);
        JSONArray arr = JSONArray.fromObject(keys);
        String key = arr.getString(0);
        return query(new String[]{"getHistory", key});
    }
}
