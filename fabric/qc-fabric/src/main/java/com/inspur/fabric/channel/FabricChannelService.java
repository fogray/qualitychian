package com.inspur.fabric.channel;

import com.v6.base.service.BaseServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loushang.util.Page;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：数据审核记录表 Service 实现类
 * 创建人：DESKTOP-PK95QHU
 * 创建时间：2017-12-22
 * 修改人：
 * 修改时间：
 */
public class FabricChannelService extends BaseServiceImpl implements IFabricChannelService {
	
	private static final long serialVersionUID = -1701025214421302448L;

	private static final Log log = LogFactory.getLog(FabricChannelService.class);
	
	private IFabricChannelDomain fabricChannelDomain;

	public IFabricChannelDomain getFabricChannelDomain() {
		return fabricChannelDomain;
	}

	public void setFabricChannelDomain(IFabricChannelDomain fabricChannelDomain) {
		this.fabricChannelDomain = fabricChannelDomain;
	}

	/**
	 * 功能描述：删除 数据审核记录表 一条记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param String testId
	 * @see com.inspur.base.producttest.ITestService#deleteTest(String)
	 */
	public void deleteFabricChannel(final String id) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getFabricChannelDomain().deleteFabricChannel(id);
			}
		});
	}

	/**
	 * 功能描述：取得 数据审核记录表 一条明细
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param String testId
	 * @return
	 * @see com.inspur.base.producttest.ITestService#getTest(String)
	 */
	public Map<String,Object> getFabricChannel(String id) {
		return getFabricChannelDomain().getFabricChannel(id);
	}

	/**
	 * 功能描述：查询 数据审核记录表 一页记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param map
	 * @return
	 * @see com.inspur.base.producttest.ITestService#getAllTest(Map)
	 */
	public List<Map<String,Object>> getAllFabricChannel() {
		return getFabricChannelDomain().getAllFabricChannel();
	}
	/**
	 * 通过channelName获取链状态
	 * */
	public Map<String,Object> getFabricChannelByChannelName(Map<String, String> map) {
		 return getFabricChannelDomain().getFabricChannelMap(map);
	}
	/**
	 * 功能描述：插入 数据审核记录表 一条记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param beanMap
	 * @return
	 * @see com.inspur.base.producttest.ITestService#insertTest(Map)
	 */
	public Map<String,Object> insertFabricChannel(final Map<String,Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getFabricChannelDomain().insertFabricChannel(beanMap);
			}
		});
		return beanMap;
	}
	@Override
	public Map<String, Object> getFabricChannelMap(Map<String, String> map) {
		return getFabricChannelDomain().getFabricChannelMap(map);
	}

	@Override
	public Page getAllFabricChannel(Map<String, Object> map) {
		return getFabricChannelDomain().getAllFabricChannel(map);
	}

	/**
	 * 功能描述：修改 数据审核记录表 一条记录
	 * 创建人：DESKTOP-PK95QHU
	 * 创建时间：2017-12-22
	 * @param beanMap
	 * @return
	 * @see com.inspur.base.producttest.ITestService#updateTest(Map)
	 */
	public Map<String,Object> updateFabricChannel(final Map<String,Object> beanMap) {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				getFabricChannelDomain().updateFabricChannel(beanMap);
			}
		});
		return beanMap;
	}

	/**
	 * 获取所有有效的通道
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getValidChannels() {
		return getFabricChannelDomain().getValidChannels();
	}

	@Override
	protected void initService() {
		if (getFabricChannelDomain() == null) {
			throw new RuntimeException("FabricChannelServiceImpl配置错误, 属性fabricChannelDomain不能为空");
		}
	}


}
