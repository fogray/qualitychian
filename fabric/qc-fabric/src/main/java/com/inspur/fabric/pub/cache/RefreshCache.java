package com.inspur.fabric.pub.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.loushang.waf.ComponentFactory;

import com.inspur.fabric.chaincode.IChaincodeService;
import com.inspur.fabric.chainorgan.IChainOrganService;
import com.inspur.fabric.channel.IFabricChannelService;
import com.inspur.fabric.peer.IPeerService;
import com.inspur.pub.cache.ICache;
import com.inspur.pub.cache.SimpCache;

/**
 * 刷新缓存中存放的常用数据，如参数、字典数据、region信息等
 * @author zhangjian
 */
public class RefreshCache {
	/** fabric 组织  */
	public static final ICache chainOrgans = new SimpCache();

	/** fabric peer  */
	static final ICache chainPeers = new SimpCache();
	static final ICache chainCodes = new SimpCache();
	static final ICache chainChannels = new SimpCache();
	static final ICache chainChannelsStatus = new SimpCache();
	static final ICache chainConsensus = new SimpCache();
	static final ICache chcodeChannels = new SimpCache();

	static final ICache chainChaincodeVersion = new SimpCache();
	
	static {
		//初始化缓存
		refreshAll();
		
		//启动定时刷新任务，每10分钟刷新一次
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				RefreshCache.refreshAll();
			}
		}, 10, 10, TimeUnit.MINUTES);
	}

	/**
	 * 刷新缓存
	 */
	public static void refreshAll() {
		//加载fabric的组织
		refreshChainOrgans();
		//加载fabric code 
		refreshChainCodes();
		refreshChainChannels();
		refreshChainChannelsStatus();
		refreshChainPeers();
	}
	
	/**
	 * list to map,转成一个大的map,<br>类似于trace1=1,trace2=2
	 * @param list
	 * @param key 获取key字段名
	 * @param value 获取value字段名
	 * @return
	 */
    public static Map<String, Object> list2Map(List<Map<String, Object>> list ,String key ,String value) {  
        Map<String, Object> map = new HashMap<String, Object>();  
        for (Map<String, Object> organParam : list) {
			map.put((String)organParam.get(key), organParam.get(value));
		} 
        return map;
    } 
    
	/**
	 * 加载channel/链 数据
	 */
    public static void refreshChainChannels() {
    	IFabricChannelService fabricChannelService = (IFabricChannelService)ComponentFactory.getBean("fabricChannelService");
		List<Map<String, Object>> allChannel = fabricChannelService.getAllFabricChannel();
		Map<String, Object> organParamCache = list2Map(allChannel, "CHANNEL_NAME", "CHANNEL_DESC");
		chainChannels.setCachedData(organParamCache);
    }
    /**
	 * 加载channel/链 状态
	 */
    public static void refreshChainChannelsStatus() {
    	IFabricChannelService fabricChannelService = (IFabricChannelService)ComponentFactory.getBean("fabricChannelService");
		List<Map<String, Object>> allChannel = fabricChannelService.getAllFabricChannel();
		Map<String, Object> organParamCache = list2Map(allChannel, "CHANNEL_NAME", "CHANNEL_STATUS");
		chainChannelsStatus.setCachedData(organParamCache);
    }
	/**
	 * 加载fabric组织数据
	 */
	public static void refreshChainOrgans() {
		IChainOrganService chainOrganService = (IChainOrganService)ComponentFactory.getBean("chainOrganService");
		List<Map<String, Object>> chainOrgan = chainOrganService.getChainOrgan(new HashMap());
		
		Map<String, Object> organParamCache = new HashMap<String, Object>();
		if (null != chainOrgan && chainOrgan.size() > 0) {
			for (Map<String, Object> organParam : chainOrgan) {
				organParamCache.put((String)organParam.get("MSP_ID"), organParam.get("ORGAN_DESC"));
			}
		}
		chainOrgans.setCachedData(organParamCache);
	}
	
	/**
	 * 加载fabric peer数据
	 */
	public static void refreshChainPeers() {
		IPeerService peerService = (IPeerService)ComponentFactory.getBean("peerService");
		List<Map<String, Object>> peerList = peerService.getAllPeer(new HashMap()).getDatas();
		
		Map<String, Object> organParamCache = new HashMap<String, Object>();
		for (Map<String, Object> organParam : peerList) {
			organParamCache.put((String)organParam.get("ORGAN_NAME"), organParam.get("ORGAN_DESC"));
		}
		chainPeers.setCachedData(organParamCache);
	}
	/**
	 * 加载chain code
	 */
	public static void refreshChainCodes() {
		IChaincodeService chaincodeService = (IChaincodeService)ComponentFactory.getBean("chaincodeService");
		List<Map<String, Object>> codeList = chaincodeService.getAllChainChaincode(new HashMap()).getDatas();
		
		Map<String, Object> organParamCache = new HashMap<String, Object>();
		Map<String, Object> codechannelCache = new HashMap<String, Object>();
		Map<String, Object> chainConsensusCache = new HashMap<String, Object>();
		Map<String, Object> chainChaincodeCache = new HashMap<String, Object>();
		for (Map<String, Object> organParam : codeList) {
			organParamCache.put((String)organParam.get("CHAINCODE_NAME"), organParam.get("CHAINCODE_DESC"));
			codechannelCache.put((String)organParam.get("CHAINCODE_NAME"), organParam.get("CHANNEL_NAME"));
			chainConsensusCache.put((String)organParam.get("CHAINCODE_NAME"), organParam.get("CONSENSUS_DESC"));
			chainChaincodeCache.put((String)organParam.get("CHAINCODE_NAME"), organParam.get("VERSION"));
		}
		chainCodes.setCachedData(organParamCache);
		chcodeChannels.setCachedData(codechannelCache);
		chainConsensus.setCachedData(chainConsensusCache);
		chainChaincodeVersion.setCachedData(chainChaincodeCache);
	}
	
}
