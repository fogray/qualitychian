package com.inspur.fabric.transaction;

import java.io.Serializable;
import java.util.Map;

import org.loushang.util.Page;

/**
 * 功能描述：交易 Domain 接口类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-05
 * 修改人：
 * 修改时间：
 */
public interface ITransactionDomain extends Serializable{

	/**
	 * 功能描述: 删除 交易 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-05
	 * @param String id
	 */
	public void deleteTransaction(String id);

	/**
	 * 功能描述: 取得 交易 一条明细
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-05
	 * @param String id
	 */
	public Map<String, Object> getTransaction(String id);
	public Map<String, Object> getTranbyHash(String hash);
	public Map<String, Object> getTransactionCount(Map<String, String> map);

	/**
	 * 功能描述: 查询 交易 一页记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-05
	 * @param map
	 */
	public Page getAllTransaction(Map<String, Object> map);
	
	/**
	 * tps
	 * @param map
	 * @return
	 */
	public Page getTransactionTop(Map<String, Object> map);

	/**
	 * 功能描述: 插入 交易 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-05
	 * @param beanMap
	 */ 
	public Map<String, Object> insertTransaction(Map<String, Object> beanMap);

	/**
	 * 功能描述: 修改 交易 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-05
	 * @param beanMap
	 */
	public Map<String, Object> updateTransaction(Map<String, Object> beanMap);
}
