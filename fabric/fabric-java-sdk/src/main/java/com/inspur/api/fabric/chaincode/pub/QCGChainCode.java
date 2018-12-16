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
 * @date 2017/12/26
 */
public class QCGChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(QCGChainCode.class);
    private static QCGChainCode instance;
    public static QCGChainCode getInstance(String peerOrg){
        if(null==instance){
            synchronized (QCGChainCode.class){
                if (null==instance){
                    instance = new QCGChainCode(peerOrg);
                }
            }
        }
        return instance;
    }

    public QCGChainCode(String peerOrg){
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
            String channelName = properties.getProperty("qcg_chaincode_channel","trace1");
            String chainCodeName = properties.getProperty("qcg_chaincode_name","qc_generate_cc");
            String chainCodePath = properties.getProperty("qcg_chaincode_path","qcgeneratecc");
            String ccVersion = properties.getProperty("qcg_chaincode_version","1.0");
            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("QCGChainCode--init--err-->",e);
            instance = null;
        }
    }
}
