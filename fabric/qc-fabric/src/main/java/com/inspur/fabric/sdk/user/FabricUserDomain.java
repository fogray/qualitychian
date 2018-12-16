package com.inspur.fabric.sdk.user;

import com.lc.v6.jdbc.mybatis.V6SqlSessionUtil;
import com.v6.base.domain.BaseDomainImpl;

import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/20
 */
public class FabricUserDomain extends BaseDomainImpl implements IFabricUserDomain {
    @Override
    public Map<String, Object> getUserInfo(String userId) {
        return V6SqlSessionUtil.getSqlSession().selectOne("FabricUserDomain.getUserInfo",userId);
    }

    @Override
    public Map<String, Object> getAdminInfo(String userId) {
        return V6SqlSessionUtil.getSqlSession().selectOne("FabricUserDomain.getAdminInfo",userId);
    }

    @Override
    public int updateUserInfo(Map<String, Object> map) {
        return V6SqlSessionUtil.getSqlSession().update("FabricUserDomain.updateUserInfo",map);
    }

    @Override
    protected void initDomain() {

    }
}
