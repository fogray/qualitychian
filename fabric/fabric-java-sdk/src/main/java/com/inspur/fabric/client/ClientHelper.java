package com.inspur.fabric.client;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.base.BaseStore;
import com.inspur.fabric.base.BaseUser;
import com.inspur.fabric.base.ComUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/8
 */
public class ClientHelper {
    private static final Log log = LogFactory.getLog(ClientHelper.class);
    private static final String TEST_ADMIN_NAME = "admin";
    private static final String TESTUSER_1_NAME = "user1";
    private static final ClientConfig clientConfig = ClientConfig.getConfig();

    public BaseOrg getOrg(String peerOrg) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        if(log.isDebugEnabled()){
            log.debug("ClientHelper--getOrg--begin");
        }
        File storeFile = new File(System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties");
        BaseStore store = new BaseStore(storeFile);
        BaseOrg org = clientConfig.getOrg(peerOrg);
        org.setCAClient(HFCAClient.createNewInstance(org.getCALocation(),org.getCAProperties()));
        org.setAdmin(store.getMember(TEST_ADMIN_NAME,org.getName()));
        org.addUser(store.getMember(TESTUSER_1_NAME,org.getName()));
        return setPeerAdmin(store,org);
    }

    public BaseOrg setPeerAdmin(BaseStore store, BaseOrg org) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        final String orgName = org.getName();
        final String orgDomainName = org.getDomainName();

        String path = clientConfig.getChannelPath();
        BaseUser peerOrgAdmin = store.getMember(orgName+"Admin",orgName,org.getMSPID(),
                ComUtil.findFileSk(new File(path+"/crypto-config/peerOrganizations/"+orgDomainName+String.format("/users/Admin@%s/msp/keystore/", orgDomainName))),
                new File(path+"/crypto-config/peerOrganizations/"+orgDomainName+
                        String.format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", orgDomainName,
                                orgDomainName)));
        org.setPeerAdmin(peerOrgAdmin);
        return org;
    }

    public HFClient getHFClient() throws CryptoException, InvalidArgumentException {
        // Create instance of client.
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        return client;
    }

    public Channel getChannel(String peerOrg, String channelName) throws Exception {
        BaseOrg org = this.getOrg(peerOrg);
        HFClient client = this.getHFClient();

        client.setUserContext(org.getPeerAdmin());
        return getChannel(org, client, channelName);

    }

    public Channel getQueryChannel(String peerOrg, String channelName) throws Exception {
        BaseOrg org = this.getOrg(peerOrg);
        HFClient client = this.getHFClient();

        client.setUserContext(org.getPeerAdmin());
        return getQueryChannel(org, client, channelName);

    }

    private Channel getQueryChannel(BaseOrg org, HFClient client, String channelName) throws Exception {
        Channel channel = client.newChannel(channelName);
        channel.setTransactionWaitTime(clientConfig.getTransactionWaitTime());
        channel.setDeployWaitTime(clientConfig.getDeployWaitTime());

        Collection<BaseOrg> orgs = clientConfig.getOrgsCollection();
        for (String peerName : org.getPeerNames()) {
            String peerLocation = org.getPeerLocation(peerName);

            Properties peerProperties = clientConfig.getPeerProperties(peerName);
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            // Example of setting specific options on grpc's
            // ManagedChannelBuilder
            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            // channelPeers.add(client.newPeer(peerName, peerLocation,
            // peerProperties));
            channel.addPeer(client.newPeer(peerName, peerLocation, peerProperties));
        }


        Collection<Orderer> orderers = new LinkedList<>();

        for (String orderName : org.getOrdererNames()) {
            orderers.add(client.newOrderer(orderName, org.getOrdererLocation(orderName),
                    clientConfig.getOrdererProperties(orderName)));
        }

        // Just pick the first orderer in the list to create the chain.
        Orderer anOrderer = orderers.iterator().next();
        channel.addOrderer(anOrderer);

        for (String eventHubName : org.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(eventHubName, org.getEventHubLocation(eventHubName),
                    clientConfig.getEventHubProperties(eventHubName));
            channel.addEventHub(eventHub);
        }

        if (!channel.isInitialized()) {
            channel.initialize();
        }

        return channel;
    }

    private Channel getChannel(BaseOrg org, HFClient client, String channelName)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException,
            CryptoException, InvalidArgumentException, TransactionException {
        Channel channel = client.newChannel(channelName);
        channel.setTransactionWaitTime(clientConfig.getTransactionWaitTime());
        channel.setDeployWaitTime(clientConfig.getDeployWaitTime());

        Collection<BaseOrg> orgs = clientConfig.getOrgsCollection();
        for (BaseOrg porg : orgs){
            for (String peerName : porg.getPeerNames()) {
                String peerLocation = porg.getPeerLocation(peerName);

                Properties peerProperties = clientConfig.getPeerProperties(peerName);
                if (peerProperties == null) {
                    peerProperties = new Properties();
                }
                peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
                channel.addPeer(client.newPeer(peerName, peerLocation, peerProperties));
            }
        }


        Collection<Orderer> orderers = new LinkedList<>();

        for (String orderName : org.getOrdererNames()) {
            orderers.add(client.newOrderer(orderName, org.getOrdererLocation(orderName),
                    clientConfig.getOrdererProperties(orderName)));
        }

        // Just pick the first orderer in the list to create the chain.
        Orderer anOrderer = orderers.iterator().next();
        channel.addOrderer(anOrderer);

        for (String eventHubName : org.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(eventHubName, org.getEventHubLocation(eventHubName),
                    clientConfig.getEventHubProperties(eventHubName));
            channel.addEventHub(eventHub);
        }

        if (!channel.isInitialized()) {
            channel.initialize();
        }

        return channel;
    }

    public ChaincodeID getChaincodeID(String chainCodeName, String chainCodePath, String version) {
        return ChaincodeID.newBuilder().setName(chainCodeName).setVersion(version).setPath(chainCodePath)
                .build();
    }

}
