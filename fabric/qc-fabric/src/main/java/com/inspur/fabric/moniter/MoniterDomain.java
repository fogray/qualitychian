package com.inspur.fabric.moniter;

import com.inspur.fabric.chaincode.IChaincodeDomain;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.domain.BaseDomainImpl;

import java.util.List;
import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/2/8
 */
public class MoniterDomain extends BaseDomainImpl implements IMoniterDomain {

    @Override
    public int insertBlock(Map<String, Object> beanMap) {
        return V6SqlSessionUtil.getSqlSession().insert("MoniterDomain.insertBlock", beanMap);
    }

    @Override
    public int insertTx(Map<String, Object> beanMap) {
        return V6SqlSessionUtil.getSqlSession().insert("MoniterDomain.insertTx", beanMap);
    }

    @Override
    public Map<String, Object> getBlockNum(String channelName) {
        return V6SqlSessionUtil.getSqlSession().selectOne("MoniterDomain.getBlockNum",channelName);
    }

    @Override
    public List<Map<String, Object>> searchTransaction(String param) {
        return V6SqlSessionUtil.getSqlSession().selectList("MoniterDomain.searchTransaction",param);
    }

    @Override
    public Map<String, Object> getTransactionByTxId(String txId) {
        return V6SqlSessionUtil.getSqlSession().selectOne("MoniterDomain.getTransactionByTxId",txId);
    }

    @Override
    protected void initDomain() {

    }
}
