package com.inspur.fabric.sdk.client;

import com.inspur.fabric.pub.cache.FabricCacheUtil;
import com.inspur.fabric.sdk.base.AbstractInvokeChaincode;
import com.inspur.fabric.sdk.base.BaseChannel;
import com.inspur.fabric.sdk.base.FabricManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.ChaincodeID;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/21
 */
public class ChaincodeImpl extends AbstractInvokeChaincode {
    private static final Log log = LogFactory.getLog(ChaincodeImpl.class);

    private static ConcurrentHashMap<String, ChaincodeImpl> instanceCache = new ConcurrentHashMap<String, ChaincodeImpl>();

    public static ChaincodeImpl getInstance(String chaincodeName, String chaincodePath, String user){
        String key = chaincodeName+"-"+user;
        if(!instanceCache.containsKey(key)){
            synchronized (ChaincodeImpl.class){
                if(!instanceCache.containsKey(key)){
                    String channelName = FabricCacheUtil.getChainCodeChannel(chaincodeName);
                    String version = FabricCacheUtil.getChaincodeVersion(chaincodeName);
                    if (channelName==null || "".equals(chaincodeName)){
                        throw new RuntimeException("chaincode "+chaincodeName+" does not exist!");
                    }
                    instanceCache.put(key, new ChaincodeImpl(chaincodeName, chaincodePath, version, channelName, user));
                }
            }
        }
        return instanceCache.get(key);
    }

    protected ChaincodeImpl(String chaincodeName, String chaincodePath, String version, String channelName, String user){
        init(chaincodeName, chaincodePath, version, channelName, user);
    }

    @Override
    protected void init(String chaincodeName, String chaincodePath, String version, String channelName, String user) {
        try {
            log.error("ChaincodeImpl init chaincodeName:"+chaincodeName);
            log.error("ChaincodeImpl init chaincodePath:"+chaincodePath);
            log.error("ChaincodeImpl init version:"+version);
            log.error("ChaincodeImpl init channelName:"+channelName);
            log.error("ChaincodeImpl init user:"+user);
            baseChannel = BaseChannel.getInstance(channelName,user, FabricManager.getConfig().getOrgNames());
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setPath(chaincodePath).setVersion(version).build();
        } catch (Exception e) {
            log.error("ChaincodeImpl init error:",e);
        }
    }
}
