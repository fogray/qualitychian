package com.inspur.fabric.moniter;

import java.util.List;
import java.util.Map;

/**
 * fabric监控接口domain接口类
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/2/8
 */
public interface IMoniterDomain {

    /**
     * 插入一条区块信息
     * @param beanMap
     * @return
     */
    public int insertBlock(Map<String, Object> beanMap);

    /**
     * 插入一条交易信息
     * @param beanMap
     * @return
     */
    public int insertTx(Map<String, Object> beanMap);

    /**
     * 获取监控表中最新区块号
     * @param channelName
     * @return
     */
    public Map<String,Object> getBlockNum(String channelName);

    /**
     * 根据交易id查询交易信息（模糊查询）
     * @param param
     * @return
     */
    public List<Map<String, Object>> searchTransaction(String param);

    /**
     * 根据交易id查询交易信息
     * @param txId
     * @return
     */
    public Map<String,Object> getTransactionByTxId(String txId);

}
