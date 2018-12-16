package com.inspur.fabric.peer;

import java.io.Serializable;
import java.util.Map;

import org.loushang.util.Page;

/**
 * 功能描述：fabric网络节点 Service 接口类
 * 创建人：JIANGDSH01
 * 创建时间：2018-01-29
 * 修改人：
 * 修改时间：
 */
public interface IPeerService extends Serializable{

	/**
	 * 功能描述: 删除 fabric网络节点 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-29
	 * @param String peerName
	 */
	public void deletePeer(String peerName);

	/**
	 * 功能描述: 取得 fabric网络节点 一条明细
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-29
	 * @param String peerName
	 */
	public Map<String, Object> getPeer(String peerName);

    /**
	 * 功能描述: 查询 fabric网络节点 一页记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-29
	 * @param map
	 */
	public Page getAllPeer(Map<String, Object> map);

	/**
	 * 功能描述: 插入 fabric网络节点 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-29
	 * @param beanMap
	 */
	public Map<String, Object> insertPeer(Map<String, Object> beanMap);

    /**
	 * 功能描述: 修改 fabric网络节点 一条记录
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-29
	 * @param beanMap
	 */
	public Map<String, Object> updatePeer(Map<String, Object> beanMap);

	/**
	 * 功能描述: 取得 fabric网络节点 一条明细
	 * 创建人: JIANGDSH01
	 * 创建时间: 2018-01-29
	 * @param String peerId
	 */
	public Map<String, Object> getPeerById(String peerId);
}
