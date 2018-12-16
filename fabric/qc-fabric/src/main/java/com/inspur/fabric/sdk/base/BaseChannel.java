package com.inspur.fabric.sdk.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/18
 */
public class BaseChannel {
    private static final Log log = LogFactory.getLog(BaseChannel.class);

    private static ConcurrentHashMap<String, BaseChannel> instanceCache = new ConcurrentHashMap<>();

    public static BaseChannel getInstance(String channelName, String userName, List<String> orgNames) throws Exception {

        String key = userName+"-"+channelName;
        if(!instanceCache.containsKey(key)){
            synchronized (BaseChannel.class){
                if(!instanceCache.containsKey(key)){
                    BaseChannel channel = new BaseChannel(channelName,userName,orgNames);
                    if (channel != null){
                        instanceCache.put(key, channel);
                    }
                }
            }
        }
        return instanceCache.get(key);
    }

    private String channelName;

    private Channel channel;

    private HFClient client;

    private Orderers orderers;

    private String orgName;

    private List<String> orgNames;

    private BaseUser user;

    private BaseChannel(String channelName, String userName, List<String> orgNames) throws Exception {
        this.channelName = channelName;
        if (log.isDebugEnabled()){
            log.debug("BaseChannel constructor begin");
            log.debug("BaseChannel channelName-->"+channelName);
        }
        log.error("BaseChannel channelName-->"+channelName);
        BaseUser user = UserManager.getInstance().register(userName);
        BaseOrg org = FabricManager.getConfig().getOrg(user.getOrgName());
        this.orgName = org.getName();
        this.orderers = org.getOrderers();
        this.orgNames = orgNames;

        client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(user);
        channel = client.newChannel(channelName);

        setOrderers();
        setPeers();
        if (!channel.isInitialized()) {
            channel.initialize();
        }
    }

    public Channel getChannel(){
        return channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public HFClient getClient() {
        return client;
    }

    public Orderers getOrderers() {
        return orderers;
    }

    public List<String> getOrgs() {
        return orgNames;
    }

    public void setOrderers() throws InvalidArgumentException {
        BaseOrderer anOrderer = orderers.get().get(0);
        String ordererName = anOrderer.getOrdererName();
        String ordererLocation = anOrderer.getOrdererLocation();
        Properties ordererProperties = FabricManager.getConfig().getOrdererProperties(ordererName);
        channel.addOrderer(client.newOrderer(ordererName,ordererLocation,ordererProperties));
    }

    public void setPeers() throws InvalidArgumentException {
        for (String orgName: orgNames){
            BaseOrg org = FabricManager.getConfig().getOrg(orgName);
            Peers peers = org.getPeers();
            for(BasePeer peer : peers.get()){
                String peerName = peer.getPeerName();
                String peerLocation = peer.getPeerLocation();
                Properties peerProperties = FabricManager.getConfig().getPeerProperties(peerName);
                peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);

                String eventHubLocation = peer.getPeerEventHubLocation();
                if(this.orgName.equals(peer.getOrgName())){
                    Properties eventHubProperties = FabricManager.getConfig().getEventHubProperties(peerName);
                    channel.addEventHub(client.newEventHub(peerName,eventHubLocation,eventHubProperties));
                }
                channel.addPeer(client.newPeer(peerName,peerLocation,peerProperties));
            }
        }

    }

}
