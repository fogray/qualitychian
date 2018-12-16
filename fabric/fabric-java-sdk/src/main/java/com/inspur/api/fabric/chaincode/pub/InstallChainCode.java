package com.inspur.api.fabric.chaincode.pub;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

/**
 * 链码安装
 * @author yanghaiyong@inspur.com
 * @description
 * @date 2017/12/23
 */
public class InstallChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(InstallChainCode.class);
    private static InstallChainCode instance;
    public static InstallChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (InstallChainCode.class){
                if (null==instance){
                    instance = new InstallChainCode(peerOrg);
                }
            }
        }
        return instance;
    }
    public InstallChainCode(String peerOrg){
        init(peerOrg);
    }

    @Override
    protected void init(String peerOrg){
        try {
//            Properties properties = new Properties();
//            File loadFile;
//            FileInputStream fis;
//            loadFile = new File(this.getClass().getResource("/").getPath()+"/fabric.properties").getAbsoluteFile();
//            fis = new FileInputStream(loadFile);
//            properties = PropertiesLoaderUtils.loadAllProperties("fabric.properties");
//            properties.load(fis);
            String channelName = "channel01";
//            String channelName = properties.getProperty("org_chaincode_channel","trace1");
//            String chainCodeName = properties.getProperty("org_chaincode_name","org_cc");
//            String chainCodePath = properties.getProperty("org_chaincode_path","orgcc");
//            String ccVersion = properties.getProperty("org_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("InstallChainCode--init--err-->", e);
            instance = null;
        }
    }

    public void install(String chaincodeName, String chaincodePath, String version) {
    	this.chaincodeID = clientHelper.getChaincodeID(chaincodeName,chaincodePath,version);
    	List<String> peers = new ArrayList<String>();
    	try {
			install(peers);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
