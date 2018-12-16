package com.inspur.fabric.chainorgan;

import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

import com.inspur.pub.tool.ObjectValidateUtil;
import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.domain.BaseDomainImpl;

/**
 * 功能描述：组织 Domain 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-01-30
 * 修改人：
 * 修改时间：
 */
public class ChainOrganDomain extends BaseDomainImpl implements IChainOrganDomain {

	private static final long serialVersionUID = 6371109171528995391L;

	/**
	 * 功能描述：删除 组织 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param String organId
	 */
	public void deleteChainOrgan(String organId) {
		V6SqlSessionUtil.getSqlSession().delete("ChainOrganDomain.deleteChainOrgan", organId);
	}

	/**
	 * 功能描述：取得 组织 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param String organId
	 * @return
	 */
	public Map<String, Object> getChainOrgan(String organId) {
		return V6SqlSessionUtil.getSqlSession().selectOne("ChainOrganDomain.getChainOrgan", organId);
	}
	
	/**
	 * 功能描述：查询 组织 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param map
	 * @return
	 */
	public Page getAllChainOrgan(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("ChainOrganDomain.getAllChainOrganPage", map);
		return PageUtil.createPage(map, datas);
	}
	
	public List getChainOrgan(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("ChainOrganDomain.getAllChainOrganPage", map);
		return datas;
	}
	
	/**
	 * 功能描述：插入 组织 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainOrgan(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().insert("ChainOrganDomain.insertChainOrgan", beanMap);
		return beanMap;
	}
	
	/**
	 * 功能描述：修改 组织 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainOrgan(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().update("ChainOrganDomain.updateChainOrgan", beanMap);
		return beanMap;
	}

	@Override
	protected void initDomain() {
	}

	@Override
	public long getAllChainOrganSize() {
		Map map = V6SqlSessionUtil.getSqlSession().selectOne("ChainOrganDomain.getChainOrgans");
		return ObjectValidateUtil.isNull(map) ? 0 :(long) map.get("VAL");
	}

	/**
	 * 获取指定组织加入的所有channel
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getAllChannelByOrgan(Map<String, Object> map) {
		return V6SqlSessionUtil.getSqlSession().selectList("ChainOrganDomain.getAllChannelByOrgan", map);
	}
}
