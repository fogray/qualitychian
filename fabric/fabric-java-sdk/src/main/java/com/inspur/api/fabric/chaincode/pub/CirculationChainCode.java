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
public class CirculationChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(CirculationChainCode.class);
    private static CirculationChainCode instance;
    public static CirculationChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (CirculationChainCode.class){
                if (null==instance){
                    instance = new CirculationChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public CirculationChainCode(String peerOrg){
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
            String channelName = properties.getProperty("circulation_chaincode_channel","trace2");
            String chainCodeName = properties.getProperty("circulation_chaincode_name","qc_circulation_cc");
            String chainCodePath = properties.getProperty("circulation_chaincode_path","qccirculationcc");
            String ccVersion = properties.getProperty("circulation_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("CirculationChainCode--init--err-->",e);
            instance = null;
        }
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
