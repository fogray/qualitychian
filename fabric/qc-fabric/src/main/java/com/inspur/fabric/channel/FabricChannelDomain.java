package com.inspur.fabric.channel;

import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.domain.BaseDomainImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loushang.util.Page;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：数据审核记录表 Domain 实现类
 * 创建人：DESKTOP-PK95QHU
 * 创建时间：2017-12-22
 * 修改人：
 * 修改时间：
 */
public class FabricChannelDomain extends BaseDomainImpl implements IFabricChannelDomain {

	private static final long serialVersionUID = 1085124915575207749L;
	private static final Log log = LogFactory.getLog(FabricChannelDomain.class);

	/**
	 * 功能描述：删除 数据审核记录表 一条记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param String testId
	 * @see com.inspur.base.producttest.ITestDomain#deleteTest(String)
	 */
	public void deleteFabricChannel(String id) {
		V6SqlSessionUtil.getSqlSession().delete("FabricChannelDomain.deleteFabricChannel",id);
	}

	/**
	 * 功能描述：取得 数据审核记录表 一条明细
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param String testId
	 * @return
	 * @see com.inspur.base.producttest.ITestDomain#getTest(String)
	 */
	public Map<String,Object> getFabricChannel(String id) {
		return V6SqlSessionUtil.getSqlSession().selectOne("FabricChannelDomain.getFabricChannel",id);
	}

	/**
	 * 功能描述：查询 数据审核记录表 一页记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param map
	 * @return
	 * @see com.inspur.base.producttest.ITestDomain#getAllTest(Map)
	 */
	public List<Map<String,Object>> getAllFabricChannel() {
		return V6SqlSessionUtil.getSqlSession().selectList("FabricChannelDomain.getAllFabricChannel");
	}
	/**
	 * 通过channelName获取链状态
	 * *//*
	public Map<String,Object> getFabricChannelByChannelName(Map<String, Object> map) {
		return V6SqlSessionUtil.getSqlSession().selectOne("FabricChannelDomain.getAllFabricChannel",map);
	}*/
	@Override
	public Map<String, Object> getFabricChannelMap(Map<String, String> map) {
		return V6SqlSessionUtil.getSqlSession().selectOne("FabricChannelDomain.getFabricChannelMap",map);
	}

	public Page getAllFabricChannel(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("FabricChannelDomain.getAllFabricChannel", map);
		return PageUtil.createPage(map, datas);
	}

	/**
	 * 功能描述：插入 数据审核记录表 一条记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param beanMap
	 * @return
	 * @see com.inspur.base.producttest.ITestDomain#insertTest(Map)
	 */
	public Map<String,Object> insertFabricChannel(Map<String,Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().insert("FabricChannelDomain.insertFabricChannel",beanMap);
		return beanMap;
	}

	/**
	 * 功能描述：修改 数据审核记录表 一条记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param beanMap
	 * @return
	 * @see com.inspur.base.producttest.ITestDomain#updateTest(Map)
	 */
	public Map<String,Object> updateFabricChannel(Map<String,Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().update("FabricChannelDomain.updateFabricChannel",beanMap);
		return beanMap;
	}

	/**
	 * 获取所有有效的通道
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getValidChannels() {
		return V6SqlSessionUtil.getSqlSession().selectList("FabricChannelDomain.getValidChannels");
	}

	@Override
	protected void initDomain() {
	}


}
