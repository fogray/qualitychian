package com.inspur.fabric.sdk.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/18
 */
public class Orderers {
    private String ordererDomainName;

    //orderer节点集合
    private List<BaseOrderer> orderers;

    public Orderers(){
        this.orderers = new ArrayList<BaseOrderer>();
    }

    public String getOrdererDomainName() {
        return ordererDomainName;
    }

    public void setOrdererDomainName(String ordererDomainName) {
        this.ordererDomainName = ordererDomainName;
    }

    public void addOrderer(String name, String location){
        orderers.add(new BaseOrderer(name, location));
    }

    public List<BaseOrderer> get(){
        return orderers;
    }
}
