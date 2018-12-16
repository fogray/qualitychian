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
 * @date 2017/12/26
 */
public class EjiaoChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(EjiaoChainCode.class);
    private static EjiaoChainCode instance;
    public static EjiaoChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (EjiaoChainCode.class){
                if (null==instance){
                    instance = new EjiaoChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public EjiaoChainCode(String peerOrg){
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
            String channelName = properties.getProperty("ejiao_chaincode_channel","trace6");
            String chainCodeName = properties.getProperty("ejiao_chaincode_name","qc_ejiao_cc");
            String chainCodePath = properties.getProperty("ejiao_chaincode_path","qcejiaocc");
            String ccVersion = properties.getProperty("ejiao_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("EjiaoChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String setTraceInfo(String key, String value) throws Exception {
        return invoke(new String[]{"setTraceInfo",key,value,"trace1"});
    }

    public String getTraceInfo(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String barCode = jo.getString("BAR_CODE");
        String param = "{\"selector\":{\"$and\":[{\"PRODUCT_CODE\":\""+productCode+"\"},{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\""+barCode+"\"]}}}}]}}";
        String keys = queryTraceKeys(param);
        JSONArray arr = new JSONArray(keys);
        String key = arr.getString(0);
        return query(new String[]{"getTraceInfo",key});
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
            log.error("EjiaoChainCode--searchCode--err-->",e);
        }
        return res;
    }

    public String queryTraceKeys(String query) throws Exception {
        return query(new String[]{"queryTraceKeys",query});
    }

    public String getHistory(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String barCode = jo.getString("BAR_CODE");
        String param = "{\"selector\":{\"$and\":[{\"PRODUCT_CODE\":\""+productCode+"\"},{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\""+barCode+"\"]}}}}]}}";
        String keys = queryTraceKeys(param);
        JSONArray arr = new JSONArray(keys);
        String key = arr.getString(0);
        return query(new String[]{"getHistory",key});
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }
}
