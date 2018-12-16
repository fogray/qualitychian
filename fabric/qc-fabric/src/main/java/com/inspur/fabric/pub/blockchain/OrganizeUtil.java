/**
 * Project Name:qc<br>
 * File Name:OrganizeUtil.java<br>
 * Package Name:com.inspur.pub.blockchain<br>
 * Date:2018年5月31日上午11:38:17<br>
 * Copyright (c) 2018, <a href='http://weibo.com/jspc'>jiang</a> All Rights Reserved.<br>
 *
 */
package com.inspur.fabric.pub.blockchain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author jiangdsh
 *
 */
public class OrganizeUtil {
	/**
	 * 
	 */
	public OrganizeUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 计算数据未同步的组织 
	 * @param channelHeightMap
	 * @return
	 */
	public static Map<String, String> getUnSyncOrg(Map<String, Object> channelHeightMap) {
		Map<String, String> orgUnSyncMap = new HashMap<String, String>(); //数据不同步组织Map
		for (Map.Entry<String, Object>  entry : channelHeightMap.entrySet()) {
			List<Map<String, Object>> cOrgs = (List<Map<String, Object>>)channelHeightMap.get(entry.getKey());
			Collections.sort(cOrgs, new Comparator() {
				public int compare(Object o1, Object o2) {
					Map<String, Object> map1 = (Map<String, Object>) o1;
					Map<String, Object> map2 = (Map<String, Object>) o2;
					Long b1 = (Long)map1.get("CHANNEL_BLOCK_HEIGHT");
					Long b2 = (Long)map2.get("CHANNEL_BLOCK_HEIGHT");
					if (b1 > b2) {
						return -1;
					} else if (b1 < b2) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			Long maxOrgBlockHeight = (Long)cOrgs.get(0).get("CHANNEL_BLOCK_HEIGHT");
			for(Map<String, Object> co : cOrgs) {
				String orgName = (String) co.get("ORGAN_NAME");
				if (orgUnSyncMap.containsKey(orgName)) {
					break;
				}
				long bh = (Long) co.get("CHANNEL_BLOCK_HEIGHT");
				if (bh < maxOrgBlockHeight) {
					//该组织存在不同步的channel
					orgUnSyncMap.put(orgName, "0");
				}
			}
		}
		return orgUnSyncMap;
	}
}
