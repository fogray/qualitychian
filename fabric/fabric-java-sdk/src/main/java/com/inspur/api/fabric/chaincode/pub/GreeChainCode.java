package com.inspur.api.fabric.chaincode.pub;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

/**
 * 格力上链
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/26
 */
public class GreeChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(GreeChainCode.class);
    private static GreeChainCode instance;
    public static GreeChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (GreeChainCode.class){
                if (null==instance){
                    instance = new GreeChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public GreeChainCode(String peerOrg){
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
            String channelName = properties.getProperty("gree_chaincode_channel","trace4");
            String chainCodeName = properties.getProperty("gree_chaincode_name","qc_gree_cc");
            String chainCodePath = properties.getProperty("gree_chaincode_path","qcgreecc");
            String ccVersion = properties.getProperty("gree_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("QCGChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("TraceChainCode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key1});
        } catch (Exception e) {
            log.error("getTraceInfo--err-->",e);
        }
        return res;
    }
    public String getTraceHistory(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("TraceChainCode--getTraceHistory--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getHistory",key1});
        } catch (Exception e) {
            log.error("TraceChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }
}
