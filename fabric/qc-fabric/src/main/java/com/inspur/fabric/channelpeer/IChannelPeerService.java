package com.inspur.fabric.channelpeer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

/**
 * 功能描述：chain_channel_peer Service 接口类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-06
 * 修改人：
 * 修改时间：
 */
public interface IChannelPeerService extends Serializable{
	/**
	 * 查询通道/链 企业数
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>>  getChannelOrg(Map<String, Object> map) ;
	
	/**
	 * 企业节点数
	 * @return
	 */
	public List<Map<String, Object>> getOrganizePeer() ;
	
	/***
	 * 企业总数
	 * @return
	 */
	public Map<String, Object> getOrganizeCount();
	
	/**
	 * 链内企业数
	 * @return
	 */
	public List<Map<String, Object>> getChainChannelOrgCount();
	
	/**
	 * 查询链内节点数
	 * @return
	 */
	public List<Map<String, Object>> getChainChannelPeerCount() ;
	
	/**
	 * 功能描述: 删除 chain_channel_peer 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-06
	 * @param Map paraMap
	 */
	public void deleteChainChannelPeer(Map paraMap);

	/**
	 * 功能描述: 取得 chain_channel_peer 一条明细
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-06
	 * @param Map paraMap
	 */
	public Map<String, Object> getChainChannelPeer(Map paraMap);

    /**
	 * 功能描述: 查询 chain_channel_peer 一页记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-06
	 * @param map
	 */
	public Page getAllChainChannelPeer(Map<String, Object> map);

	/**
	 * 功能描述: 插入 chain_channel_peer 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-06
	 * @param beanMap
	 */
	public Map<String, Object> insertChainChannelPeer(Map<String, Object> beanMap);

    /**
	 * 功能描述: 修改 chain_channel_peer 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-02-06
	 * @param beanMap
	 */
	public Map<String, Object> updateChainChannelPeer(Map<String, Object> beanMap);
	
	/**
	 * 企业节点数
	 * @return
	 */
	public List<Map<String, Object>> getOrganPeerCount() ;

	/**
	 * 节点加入的channel列表
	 * @param String peerId
	 * @return
	 */
	public List<Map<String, Object>> getChannelListByPeerId(String peerId);
}
