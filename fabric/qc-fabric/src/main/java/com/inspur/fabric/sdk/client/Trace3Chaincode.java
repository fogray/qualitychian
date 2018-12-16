package com.inspur.fabric.sdk.client;

import com.inspur.fabric.pub.cache.FabricCacheUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/25
 */
public class Trace3Chaincode extends ChaincodeImpl {
    private static final Log log = LogFactory.getLog(Trace3Chaincode.class);
    private static ConcurrentHashMap<String, Trace3Chaincode> instanceCache = new ConcurrentHashMap<String, Trace3Chaincode>();
    public static Trace3Chaincode getInstance(String chaincodeName, String chaincodePath, String user){
        String key = user+"-"+chaincodeName;
        if(!instanceCache.containsKey(key)){
            synchronized (CheckChaincode.class){
                if (!instanceCache.containsKey(key)){
                    String channelName = FabricCacheUtil.getChainCodeChannel(chaincodeName);
                    String version = FabricCacheUtil.getChaincodeVersion(chaincodeName);
                    if (channelName==null || "".equals(channelName) || version==null || "".equals(version)){
                        throw new RuntimeException("chaincode "+chaincodeName+" does not exist!");
                    }
                    instanceCache.put(key, new Trace3Chaincode(chaincodeName,chaincodePath,version,channelName,user));
                }
            }
        }
        return instanceCache.get(key);
    }
    protected Trace3Chaincode(String chaincodeName, String chaincodePath, String version, String channelName, String user) {
        super(chaincodeName, chaincodePath, version, channelName, user);
    }

    public String setTraceInfo(String key, String value) {
        String res = "failed";
        try{
            res = invoke(new String[]{"setTraceInfo", key, value, "trace1", "3"});
        }catch (Exception e){
            log.error("Trace3ChainCode--setTraceInfo--err-->",e);
        }
        return res;
    }

    public String getTraceInfo(String key){
        String res = "";
        try {
            res = query(new String[]{"getTraceInfo",key});
        } catch (Exception e) {
            log.error("Trace3ChainCode getTraceInfo--err-->",e);
        }
        return res;
    }
    public String getTraceHistory(String key){
        String res = "";
        try {
            res = query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("Trace3ChainCode--getTraceHistory--err-->",e);
        }
        return res;
    }

    public String deleteTraceInfo(String key) throws Exception {
        return invoke(new String[]{"deleteTraceInfo",key});
    }
}
