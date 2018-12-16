package com.inspur.fabric.chaincode;

import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.domain.BaseDomainImpl;

/**
 * 功能描述：链码 Domain 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-01
 * 修改人：
 * 修改时间：
 */
public class ChaincodeDomain extends BaseDomainImpl implements IChaincodeDomain {

	private static final long serialVersionUID = 839316794531291450L;

	/**
	 * 功能描述：删除 链码 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param String chaincodeId
	 */
	public void deleteChainChaincode(String chaincodeId) {
		V6SqlSessionUtil.getSqlSession().delete("ChaincodeDomain.deleteChainChaincode", chaincodeId);
	}

	/**
	 * 功能描述：取得 链码 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param String chaincodeId
	 * @return
	 */
	public Map<String, Object> getChainChaincode(String chaincodeId) {
		return V6SqlSessionUtil.getSqlSession().selectOne("ChaincodeDomain.getChainChaincode", chaincodeId);
	}

	@Override
	public List<Map<String, Object>> getChainChaincodeMap(Map<String, String> map) {
		return V6SqlSessionUtil.getSqlSession().selectList("ChaincodeDomain.getChainChaincodeMap", map);
	}

	/**
	 * 功能描述：查询 链码 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param map
	 * @return
	 */
	public Page getAllChainChaincode(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("ChaincodeDomain.getAllChainChaincodePage", map);
		return PageUtil.createPage(map, datas);
	}
	
	/**
	 * 功能描述：插入 链码 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainChaincode(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().insert("ChaincodeDomain.insertChainChaincode", beanMap);
		return beanMap;
	}
	
	/**
	 * 功能描述：修改 链码 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainChaincode(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().update("ChaincodeDomain.updateChainChaincode", beanMap);
		return beanMap;
	}

	@Override
	protected void initDomain() {
	}
	//获取链信息
	@Override
	public Map<String, Object> getChainChaincodeByParamsName(String paramsName) {
		return V6SqlSessionUtil.getSqlSession().selectOne("ChaincodeDomain.getChainChaincodeByParamsName", paramsName);
		
	}
}
