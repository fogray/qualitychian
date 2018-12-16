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
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/1/24
 */
public class BuildChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(BuildChainCode.class);
    private static BuildChainCode instance;
    public static BuildChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (BuildChainCode.class){
                if (null==instance){
                    instance = new BuildChainCode(peerOrg);
                }
            }
        }
        return instance;
    }
    public BuildChainCode(String peerOrg){
        init(peerOrg);
    }

    @Override
    protected void init(String peerOrg){
        try {
            Properties properties = new Properties();
            File loadFile;
            FileInputStream fis;
            loadFile = new File(this.getClass().getResource("/").getPath()+"/fabric.properties").getAbsoluteFile();
            fis = new FileInputStream(loadFile);
            properties.load(fis);
            String channelName = properties.getProperty("build_chaincode_channel","trace4");
            String chainCodeName = properties.getProperty("build_chaincode_name","qc_build_cc");
            String chainCodePath = properties.getProperty("build_chaincode_path","qcbuildcc");
            String ccVersion = properties.getProperty("build_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("BuildChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("BuildChainCode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key1});
        } catch (Exception e) {
            log.error("BuildChainCode--getTraceInfo--err-->",e);
            e.printStackTrace();
        }
        return res;
    }
    public String getTraceHistory(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("BuildChainCode--getTraceHistory--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getHistory",key1});
        } catch (Exception e) {
            log.error("BuildChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }
}
