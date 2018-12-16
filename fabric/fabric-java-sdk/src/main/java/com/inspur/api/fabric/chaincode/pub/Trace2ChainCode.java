package com.inspur.api.fabric.chaincode.pub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

/**
 * 类型2(trace_code_type=2)生产溯源信息上链（例如：阿胶的溯源数据）
 * @author yanghaiyong
 * @description
 * @date 2017/12/26
 */
public class Trace2ChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(Trace2ChainCode.class);
    private static Trace2ChainCode instance;
    private String channelName;
    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;
    public static Trace2ChainCode getInstance(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
        if(null==instance){
            synchronized (Trace2ChainCode.class){
                if (null==instance){
                    instance = new Trace2ChainCode(peerOrg, channelName, chaincodeName, chaincodePath, chaincodeVer);
                }
            }
        }
        return instance;
    }
    
    public Trace2ChainCode(String peerOrg, String channelName, String chaincodeName, String chaincodePath, String chaincodeVer){
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
            log.error("Trace2ChainCode--init--err-->",e);
            instance = null;
        }
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo",key,value,"trace1", "2"});
        }catch (Exception e){
            log.error("Trace2ChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfo(String key) {
    	String res = "";
    	try {
//            JSONObject jo = new JSONObject(json);
//            String productCode = jo.getString("PRODUCT_CODE");
//            String barCode = jo.getString("BAR_CODE");
//            String param = "{\"selector\":{\"$and\":[{\"PRODUCT_CODE\":\""+productCode+"\"},{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\""+barCode+"\"]}}}}]}}";
//            System.out.println("****arr="+param);
//            String keys = queryTraceKeys(param);
//            JSONArray arr = new JSONArray(keys);
//            String key = arr.getString(0);
//            System.out.println("****arr="+arr.toString());
            res = query(new String[]{"getTraceInfo",key});
    	} catch(Exception e) {
    		e.printStackTrace();
            log.error("Trace2ChainCode--getTraceInfo--err-->",e);
    	}
        return res;
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
            log.error("Trace2ChainCode--searchCode--err-->",e);
        }
        return res;
    }

    public String queryTraceKeys(String query) throws Exception {
        return query(new String[]{"queryTraceKeys",query});
    }

    public String getTraceHistory(String json) {
    	String res = "";
    	try {
            JSONObject jo = new JSONObject(json);
            String productCode = jo.getString("PRODUCT_CODE");
            String barCode = jo.getString("BAR_CODE");
            String param = "{\"selector\":{\"$and\":[{\"PRODUCT_CODE\":\""+productCode+"\"},{\"TRACE_CODE_JSON\":{\"$elemMatch\":{\"BAR_CODE\":{\"$all\":[\""+barCode+"\"]}}}}]}}";
            String keys = queryTraceKeys(param);
            JSONArray arr = new JSONArray(keys);
            String key = arr.getString(0);
            res = query(new String[]{"getHistory",key});
    	} catch(Exception e) {
            log.error("Trace2ChainCode--getTraceHistory--err-->",e);
    	}
        return res;
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }
}
