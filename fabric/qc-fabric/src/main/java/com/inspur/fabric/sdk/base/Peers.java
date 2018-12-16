package com.inspur.fabric.sdk.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/18
 */
public class Peers {

    private String orgName;

    private String mspId;

    private String orgDomainName;

    private Map<String, BasePeer> peerMap;

    private List<BasePeer> peers;

    public Peers(String orgName, String mspId, String orgDomainName){
        this.orgName = orgName;
        this.mspId = mspId;
        this.orgDomainName = orgDomainName;
        peerMap = new HashMap<>();
        peers = new ArrayList<>();
    }

    public String getOrgName() {
        return orgName;
    }

    public String getMspId() {
        return mspId;
    }

    public String getOrgDomainName() {
        return orgDomainName;
    }

    public List<BasePeer> get(){
        return peers;
    }

    public BasePeer getPeer(String peerName){
        return peerMap.get(peerName);
    }

    public void addPeer(String peerName, String orgName, String peerLocation){
        BasePeer peer = new BasePeer(peerName, orgName, peerLocation);
        peerMap.put(peerName, peer);
        peers.add(peer);
    }
    public void addPeer(String peerName, String orgName, String peerLocation, String caLocation){
        BasePeer peer = new BasePeer(peerName, orgName, peerLocation, caLocation);
        peerMap.put(peerName, peer);
        peers.add(peer);
    }

    public void addPeer(String peerName, String orgName, String peerLocation, String peerEventHubLocation, String caLocation){
        BasePeer peer = new BasePeer(peerName,orgName,peerLocation,peerEventHubLocation,caLocation);
        peerMap.put(peerName, peer);
        peers.add(peer);
    }

}
