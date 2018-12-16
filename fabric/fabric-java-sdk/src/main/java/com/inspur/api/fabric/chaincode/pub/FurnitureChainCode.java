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
 * @date 2018/5/21
 */
public class FurnitureChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(FurnitureChainCode.class);
    private static FurnitureChainCode instance;
    public static FurnitureChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (FurnitureChainCode.class){
                if (null==instance){
                    instance = new FurnitureChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public FurnitureChainCode(String peerOrg){
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
            String channelName = properties.getProperty("furniture_chaincode_channel","trace7");
            String chainCodeName = properties.getProperty("furniture_chaincode_name","qc_furniture_cc");
            String chainCodePath = properties.getProperty("furniture_chaincode_path","qcfurniturecc");
            String ccVersion = properties.getProperty("furniture_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("FurnitureChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo",key,value,"trace1"});
        }catch (Exception e){
            log.error("FurnitureChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = queryTraceKeys(param);
            if(log.isDebugEnabled()){
                log.debug("FurnitureChainCode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key1});
        } catch (Exception e) {
            log.error("FurnitureChainCode--getTraceInfo--err-->",e);
        }
        return res;
    }

    public String queryTraceKeys(String param) {
        String res = "[]";
        try {
            res = query(new String[]{"queryTraceKeys",param});
        }catch (Exception e){
            log.error("FurnitureChainCode--queryTraceKeys--err-->",e);
        }
        return res;
    }

    public String getTraceHistory(String key){
        String res = "";
        try {
            res = query(new String[]{"getHistory",key});
        }catch (Exception e){
            log.error("FurnitureChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }

}
