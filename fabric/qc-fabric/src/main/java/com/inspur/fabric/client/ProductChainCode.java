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
 * @date 2017/12/23
 */
public class ProductChainCode extends AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(ProductChainCode.class);
    private static ProductChainCode instance;
    public static ProductChainCode getInstance(String peerOrg){
        if (null==instance){
            synchronized (ProductChainCode.class){
                if (null==instance){
                    instance = new ProductChainCode(peerOrg);
                }
            }
        }
        return instance;
    }
    public ProductChainCode(String peerOrg){
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
            String channelName = properties.getProperty("product_chaincode_channel","trace1");
            String chainCodeName = properties.getProperty("product_chaincode_name","product_cc");
            String chainCodePath = properties.getProperty("product_chaincode_path","productcc");
            String ccVersion = properties.getProperty("product_chaincode_version","1.0");

            if(log.isDebugEnabled()){
                log.debug("ProductChainCode--channelName-->"+channelName);
                log.debug("ProductChainCode--chainCodeName-->"+chainCodeName);
                log.debug("ProductChainCode--chainCodePath-->"+chainCodePath);
                log.debug("ProductChainCode--ccVersion-->"+ccVersion);
            }

            this.client = clientHelper.getHFClient();
            this.channel = clientHelper.getChannel(peerOrg,channelName);
            this.queryChannel = clientHelper.getQueryChannel(peerOrg,channelName);
            this.chaincodeID = clientHelper.getChaincodeID(chainCodeName,chainCodePath,ccVersion);
            BaseOrg org = clientHelper.getOrg(peerOrg);
            this.client.setUserContext(org.getPeerAdmin());
        } catch (Exception e) {
            log.error("ProductChainCode--init--err-->",e);
            instance = null;
        }
    }
}
