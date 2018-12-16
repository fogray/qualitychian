package com.inspur.api.fabric.chaincode.pub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

/**
 * @author zhang_lan@inspur.com
 * @description 茅台的第一个写链 链码调用实例
 * @date 2018/3/27
 */
public class Moutai1ChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(Moutai1ChainCode.class);
    private static volatile Moutai1ChainCode instance;
    public static Moutai1ChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (Moutai1ChainCode.class){
                if (null==instance){
                    instance = new Moutai1ChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public Moutai1ChainCode(String peerOrg){
        init(peerOrg);
    }
    
    @Override
    protected void init(String peerOrg) {
    	Properties properties = new Properties();
    	File loadFile;
    	FileInputStream fis = null;
        try {
            loadFile = new File(this.getClass().getResource("/").getPath()+"/fabric.properties").getAbsoluteFile();
            fis = new FileInputStream(loadFile);
            properties.load(fis);
            String channelName = properties.getProperty("moutaifirst_chaincode_channel","channel01");
            String chainCodeName = properties.getProperty("moutaifirst_chaincode_name","qc_moutaifirst_cc");
            String chainCodePath = properties.getProperty("moutaifirst_chaincode_path","qcmoutaifirstcc");
            String ccVersion = properties.getProperty("moutaifirst_chaincode_version","1.0");
            System.out.println("********chainCodePath="+chainCodePath);
            this.client = clientHelper.getHFClient();
            //重写getChannel
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            
            //org.getUser("User1")
            this.client.setUserContext(org.getPeerAdmin());//这是使用admin用户操作,需要研究下使用不同的用getUser("user1")
        } catch (Exception e) {
            e.printStackTrace();
            instance = null;
            try {
				fis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
    }
    /**
     * 查询瓶码,模糊搜索
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
