package com.inspur.ether.trans;

import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

import com.v6.base.service.BaseServiceImpl;

/**
 * 功能描述：用户以太坊交易记录 Service 接口类
 * 创建人：yanghaiyong
 * 创建时间：2018-07-27
 * 修改人：
 * 修改时间：
 */
public class TransRecordService extends BaseServiceImpl implements ITransRecordService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ITransRecordDomain scanDomain;
	
	@Override
	protected void initService() {
		if (getTransRecordDomain() == null) {
			throw new RuntimeException("TransRecordServiceImpl配置错误, 属性scanDomain不能为空");
		}
	}
	
	public ITransRecordDomain getTransRecordDomain() {
		return scanDomain;
	}

	public void setTransRecordDomain(ITransRecordDomain scanDomain) {
		this.scanDomain = scanDomain;
	}

	/**
	 * 查询以太坊交易记录列表：分页
	 * @param param
	 * @return
	 */
	public Page getAllTransRecordPage(Map<String, Object> param) {
		return getTransRecordDomain().getAllTransRecordPage(param);
	}
	
	/**
	 * 查询以太坊交易记录列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryTransRecordList(Map<String, Object> param) {
		return getTransRecordDomain().queryTransRecordList(param);
	}
	
	/**
	 * 新增以太坊交易记录
	 * @param bean
	 * @return
	 */
	public int insertTrasnRecord(Map<String, Object> bean) {
		return getTransRecordDomain().insertTrasnRecord(bean);
	}

	/**
	 * 更新以太坊交易记录
	 * @param bean
	 * @return
	 */
	public int updateTransRecord(Map<String, Object> bean) {
		return getTransRecordDomain().updateTransRecord(bean);
	}
}
