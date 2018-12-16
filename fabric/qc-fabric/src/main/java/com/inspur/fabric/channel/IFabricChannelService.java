package com.inspur.fabric.channel;

import org.loushang.util.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：数据审核记录表 Service 接口类
 * 创建人：DESKTOP-PK95QHU
 * 创建时间：2017-12-22
 * 修改人：
 * 修改时间：
 */
public interface IFabricChannelService extends Serializable{
	/**
	 * query page 
	 * @param map
	 * @return
	 */
	public Page getAllFabricChannel(Map<String, Object> map);

	/**
	 * 功能描述: 删除 数据审核记录表 一条记录
	 * 创建人: DESKTOP-PK95QHU
	 * 创建时间: 2017-12-22
	 * @param String testId
	 */
	public void deleteFabricChannel(String id);

	/**
	 * 功能描述: 取得 数据审核记录表 一条明细
	 * 创建人: DESKTOP-PK95QHU
	 * 创建时间: 2017-12-22
	 * @param String testId
	 */
	public Map<String,Object> getFabricChannel(String id);
	public Map<String,Object> getFabricChannelMap(Map<String, String> map);

    /**
	 * 功能描述: 查询 数据审核记录表 一页记录
	 * 创建人: DESKTOP-PK95QHU
	 * 创建时间: 2017-12-22
	 * @param map
	 */
	public List<Map<String,Object>> getAllFabricChannel();
	/**
	 * 通过channelName获取链状态
	 * */
	public Map<String,Object> getFabricChannelByChannelName(Map<String, String> map);
	/**
	 * 功能描述: 插入 数据审核记录表 一条记录
	 * 创建人: DESKTOP-PK95QHU
	 * 创建时间: 2017-12-22
	 * @param beanMap
	 */
	public Map<String,Object> insertFabricChannel(Map<String, Object> beanMap);

    /**
	 * 功能描述: 修改 数据审核记录表 一条记录
	 * 创建人: DESKTOP-PK95QHU
	 * 创建时间: 2017-12-22
	 * @param beanMap
	 */
	public Map<String,Object> updateFabricChannel(Map<String, Object> beanMap);

	/**
	 * 获取所有有效的通道
	 * @return
	 */
	public List<Map<String, Object>> getValidChannels();
}
