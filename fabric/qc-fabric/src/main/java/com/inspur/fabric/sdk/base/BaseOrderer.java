package com.inspur.fabric.sdk.base;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/18
 */
public class BaseOrderer {
    //orderer节点的域名
    private String ordererName;
    //orderer节点的地址
    private String ordererLocation;

    public BaseOrderer(String ordererName, String ordererLocation){
        this.ordererName = ordererName;
        this.ordererLocation = ordererLocation;
    }

    public String getOrdererName() {
        return ordererName;
    }

    public void setOrdererName(String ordererName) {
        this.ordererName = ordererName;
    }

    public String getOrdererLocation() {
        return ordererLocation;
    }

    public void setOrdererLocation(String ordererLocation) {
        this.ordererLocation = ordererLocation;
    }

}
