package com.inspur.fabric.chainblocks;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loushang.util.Page;

import com.v6.base.domain.BaseDomainImpl;
import com.lc.v6.jdbc.mybatis.PageUtil;
import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;

/**
 * 功能描述：chain_blocks Domain 实现类
 * 创建人：JIANGDSH01
 * 创建时间：2018-04-03
 * 修改人：
 * 修改时间：
 */
public class ChainBlocksDomain extends BaseDomainImpl implements IChainBlocksDomain {

	private static final long serialVersionUID = -1645818064989433996L;
	private static final Log log = LogFactory.getLog(ChainBlocksDomain.class);

	/**
	 * 功能描述：删除 chain_blocks 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param String id
	 */
	public void deleteChainBlocks(String id) {
		V6SqlSessionUtil.getSqlSession().delete("ChainBlocksDomain.deleteChainBlocks", id);
	}

	/**
	 * 功能描述：取得 chain_blocks 一条明细
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param String id
	 * @return
	 */
	public Map<String, Object> getChainBlocks(String id) {
		return V6SqlSessionUtil.getSqlSession().selectOne("ChainBlocksDomain.getChainBlocks", id);
	}
	/**
	 * 查询某个通道/链的高度
	 * @param channel
	 * @return
	 */
	public Map<String, Object> getChainBlockHight(String channel) {
		return V6SqlSessionUtil.getSqlSession().selectOne("ChainBlocksDomain.getChainBlockHight", channel);
	}
	
	/**
	 * 功能描述：查询 chain_blocks 一页记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param map
	 * @return
	 */
	public Page getAllChainBlocks(Map<String, Object> map) {
		List datas = V6SqlSessionUtil.getSqlSession().selectList("ChainBlocksDomain.getAllChainBlocksPage", map);
		return PageUtil.createPage(map, datas);
	}
	
	/**
	 * 功能描述：插入 chain_blocks 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> insertChainBlocks(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().insert("ChainBlocksDomain.insertChainBlocks", beanMap);
		return beanMap;
	}
	
	/**
	 * 功能描述：修改 chain_blocks 一条记录
	 * 创建人：JIANGDSH01
	 * 创建时间：2018-04-03
	 * @param beanMap
	 * @return
	 */
	public Map<String, Object> updateChainBlocks(Map<String, Object> beanMap) {
		V6SqlSessionUtil.getSqlSession().update("ChainBlocksDomain.updateChainBlocks", beanMap);
		return beanMap;
	}

	@Override
	protected void initDomain() {
	}
}
