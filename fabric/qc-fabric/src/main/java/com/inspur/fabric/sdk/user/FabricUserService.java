package com.inspur.fabric.sdk.user;

import com.v6.base.service.BaseServiceImpl;

import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/20
 */
public class FabricUserService extends BaseServiceImpl implements IFabricUserService {

    private IFabricUserDomain fabricUserDomain;

    public IFabricUserDomain getFabricUserDomain() {
        return fabricUserDomain;
    }

    public void setFabricUserDomain(IFabricUserDomain fabricUserDomain) {
        this.fabricUserDomain = fabricUserDomain;
    }

    @Override
    public Map<String, Object> getUserInfo(String userId) {
        return getFabricUserDomain().getUserInfo(userId);
    }

    @Override
    public Map<String, Object> getAdminInfo(String userId) {
        return getFabricUserDomain().getAdminInfo(userId);
    }

    @Override
    public int updateUserInfo(Map<String, Object> map) {
        return getFabricUserDomain().updateUserInfo(map);
    }

    @Override
    protected void initService() {

    }
}
