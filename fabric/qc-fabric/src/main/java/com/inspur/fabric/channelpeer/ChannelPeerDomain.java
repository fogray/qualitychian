package com.inspur.fabric.channelpeer;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loushang.util.Page;

import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.domain.BaseDomainImpl;

/**
 * 功能描述：chain_channel_peer Domain 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-06
 * 修改人：
 * 修改时间：
 */
public class ChannelPeerDomain extends BaseDomainImpl implements IChannelPeerDomain {

	private static final long serialVersionUID = -7961010272396231745L;
	private static final Log log = LogFactory.getLog(ChannelPeerDomain.class);

	/**
	 * 功能描述：删除 chain_channel_peer 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param Map paraMap
	 */
	public void deleteChainChannelPeer(Map paraMap) {
		V6SqlSessionUtil.getSqlSession().delete("ChainChannelPeerDomain.deleteChainChannelPeer", paraMap);
	}

	/**
	 * 功能描述：取得 chain_channel_peer 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param Map paraMap
	 * @return
	 */
	public Map<String, Object> getChainChannelPeer(Map paraMap) {
		return V6SqlSessionUtil.getSqlSession().selectOne("ChainChannelPeerDomain.getChainChannelPeer", paraMap);
	}
	
	/**
	 * 功能描述：查询 chain_channel_peer 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param map
	 * @return
	 */
	public Page getAllChainChannelPeer(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("ChainChannelPeerDomain.getAllChainChannelPeerPage", map);
		return PageUtil.createPage(map, datas);
	}
	/**
	 * 查询通道/链 企业数
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getChannelOrg(Map<String, Object> map) {
		return V6SqlSessionUtil.getSqlSession().selectList("ChainChannelPeerDomain.getChannelOrg", map);
	}
	
	/**
	 * 功能描述：插入 chain_channel_peer 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainChannelPeer(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().insert("ChainChannelPeerDomain.insertChainChannelPeer", beanMap);
		return beanMap;
	}
	
	/**
	 * 查询每个链内节点数
	 * @return
	 */
	public List<Map<String, Object>> getChainChannelPeerCount() {
		List<Map<String, Object>> beanMap = V6SqlSessionUtil.getSqlSession().selectList("ChainChannelPeerDomain.getChainChannelPeerCount");
		return beanMap;
	}
	/**
	 * 每个链内企业数
	 * @return
	 */
	public List<Map<String, Object>> getChainChannelOrgCount() {
		List<Map<String, Object>> beanMap = V6SqlSessionUtil.getSqlSession().selectList("ChainChannelPeerDomain.getChainChannelOrgCount");
		return beanMap;
	}
	
	/***
	 * 企业总数
	 * @return
	 */
	public Map<String, Object> getOrganizeCount() {
		return V6SqlSessionUtil.getSqlSession().selectOne("ChainChannelPeerDomain.getOrganizeCount");
	}
	
	/**
	 * 企业节点数
	 * @return
	 */
	public List<Map<String, Object>> getOrganizePeer() {
		List<Map<String, Object>> beanMap = V6SqlSessionUtil.getSqlSession().selectList("ChainChannelPeerDomain.getOrganizePeer");
		return beanMap;
	}
	
	/**
	 * 功能描述：修改 chain_channel_peer 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainChannelPeer(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().update("ChainChannelPeerDomain.updateChainChannelPeer", beanMap);
		return beanMap;
	}

	@Override
	protected void initDomain() {
	}
	
	/**
	 * 企业节点数
	 * @return
	 */
	public List<Map<String, Object>> getOrganPeerCount() {
		return V6SqlSessionUtil.getSqlSession().selectList("ChainChannelPeerDomain.getOrganPeerCount");
	}

	/**
	 * 节点加入的channel列表
	 * @param String peerId
	 * @return
	 */
	public List<Map<String, Object>> getChannelListByPeerId(String peerId) {
		return V6SqlSessionUtil.getSqlSession().selectList("ChainChannelPeerDomain.getChannelListByPeerId", peerId);
	}
}
