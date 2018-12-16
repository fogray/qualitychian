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
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/1/24
 */
public class ElectricChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(ElectricChainCode.class);
    private static ElectricChainCode instance;
    public static ElectricChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (ElectricChainCode.class){
                if (null==instance){
                    instance = new ElectricChainCode(peerOrg);
                }
            }
        }
        return instance;
    }
    public ElectricChainCode(String peerOrg){
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
            String channelName = properties.getProperty("electric_chaincode_channel","trace4");
            String chainCodeName = properties.getProperty("electric_chaincode_name","qc_electric_cc");
            String chainCodePath = properties.getProperty("electric_chaincode_path","qcelectriccc");
            String ccVersion = properties.getProperty("electric_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("ElectricChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("ElectricChainCode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key1});
        } catch (Exception e) {
            log.error("getElectricInfo--err-->",e);
        }
        return res;
    }

    public String getTraceHistory(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = query(new String[]{"queryTraceKeys",param});
            if(log.isDebugEnabled()){
                log.debug("ElectricChainCode--getTraceHistory--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getHistory",key1});
        } catch (Exception e) {
            log.error("getTraceHistory--err-->",e);
        }
        return res;
    }
}
