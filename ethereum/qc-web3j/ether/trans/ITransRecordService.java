package com.inspur.ether.trans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

/**
 * 功能描述：用户以太坊交易记录 Service 接口类
 * 创建人：yanghaiyong
 * 创建时间：2018-07-27
 * 修改人：
 * 修改时间：
 */
public interface ITransRecordService extends Serializable{
	
	/**
	 * 查询以太坊交易记录列表：分页
	 * @param param
	 * @return
	 */
	public Page getAllTransRecordPage(Map<String, Object> param);
	
	/**
	 * 查询以太坊交易记录列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryTransRecordList(Map<String, Object> param);
	
	/**
	 * 新增以太坊交易记录
	 * @param bean
	 * @return
	 */
	public int insertTrasnRecord(Map<String, Object> bean);

	/**
	 * 更新以太坊交易记录
	 * @param bean
	 * @return
	 */
	public int updateTransRecord(Map<String, Object> bean);
}
