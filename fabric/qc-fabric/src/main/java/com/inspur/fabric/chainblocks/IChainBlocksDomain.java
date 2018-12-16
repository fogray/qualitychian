package com.inspur.fabric.chainblocks;

import java.io.Serializable;
import java.util.Map;

import org.loushang.util.Page;

/**
 * 功能描述：chain_blocks Domain 接口类
 * 创建人：JIANGDSH01
 * 创建时间：2018-04-03
 * 修改人：
 * 修改时间：
 */
public interface IChainBlocksDomain extends Serializable{

	/**
	 * 功能描述: 删除 chain_blocks 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-04-03
	 * @param String id
	 */
	public void deleteChainBlocks(String id);

	/**
	 * 功能描述: 取得 chain_blocks 一条明细
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-04-03
	 * @param String id
	 */
	public Map<String, Object> getChainBlocks(String id);
	public Map<String, Object> getChainBlockHight(String channel) ;

	/**
	 * 功能描述: 查询 chain_blocks 一页记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-04-03
	 * @param map
	 */
	public Page getAllChainBlocks(Map<String, Object> map);

	/**
	 * 功能描述: 插入 chain_blocks 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-04-03
	 * @param beanMap
	 */ 
	public Map<String, Object> insertChainBlocks(Map<String, Object> beanMap);

	/**
	 * 功能描述: 修改 chain_blocks 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-04-03
	 * @param beanMap
	 */
	public Map<String, Object> updateChainBlocks(Map<String, Object> beanMap);
}
