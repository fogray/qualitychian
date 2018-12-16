package com.inspur.fabric.chaincode;

import java.util.List;
import java.util.Map;

import org.loushang.util.Page;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.v6.base.service.BaseServiceImpl;

/**
 * 功能描述：链码 Service 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-02-01
 * 修改人：
 * 修改时间：
 */
public class ChaincodeService extends BaseServiceImpl implements IChaincodeService {
	
	private static final long serialVersionUID = -4414340954307796781L;
	private IChaincodeDomain chaincodeDomain;
	
	/**
	 * 功能描述：删除 链码 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param String chaincodeId
	 */
	public void deleteChainChaincode(final String chaincodeId) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChaincodeDomain().deleteChainChaincode(chaincodeId);
			}
		});
	}
	
	/**
	 * 功能描述：取得 链码 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param String chaincodeId
	 * @return
	 */
	public Map<String, Object> getChainChaincode(String chaincodeId) {
		return getChaincodeDomain().getChainChaincode(chaincodeId);
	}
	@Override
	public List<Map<String, Object>> getChainChaincodeMap(Map<String, String> map) {
		return getChaincodeDomain().getChainChaincodeMap(map);
	}

	/**
	 * 功能描述：查询 链码 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param map
	 * @return
	 */
	public Page getAllChainChaincode(Map<String, Object> map) {
		return getChaincodeDomain().getAllChainChaincode(map);
	}
	
	/**
	 * 功能描述：插入 链码 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainChaincode(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChaincodeDomain().insertChainChaincode(beanMap);
			}
		});
		return beanMap;
	}

	/**
	 * 功能描述：修改 链码 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-02-01
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainChaincode(final Map<String, Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getChaincodeDomain().updateChainChaincode(beanMap);
			}
		});
		return beanMap;
	}

	@Override
	protected void initService() {
		if (getChaincodeDomain() == null) {
			throw new RuntimeException("ChainChaincodeServiceImpl配置错误, 属性chainChaincodeDomain不能为空");
		}
	}

	public IChaincodeDomain getChaincodeDomain() {
		return chaincodeDomain;
	}

	public void setChaincodeDomain(IChaincodeDomain chaincodeDomain) {
		this.chaincodeDomain = chaincodeDomain;
	}
	//获取链信息
	@Override
	public Map<String, Object> getChainChaincodeByParamsName(String paramsName) {
		
		return getChaincodeDomain().getChainChaincodeByParamsName(paramsName);
	}
	

}
