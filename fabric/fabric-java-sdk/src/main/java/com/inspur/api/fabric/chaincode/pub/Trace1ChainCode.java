package com.inspur.api.fabric.chaincode.pub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

/**
 * 类型1（trace_code_type=1）生产溯源信息上链（格力、家电、家居、建材等除阿胶、浪潮信息外的溯源数据）
 * @author yanghaiyong
 * @description
 * @date 2017/12/26
 */
public class Trace1ChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(Trace1ChainCode.class);
    private static Trace1ChainCode instance;
    private String channelName;
    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;
    public static Trace1ChainCode getInstance(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
        if(null==instance){
            synchronized (Trace1ChainCode.class){
                if (null==instance){
                    instance = new Trace1ChainCode(peerOrg, channelName, chaincodeName, chaincodePath, chaincodeVer);
                }
            }
        }
        return instance;
    }
    
    public Trace1ChainCode(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
    	this.channelName = channelName;
    	this.chaincodeName = chaincodeName;
    	this.chaincodePath = chaincodePath;
    	this.chaincodeVersion = chaincodeVer;
        init(peerOrg);
    }
    @Override
    protected void init(String peerOrg) {
        try {
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(this.chaincodeName,this.chaincodePath,this.chaincodeVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("Trace1ChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo", key,value,"trace1", "1"});
        }catch (Exception e){
            log.error("Trace1ChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
            String param = "{\"selector\":{\"$and\": [{\"START\": {\"$lte\": \""+key+"\"}}, {\"END\": {\"$gte\": \""+key+"\"}}]}}";
            String json = queryTraceKeys(param);
            if(log.isDebugEnabled()){
                log.debug("Trace1ChainCode--getTraceInfo--json-->"+json);
            }
            JSONArray arr = new JSONArray(json);
            String key1 = arr.getString(0);
            res = query(new String[]{"getTraceInfo",key1});
        } catch (Exception e) {
            log.error("Trace1ChainCode--getTraceInfo--err-->",e);
        }
        return res;
    }

    public String queryTraceKeys(String param) {
        String res = "[]";
        try {
            res = query(new String[]{"queryTraceKeys",param});
        }catch (Exception e){
            log.error("Trace1ChainCode--queryTraceKeys--err-->",e);
        }
        return res;
    }

    public String getTraceHistory(String key){
        String res = "";
        try {
            res = query(new String[]{"getHistory",key});
        }catch (Exception e){
            log.error("Trace1ChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }
}
