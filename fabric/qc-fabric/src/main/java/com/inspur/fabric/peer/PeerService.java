package com.inspur.fabric.peer;

import java.util.Map;

import org.loushang.util.Page;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.v6.base.service.BaseServiceImpl;

/**
 * 功能描述：fabric网络节点 Service 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-01-29
 * 修改人：
 * 修改时间：
 */
public class PeerService extends BaseServiceImpl implements IPeerService {
	
	private static final long serialVersionUID = 8606170648472413621L;
	private IPeerDomain peerDomain;
	
	/**
	 * 功能描述：删除 fabric网络节点 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param String peerName
	 */
	public void deletePeer(final String peerName) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getPeerDomain().deletePeer(peerName);
			}
		});
	}
	
	/**
	 * 功能描述：取得 fabric网络节点 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param String peerName
	 * @return
	 */
	public Map<String, Object> getPeer(String peerName) {
		return getPeerDomain().getPeer(peerName);
	}

	/**
	 * 功能描述：查询 fabric网络节点 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param map
	 * @return
	 */
	public Page getAllPeer(Map<String, Object> map) {
		return getPeerDomain().getAllPeer(map);
	}
	
	/**
	 * 功能描述：插入 fabric网络节点 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertPeer(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getPeerDomain().insertPeer(beanMap);
			}
		});
		return beanMap;
	}

	/**
	 * 功能描述：修改 fabric网络节点 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-29
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updatePeer(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getPeerDomain().updatePeer(beanMap);
			}
		});
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
		return getPeerDomain().getPeerById(peerId);
	}

	@Override
	protected void initService() {
		if (getPeerDomain() == null) {
			throw new RuntimeException("PeerServiceImpl配置错误, 属性peerDomain不能为空");
		}
	}
	
	public IPeerDomain getPeerDomain() {
		return peerDomain;
	}

	public void setPeerDomain(IPeerDomain peerDomain) {
		this.peerDomain = peerDomain;
	}
}
