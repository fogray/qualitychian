package com.inspur.fabric.transaction;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loushang.util.Page;

import com.v6.base.domain.BaseDomainImpl;
import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;

/**
 * 功能描述：交易 Domain 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-05
 * 修改人：
 * 修改时间：
 */
public class TransactionDomain extends BaseDomainImpl implements ITransactionDomain {

	private static final long serialVersionUID = -7157088407228753846L;
	private static final Log log = LogFactory.getLog(TransactionDomain.class);

	/**
	 * 功能描述：删除 交易 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param String id
	 */
	public void deleteTransaction(String id) {
		V6SqlSessionUtil.getSqlSession().delete("TransactionDomain.deleteTransaction", id);//sqlSessionFactory_fabric
	}

	/**
	 * 功能描述：取得 交易 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param String id
	 * @return
	 */
	public Map<String, Object> getTransaction(String id) {
		return V6SqlSessionUtil.getSqlSession().selectOne("TransactionDomain.getTransaction", id);
	}
	
	public Map<String, Object> getTranbyHash(String hash) {
		return V6SqlSessionUtil.getSqlSession().selectOne("TransactionDomain.getTranbyHash", hash);
	}
	
	/**
	 * 功能描述：查询 交易 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param map
	 * @return
	 */
	public Page getAllTransaction(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("TransactionDomain.getAllTransactionPage", map);
		return PageUtil.createPage(map, datas);
	}
	
	/**
	 * 功能描述：插入 交易 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertTransaction(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().insert("TransactionDomain.insertTransaction", beanMap);
		return beanMap;
	}
	
	/**
	 * 功能描述：修改 交易 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-05
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateTransaction(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().update("TransactionDomain.updateTransaction", beanMap);
		return beanMap;
	}

	@Override
	protected void initDomain() {
	}

	@Override
	public Map<String, Object> getTransactionCount(Map<String, String> map) {
		return V6SqlSessionUtil.getSqlSession().selectOne("TransactionDomain.getTransactionCount", map);
	}

	@Override
	public Page getTransactionTop(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("TransactionDomain.getTransactionTopPage", map);
		return PageUtil.createPage(map, datas);
	}
}
