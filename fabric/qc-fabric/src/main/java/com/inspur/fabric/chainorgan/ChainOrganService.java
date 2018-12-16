package com.inspur.fabric.chainorgan;

import java.util.List;
import java.util.Map;

import org.loushang.util.Page;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.service.BaseServiceImpl;

/**
 * 功能描述：组织 Service 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-01-30
 * 修改人：
 * 修改时间：
 */
public class ChainOrganService extends BaseServiceImpl implements IChainOrganService {
	
	private static final long serialVersionUID = -4191154975094954220L;
	private IChainOrganDomain chainOrganDomain;
	
	/**
	 * 功能描述：删除 组织 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param String organId
	 */
	public void deleteChainOrgan(final String organId) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChainOrganDomain().deleteChainOrgan(organId);
			}
		});
	}
	
	/**
	 * 功能描述：取得 组织 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param String organId
	 * @return
	 */
	public Map<String, Object> getChainOrgan(String organId) {
		return getChainOrganDomain().getChainOrgan(organId);
	}

	/**
	 * 功能描述：查询 组织 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param map
	 * @return
	 */
	public Page getAllChainOrgan(Map<String, Object> map) {
		return getChainOrganDomain().getAllChainOrgan(map);
	}
	
	/**
	 * 功能描述：插入 组织 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainOrgan(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChainOrganDomain().insertChainOrgan(beanMap);
			}
		});
		return beanMap;
	}

	/**
	 * 功能描述：修改 组织 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-01-30
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainOrgan(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChainOrganDomain().updateChainOrgan(beanMap);
			}
		});
		return beanMap;
	}
	
	@Override
	public List getChainOrgan(Map<String, Object> map) {
		return getChainOrganDomain().getChainOrgan(map);
	}

	@Override
	public long getAllChainOrganSize() {
		return getChainOrganDomain().getAllChainOrganSize();
	}
	
	@Override
	protected void initService() {
		if (getChainOrganDomain() == null) {
			throw new RuntimeException("ChainOrganServiceImpl配置错误, 属性chainOrganDomain不能为空");
		}
	}
	
	public IChainOrganDomain getChainOrganDomain() {
		return chainOrganDomain;
	}

	public void setChainOrganDomain(IChainOrganDomain chainOrganDomain) {
		this.chainOrganDomain = chainOrganDomain;
	}


	/**
	 * 获取指定组织加入的所有channel
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getAllChannelByOrgan(Map<String, Object> map) {
		return getChainOrganDomain().getAllChannelByOrgan(map);
	}
	
}
