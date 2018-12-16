package com.inspur.fabric.pub.tool;

import com.inspur.fabric.pub.cache.FabricCacheUtil;

/**
 * 账号处理工具类
 * 
 * 
 */
public class ChannelStatusUtil {
	
	/**
	 * 
	 * 功能描述: 解析Token
	 *
	 * 创建人: lijia
	 * 创建时间: 2018年1月30日 下午2:10:13
	 * @param token
	 * @return
	 */
	public static boolean checkAllChannelStatus() {
		String channel1Status = FabricCacheUtil.getChainChannelStatus("trace1");
    	String channel2Status = FabricCacheUtil.getChainChannelStatus("trace2");
    	String channel3Status = FabricCacheUtil.getChainChannelStatus("trace3");
    	String channel4Status = FabricCacheUtil.getChainChannelStatus("trace4");
    	String channel5Status = FabricCacheUtil.getChainChannelStatus("trace5");
    	String channel7Status = FabricCacheUtil.getChainChannelStatus("trace7");
    	if(!"0".equals(channel1Status)&&!"0".equals(channel2Status)&&
    	   !"0".equals(channel3Status)&&!"0".equals(channel4Status)&&
    	   !"0".equals(channel5Status)&&!"0".equals(channel7Status)){
    		return true;
    	}else{
    		return false;
    	}
	}
	
}
