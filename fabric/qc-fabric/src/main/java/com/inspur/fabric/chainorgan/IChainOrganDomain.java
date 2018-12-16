package com.inspur.fabric.chainorgan;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

/**
 * 功能描述：组织 Domain 接口类
 * 创建人：JIANGDSH01
 * 创建时间：2018-01-30
 * 修改人：
 * 修改时间：
 */
public interface IChainOrganDomain extends Serializable{

	/**
	 * 功能描述: 删除 组织 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-30
	 * @param String organId
	 */
	public void deleteChainOrgan(String organId);

	/**
	 * 功能描述: 取得 组织 一条明细
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-30
	 * @param String organId
	 */
	public Map<String, Object> getChainOrgan(String organId);

	/**
	 * 功能描述: 查询 组织 一页记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-30
	 * @param map
	 */
	public Page getAllChainOrgan(Map<String, Object> map);
	
	public List getChainOrgan(Map<String, Object> map);
	
	/**
	 * 功能描述: 插入 组织 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-30
	 * @param beanMap
	 */ 
	public Map<String, Object> insertChainOrgan(Map<String, Object> beanMap);

	/**
	 * 功能描述: 修改 组织 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-30
	 * @param beanMap
	 */
	public Map<String, Object> updateChainOrgan(Map<String, Object> beanMap);
	/**
	 * 组织大小
	 * @return
	 */
	public long getAllChainOrganSize();
	
	/**
	 * 获取指定组织加入的所有channel
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getAllChannelByOrgan(Map<String, Object> map);
	
}
