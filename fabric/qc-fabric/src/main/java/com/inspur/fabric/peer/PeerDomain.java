package com.inspur.fabric.peer;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loushang.util.Page;

import com.v6.base.domain.BaseDomainImpl;
import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;

/**
 * 功能描述：fabric网络节点 Domain 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-01-29
 * 修改人：
 * 修改时间：
 */
public class PeerDomain extends BaseDomainImpl implements IPeerDomain {

	private static final long serialVersionUID = -2538514781239806687L;
	private static final Log log = LogFactory.getLog(PeerDomain.class);

	/**
	 * 功能描述：删除 fabric网络节点 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param String peerName
	 */
	public void deletePeer(String peerName) {
		V6SqlSessionUtil.getSqlSession().delete("PeerDomain.deletePeer", peerName);
	}

	/**
	 * 功能描述：取得 fabric网络节点 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param String peerName
	 * @return
	 */
	public Map<String, Object> getPeer(String peerName) {
		return V6SqlSessionUtil.getSqlSession().selectOne("PeerDomain.getPeer", peerName);
	}
	
	/**
	 * 功能描述：查询 fabric网络节点 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param map
	 * @return
	 */
	public Page getAllPeer(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("PeerDomain.getAllPeerPage", map);
		return PageUtil.createPage(map, datas);
	}
	
	/**
	 * 功能描述：插入 fabric网络节点 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertPeer(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().insert("PeerDomain.insertPeer", beanMap);
		return beanMap;
	}
	
	/**
	 * 功能描述：修改 fabric网络节点 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updatePeer(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().update("PeerDomain.updatePeer", beanMap);
		return beanMap;
	}

	/**
	 * 功能描述：取得 fabric网络节点 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param String peerId
	 * @return
	 */
	public Map<String, Object> getPeerById(String peerId) {
		return V6SqlSessionUtil.getSqlSession().selectOne("PeerDomain.getPeerById", peerId);
	}

	@Override
	protected void initDomain() {
	}
}
