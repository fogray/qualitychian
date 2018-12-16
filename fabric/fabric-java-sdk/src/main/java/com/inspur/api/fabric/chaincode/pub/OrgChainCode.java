package com.inspur.api.fabric.chaincode.pub;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.client.AbstractInvokeChainCode;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/23
 */
public class OrgChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(OrgChainCode.class);
    private static OrgChainCode instance;
    public static OrgChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (OrgChainCode.class){
                if (null==instance){
                    instance = new OrgChainCode(peerOrg);
                }
            }
        }
        return instance;
    }
    public OrgChainCode(String peerOrg){
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
            String channelName = properties.getProperty("org_chaincode_channel","trace1");
            String chainCodeName = properties.getProperty("org_chaincode_name","org_cc");
            String chainCodePath = properties.getProperty("org_chaincode_path","orgcc");
            String ccVersion = properties.getProperty("org_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
        	e.printStackTrace();
            log.error("OrgChainCode--init--err-->",e);
            instance = null;
        }
    }
}
