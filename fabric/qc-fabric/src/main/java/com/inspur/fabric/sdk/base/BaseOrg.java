package com.inspur.fabric.sdk.base;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/18
 */
public class BaseOrg {

    private String name;

    private String mspId;

    private HFCAClient caClient;

    private HFClient client;

    private BaseUser admin;

    private BaseUser peerAdmin;

    private BaseUser userContext;

    private Peers peers;

    private Orderers orderers;

    public BaseOrg(String name) throws CryptoException, InvalidArgumentException {
        this.name = name;
        this.client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
    }

    public String getName() {
        return name;
    }

    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId){
        this.mspId = mspId;
    }

    public HFCAClient getCaClient() {
        return caClient;
    }

    public void setCaClient(HFCAClient caClient) {
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        this.caClient = caClient;
    }

    public HFClient getClient() {
        return client;
    }

    public void setClient(HFClient client) {
        this.client = client;
    }

    public BaseUser getAdmin() {
        return admin;
    }

    public void setAdmin(BaseUser admin) {
        this.admin = admin;
    }

    public BaseUser getPeerAdmin() {
        return peerAdmin;
    }

    public void setPeerAdmin(BaseUser peerAdmin) {
        this.peerAdmin = peerAdmin;
    }

    public BaseUser getUserContext() {
        return userContext;
    }

    public void setUserContext(BaseUser userContext) throws InvalidArgumentException {
        this.userContext = userContext;
        client.setUserContext(userContext);
    }

    public Peers getPeers() {
        return peers;
    }

    public void setPeers(Peers peers) {
        this.peers = peers;
    }

    public Orderers getOrderers() {
        return orderers;
    }

    public void setOrderers(Orderers orderers) {
        this.orderers = orderers;
    }

}
