package com.inspur.fabric.pub.cache;

import java.util.Map;

import org.loushang.waf.ComponentFactory;

import com.inspur.base.ruledata.IRuleDataService;

/**
 * 缓存，用于存放常用参数、region信息等
 * @author zhangjian
 *  增加校验规则缓存,防止每次请求接口,重复请求数据表
 */
public class FabricCacheUtil {

	/**
	 * ORGAN_NAME-->ORGAN_DESC
	 * @param paramsName peerOrg1
	 * @return	浪潮
	 */
	public static String getChainPeer(String paramsName) {
		return (String) RefreshCache.chainPeers.get(paramsName);
	}

	/**
	 * CHAINCODE_NAME-->CHAINCODE_DESC
	 * @param paramsName qc_org_cc
	 * @return 企业备案
	 */
	public static String getChainCode(String paramsName) {
		return (String) RefreshCache.chainCodes.get(paramsName);
	}

	/**
	 * CHANNEL_NAME-->CHANNEL_DESC
	 * @param paramsName trace1
	 * @return 基础链
	 */
	public static String getChainChannel(String paramsName) {
		return (String) RefreshCache.chainChannels.get(paramsName);
	}

	/**
	 * CHANNEL_NAME-->CHANNEL_STATUS
	 * @param paramsName trace1
	 * @return 0/1
	 */
	public static String getChainChannelStatus(String paramsName){
		return (String) RefreshCache.chainChannelsStatus.get(paramsName);
	}

	/**
	 * CHAINCODE_NAME-->CONSENSUS_DESC
	 * @param paramsName qc_medicine_cc
	 * @return peerOrg1,peerOrg2,peerOrg9
	 */
	public static String getChainConsensus(String paramsName) {
		return (String) RefreshCache.chainConsensus.get(paramsName);
	}
	//改为数据库直接查询
	//获取链信息
	/**
	 * CHAINCODE_NAME-->CHANNEL_NAME
	 * @param paramsName qc_org_cc
	 * @return trace1
	 */
	public static String getChainCodeChannel(String paramsName) {
		return (String) RefreshCache.chcodeChannels.get(paramsName);
	}

	/**
	 * 获取fabric组织名称  MSP_ID-->ORGAN_DESC
	 * @param paramsName Org1MSP
	 * @return 浪潮
	 */
	public static String getChainOrgan(String paramsName) {
		return (String) RefreshCache.chainOrgans.get(paramsName);
	}
	
	/**
	 * 获取经营主体商品缓存对象
	 * @param id
	 * @return
	 */
	//改为数据库直接查询
	public static Map<String, Object> getBusiGoods(String id){
		IRuleDataService ruleDataService = (IRuleDataService)ComponentFactory.getBean("ruleDataService");
		Map<String, Object> map = ruleDataService.getBusiGoodsById(id);
		return map;
	}

	/**
	 * 获取链码版本 CHAINCODE_NAME-->VERSION
	 * @param paramsName qc_org_cc
	 * @return	1.0
	 */
	public static String getChaincodeVersion(String paramsName) {
		return (String) RefreshCache.chainChaincodeVersion.get(paramsName);
	}
	
}
