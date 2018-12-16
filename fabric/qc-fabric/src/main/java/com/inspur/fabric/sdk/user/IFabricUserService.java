package com.inspur.fabric.sdk.user;

import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/20
 */
public interface IFabricUserService {
    /**
     * 根据userId获取用户信息（fabric相关的）
     * @param userId
     * @return
     */
    public Map<String, Object> getUserInfo(String userId);

    /**
     * 根据userId获取用户信息（如果该用户为运营商类型）
     * @param userId
     * @return
     */
    public Map<String, Object> getAdminInfo(String userId);

    /**
     * 更新用户信息（fabric用户密码、私钥、证书）
     * @param map
     * @return
     */
    public int updateUserInfo(Map<String, Object> map);
}
