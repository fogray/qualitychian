package com.inspur.ether.trans;

import java.util.List;
import java.util.Map;

import org.loushang.util.Page;

import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.domain.BaseDomainImpl;

/**
 * 功能描述：任务扫码记录 Domain 实现类
 * 创建人：QIPENGTAO00
 * 创建时间：2018-07-11
 * 修改人：
 * 修改时间：
 */
public class TransRecordDomain extends BaseDomainImpl implements ITransRecordDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void initDomain() {
	}

	@Override
	public Page getAllTransRecordPage(Map<String, Object> param) {
		List<Map<String, Object>> datas = V6SqlSessionUtil.getSqlSession().selectList("TransRecordDomain.getAllTransRecordPage", param);
		return PageUtil.createPage(param, datas);
	}

	@Override
	public List<Map<String, Object>> queryTransRecordList(Map<String, Object> param) {
		return V6SqlSessionUtil.getSqlSession().selectList("TransRecordDomain.queryTransRecordList", param);
	}

	@Override
	public int insertTrasnRecord(Map<String, Object> bean) {
		return V6SqlSessionUtil.getSqlSession().insert("TransRecordDomain.insertTrasnRecord", bean);
	}

	@Override
	public int updateTransRecord(Map<String, Object> bean) {
		return V6SqlSessionUtil.getSqlSession().update("TransRecordDomain.updateTransRecord", bean);
	}

}
