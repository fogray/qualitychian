package com.inspur.api.fabric.chaincode.pub;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * 类型3(trace_code_type=3)生产溯源信息上链（例如：格力的溯源数据）
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/26
 */
public class Trace3ChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(Trace3ChainCode.class);
    private static Trace3ChainCode instance;
    private String channelName;
    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;
    public static Trace3ChainCode getInstance(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
        if(null==instance){
            synchronized (Trace3ChainCode.class){
                if (null==instance){
                    instance = new Trace3ChainCode(peerOrg, channelName, chaincodeName, chaincodePath, chaincodeVer);
                }
            }
        }
        return instance;
    }
    
    public Trace3ChainCode(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
    	this.channelName = channelName;
    	this.chaincodeName = chaincodeName;
    	this.chaincodePath = chaincodePath;
    	this.chaincodeVersion = chaincodeVer;
        init(peerOrg);
    }
    @Override
    protected void init(String peerOrg) {
        try {
            if (this.channelName == null || "".equals(this.channelName)) {
                Properties properties = new Properties();
                File loadFile;
                FileInputStream fis;
                loadFile = new File(this.getClass().getResource("/").getPath()+"/fabric.properties").getAbsoluteFile();
                fis = new FileInputStream(loadFile);
                properties.load(fis);
                this.channelName = properties.getProperty("gree_chaincode_channel","trace4");
                this.chaincodeName = properties.getProperty("gree_chaincode_name","qc_gree_cc");
                this.chaincodePath = properties.getProperty("gree_chaincode_path","qcgreecc");
                this.chaincodeVersion = properties.getProperty("gree_chaincode_version","1.0");
        	}
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(this.chaincodeName,this.chaincodePath,this.chaincodeVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("Trace3ChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo", key, value, "trace1", "3"});
        }catch (Exception e){
            log.error("Trace3ChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }
    
    public String getTraceInfo(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("Trace3ChainCode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key1});
        } catch (Exception e) {
            log.error("Trace3ChainCode getTraceInfo--err-->",e);
        }
        return res;
    }
    public String getTraceHistory(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("Trace3ChainCode--getTraceHistory--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getHistory",key1});
        } catch (Exception e) {
            log.error("Trace3ChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }
}
