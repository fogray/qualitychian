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
 * @description 茅台的第2个写链 链码调用实例
 * @date 2018/3/27
 */
public class Moutai2ChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(Moutai2ChainCode.class);
    private static volatile Moutai2ChainCode instance;
    public static Moutai2ChainCode getInstance(String peerOrg){
        //if(null==instance){
            synchronized (Moutai2ChainCode.class){
                if (null==instance){
                    instance = new Moutai2ChainCode(peerOrg);
                }
            }
        //}
        return instance;
    }

    public Moutai2ChainCode(String peerOrg){
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
            String channelName = properties.getProperty("moutaisecond_chaincode_channel","trace7");
            String chainCodeName = properties.getProperty("moutaisecond_chaincode_name","qc_moutaifirst_cc");
            String chainCodePath = properties.getProperty("moutaisecond_chaincode_path","qcmoutaifirstcc");
            String ccVersion = properties.getProperty("moutaisecond_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());//这是使用admin用户操作,需要研究下使用不同的用
        } catch (Exception e) {
            log.error("Moutai2ChainCode--init--err-->",e);
            instance = null;
        }
    }
    /**
     * 查询瓶码出库,模糊搜索
     * @param json
     * @return
     * @throws Exception
     */
    public String getInfo(String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        String uid = jo.getString("UID");
        String param = "{\"selector\":{\"$and\": [{\"UID\": \""+uid+"\"}]}}";
        String keys = queryKeys(param);
        JSONArray arr = new JSONArray(keys);
        String key = arr.getString(0);
        return query(new String[]{"getWineInfo",key});
    }
    
    public String queryKeys(String query) throws Exception {
        return query(new String[]{"queryKeys",query});
    }
    
}
