package com.inspur.fabric.transaction;

import java.util.Map;

import org.loushang.util.Page;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.v6.base.service.BaseServiceImpl;

/**
 * 功能描述：交易 Service 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-05
 * 修改人：
 * 修改时间：
 */
public class TransactionService extends BaseServiceImpl implements ITransactionService {
	
	private static final long serialVersionUID = -7845209382726503557L;
	private ITransactionDomain transactionDomain;
	
	/**
	 * 功能描述：删除 交易 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param String id
	 */
	public void deleteTransaction(final String id) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getTransactionDomain().deleteTransaction(id);
			}
		});
	}
	
	/**
	 * 功能描述：取得 交易 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param String id
	 * @return
	 */
	public Map<String, Object> getTransaction(String id) {
		return getTransactionDomain().getTransaction(id);
	}

	/**
	 * 功能描述：查询 交易 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param map
	 * @return
	 */
	public Page getAllTransaction(Map<String, Object> map) {
		return getTransactionDomain().getAllTransaction(map);
	}
	
	/**
	 * 功能描述：插入 交易 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertTransaction(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getTransactionDomain().insertTransaction(beanMap);
			}
		});
		return beanMap;
	}

	/**
	 * 功能描述：修改 交易 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateTransaction(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getTransactionDomain().updateTransaction(beanMap);
			}
		});
		return beanMap;
	}

	@Override
	public Map<String, Object> getTranbyHash(String hash) {
		return getTransactionDomain().getTranbyHash(hash);
	}
	
	@Override
	protected void initService() {
		if (getTransactionDomain() == null) {
			throw new RuntimeException("TransactionServiceImpl配置错误, 属性transactionDomain不能为空");
		}
	}
	
	public ITransactionDomain getTransactionDomain() {
		return transactionDomain;
	}

	public void setTransactionDomain(ITransactionDomain transactionDomain) {
		this.transactionDomain = transactionDomain;
	}

	@Override
	public Map<String, Object> getTransactionCount(Map<String, String> map) {
		return getTransactionDomain().getTransactionCount(map);
	}

	@Override
	public Page getTransactionTop(Map<String, Object> map) {
		return getTransactionDomain().getTransactionTop(map);
	}

}
