package com.inspur.fabric.channelpeer;

import java.util.List;
import java.util.Map;

import org.loushang.util.Page;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.service.BaseServiceImpl;

/**
 * 功能描述：chain_channel_peer Service 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-06
 * 修改人：
 * 修改时间：
 */
public class ChannelPeerService extends BaseServiceImpl implements IChannelPeerService {
	
	private static final long serialVersionUID = -445230413759867892L;
	private IChannelPeerDomain channelPeerDomain;
	
	/**
	 * 功能描述：删除 chain_channel_peer 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param Map paraMap
	 */
	public void deleteChainChannelPeer(final Map paraMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChannelPeerDomain().deleteChainChannelPeer(paraMap);
			}
		});
	}
	
	/**
	 * 功能描述：取得 chain_channel_peer 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param Map paraMap
	 * @return
	 */
	public Map<String, Object> getChainChannelPeer(Map paraMap) {
		return getChannelPeerDomain().getChainChannelPeer(paraMap);
	}

	/**
	 * 功能描述：查询 chain_channel_peer 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param map
	 * @return
	 */
	public Page getAllChainChannelPeer(Map<String, Object> map) {
		return getChannelPeerDomain().getAllChainChannelPeer(map);
	}
	
	/**
	 * 功能描述：插入 chain_channel_peer 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainChannelPeer(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChannelPeerDomain().insertChainChannelPeer(beanMap);
			}
		});
		return beanMap;
	}

	/**
	 * 功能描述：修改 chain_channel_peer 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-06
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainChannelPeer(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChannelPeerDomain().updateChainChannelPeer(beanMap);
			}
		});
		return beanMap;
	}

	@Override
	public List<Map<String, Object>> getChainChannelPeerCount() {
		return getChannelPeerDomain().getChainChannelPeerCount();
	}

	@Override
	public List<Map<String, Object>> getChainChannelOrgCount() {
		return getChannelPeerDomain().getChainChannelOrgCount();
	}
	
	@Override
	public Map<String, Object> getOrganizeCount() {
		return getChannelPeerDomain().getOrganizeCount();
	}
	
	@Override
	public List<Map<String, Object>> getOrganizePeer() {
		return getChannelPeerDomain().getOrganizePeer();
	}

	@Override
	public List<Map<String, Object>>  getChannelOrg(Map<String, Object> map) {
		return getChannelPeerDomain().getChannelOrg(map);
	}
	
	@Override
	protected void initService() {
		if (getChannelPeerDomain() == null) {
			throw new RuntimeException("ChainChannelPeerServiceImpl配置错误, 属性chainChannelPeerDomain不能为空");
		}
	}

	public IChannelPeerDomain getChannelPeerDomain() {
		return channelPeerDomain;
	}

	public void setChannelPeerDomain(IChannelPeerDomain channelPeerDomain) {
		this.channelPeerDomain = channelPeerDomain;
	}
	
	/**
	 * 企业节点数
	 * @return
	 */
	public List<Map<String, Object>> getOrganPeerCount() {
		return getChannelPeerDomain().getOrganPeerCount();
	}

	/**
	 * 节点加入的channel列表
	 * @param String peerId
	 * @return
	 */
	public List<Map<String, Object>> getChannelListByPeerId(String peerId) {
		return getChannelPeerDomain().getChannelListByPeerId(peerId);
	}

}
