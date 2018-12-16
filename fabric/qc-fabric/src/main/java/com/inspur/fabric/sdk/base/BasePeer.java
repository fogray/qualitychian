package com.inspur.fabric.sdk.base;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/18
 */
public class BasePeer {

    //peer节点域名
    private String peerName;

    private String orgName;
    //peer节点的地址
    private String peerLocation;
    //peer节点的事件监听地址
    private String peerEventHubLocation;
    //peer节点的ca地址
    private String caLocation;
    //当前peer是否增加Event事件处理
    private boolean addEventHub = false;

    public BasePeer(String peerName, String orgName, String peerLocation){
        this.peerName = peerName;
        this.orgName = orgName;
        this.peerLocation = peerLocation;
    }

    public BasePeer(String peerName, String orgName, String peerLocation, String caLocation){
        this.peerName = peerName;
        this.orgName = orgName;
        this.peerLocation = peerLocation;
        this.caLocation = caLocation;
    }

    public BasePeer(String peerName, String orgName, String peerLocation, String peerEventHubLocation, String caLocation) {
        this.peerName = peerName;
        this.orgName = orgName;
        this.peerLocation = peerLocation;
        this.peerEventHubLocation = peerEventHubLocation;
        this.caLocation = caLocation;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPeerLocation() {
        return peerLocation;
    }

    public void setPeerLocation(String peerLocation) {
        this.peerLocation = peerLocation;
    }

    public String getPeerEventHubLocation() {
        return peerEventHubLocation;
    }

    public void setPeerEventHubLocation(String peerEventHubLocation) {
        this.peerEventHubLocation = peerEventHubLocation;
    }

    public String getCaLocation() {
        return caLocation;
    }

    public void setCaLocation(String caLocation) {
        this.caLocation = caLocation;
    }

    public boolean isAddEventHub() {
        return addEventHub;
    }

    public void addEventHub(boolean addEventHub) {
        this.addEventHub = addEventHub;
    }

}
