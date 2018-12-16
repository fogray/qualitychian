package com.inspur.fabric.client;

import com.inspur.fabric.base.BaseOrg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/1/23
 */
public class AppraisalChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(AppraisalChainCode.class);
    private static AppraisalChainCode instance;
    public static AppraisalChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (AppraisalChainCode.class){
                if (null==instance){
                    instance = new AppraisalChainCode(peerOrg);
                }
            }
        }
        return instance;
    }
    public AppraisalChainCode(String peerOrg){
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
            String channelName = properties.getProperty("appraisal_chaincode_channel","trace2");
            String chainCodeName = properties.getProperty("appraisal_chaincode_name","qc_appraisal_cc");
            String chainCodePath = properties.getProperty("appraisal_chaincode_path","qcappraisalcc");
            String ccVersion = properties.getProperty("appraisal_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("AppraisalChainCode--init--err-->",e);
            instance = null;
        }
    }
}
