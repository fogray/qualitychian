package com.inspur.fabric.chaincode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

/**
 * 功能描述：链码 Service 接口类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-01
 * 修改人：
 * 修改时间：
 */
public interface IChaincodeService extends Serializable{

	/**
	 * 功能描述: 删除 链码 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-01
	 * @param String chaincodeId
	 */
	public void deleteChainChaincode(String chaincodeId);

	/**
	 * 功能描述: 取得 链码 一条明细
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-01
	 * @param String chaincodeId
	 */
	public Map<String, Object> getChainChaincode(String chaincodeId);
	public List<Map<String, Object>> getChainChaincodeMap(Map<String, String> map);
	
    /**
	 * 功能描述: 查询 链码 一页记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-01
	 * @param map
	 */
	public Page getAllChainChaincode(Map<String, Object> map);

	/**
	 * 功能描述: 插入 链码 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-01
	 * @param beanMap
	 */
	public Map<String, Object> insertChainChaincode(Map<String, Object> beanMap);

    /**
	 * 功能描述: 修改 链码 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-01
	 * @param beanMap
	 */
	public Map<String, Object> updateChainChaincode(Map<String, Object> beanMap);
	
	
	//获取链信息
	public Map<String, Object> getChainChaincodeByParamsName(String paramsName);
	
	
	
	
	
	
	
}
