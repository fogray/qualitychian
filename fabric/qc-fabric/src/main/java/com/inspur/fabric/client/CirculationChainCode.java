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
 * @date 2017/12/26
 */
public class CirculationChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(CirculationChainCode.class);
    private static ConcurrentHashMap<String, CirculationChainCode> instanceCache = new ConcurrentHashMap<String, CirculationChainCode>();
    private String channelName;
    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;
    
    public static CirculationChainCode getInstance(String peerOrg, String channelName){
        String key = peerOrg+channelName;
        if(!instanceCache.containsKey(key)){
            synchronized (CirculationChainCode.class){
                if (!instanceCache.containsKey(key)){
                    new CirculationChainCode(peerOrg, channelName);
                }
            }
        }
        return instanceCache.get(key);
    }

    public CirculationChainCode(String peerOrg, String channelName){
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
            this.channelName = properties.getProperty("delivery_chaincode_name","qc_delivery_cc");
            this.chaincodePath = properties.getProperty("delivery_chaincode_path","qcdeliverycc");
            this.chaincodeVersion = properties.getProperty("delivery_chaincode_version","1.0");
            
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(this.chaincodeName,this.chaincodePath,this.chaincodeVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("CirculationChainCode--init--err-->",e);
        }
    }

    public String setTraceInfo(String key, String value) throws Exception {
        return invoke(new String[]{"setTraceInfo",key,value});
    }

    public String getTraceInfo(String json) throws Exception {
        JSONObject jo = JSONObject.fromObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String traceCode = jo.getString("TRACE_CODE");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"TRACE_CODE_JSON\": {\"$all\":[\""+traceCode+"\"]}}]}}";
        String keys = queryTraceKeys(param);
        JSONArray arr = JSONArray.fromObject(keys);
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
        JSONObject jo = JSONObject.fromObject(json);
        String productCode = jo.getString("PRODUCT_CODE");
        String traceCode = jo.getString("TRACE_CODE");
        String param = "{\"selector\":{\"$and\": [{\"PRODUCT_CODE\": \""+productCode+"\"}, {\"TRACE_CODE_JSON\": {\"$all\":[\""+traceCode+"\"]}}]}}";
        String keys = queryTraceKeys(param);
        JSONArray arr = JSONArray.fromObject(keys);
        String key = arr.getString(0);
        return query(new String[]{"getHistory",key});
    }
}
