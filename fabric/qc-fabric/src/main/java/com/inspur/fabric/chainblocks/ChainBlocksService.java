package com.inspur.fabric.chainblocks;

import java.util.Map;

import org.loushang.util.Page;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.v6.base.service.BaseServiceImpl;

/**
 * 功能描述：chain_blocks Service 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-04-03
 * 修改人：
 * 修改时间：
 */
public class ChainBlocksService extends BaseServiceImpl implements IChainBlocksService {
	
	private static final long serialVersionUID = 5176379976539630718L;
	private IChainBlocksDomain chainBlocksDomain;
	
	/**
	 * 功能描述：删除 chain_blocks 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param String id
	 */
	public void deleteChainBlocks(final String id) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChainBlocksDomain().deleteChainBlocks(id);
			}
		});
	}
	
	/**
	 * 功能描述：取得 chain_blocks 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param String id
	 * @return
	 */
	public Map<String, Object> getChainBlocks(String id) {
		return getChainBlocksDomain().getChainBlocks(id);
	}

	/**
	 * 功能描述：查询 chain_blocks 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param map
	 * @return
	 */
	public Page getAllChainBlocks(Map<String, Object> map) {
		return getChainBlocksDomain().getAllChainBlocks(map);
	}
	
	/**
	 * 功能描述：插入 chain_blocks 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainBlocks(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChainBlocksDomain().insertChainBlocks(beanMap);
			}
		});
		return beanMap;
	}

	/**
	 * 功能描述：修改 chain_blocks 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainBlocks(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChainBlocksDomain().updateChainBlocks(beanMap);
			}
		});
		return beanMap;
	}

	@Override
	protected void initService() {
		if (getChainBlocksDomain() == null) {
			throw new RuntimeException("ChainBlocksServiceImpl配置错误, 属性chainBlocksDomain不能为空");
		}
	}
	
	public IChainBlocksDomain getChainBlocksDomain() {
		return chainBlocksDomain;
	}

	public void setChainBlocksDomain(IChainBlocksDomain chainBlocksDomain) {
		this.chainBlocksDomain = chainBlocksDomain;
	}

	@Override
	public Map<String, Object> getChainBlockHight(String channel) {
		return getChainBlocksDomain().getChainBlockHight(channel);
	}
}
