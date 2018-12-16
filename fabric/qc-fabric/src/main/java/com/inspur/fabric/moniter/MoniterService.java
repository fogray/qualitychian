package com.inspur.fabric.moniter;

import com.v6.base.service.BaseServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/2/8
 */
public class MoniterService extends BaseServiceImpl implements IMoniterService {
    private static final Log log = LogFactory.getLog(MoniterService.class);

    private IMoniterDomain moniterDomain;
    public IMoniterDomain getMoniterDomain() {
        return moniterDomain;
    }

    public void setMoniterDomain(IMoniterDomain moniterDomain) {
        this.moniterDomain = moniterDomain;
    }

    @Override
    public int insertBlock(Map<String, Object> beanMap) {
        return getMoniterDomain().insertBlock(beanMap);
    }

    @Override
    public int insertTx(Map<String, Object> beanMap) {
        return getMoniterDomain().insertTx(beanMap);
    }

    @Override
    public Map<String, Object> getBlockNum(String channelName) {
        return getMoniterDomain().getBlockNum(channelName);
    }

    @Override
    public List<Map<String, Object>> searchTransaction(String param) {
        return getMoniterDomain().searchTransaction(param);
    }

    @Override
    public Map<String, Object> getTransactionByTxId(String txId) {
        return getMoniterDomain().getTransactionByTxId(txId);
    }

    @Override
    protected void initService() {

    }
}
