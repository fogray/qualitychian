package com.inspur.fabric.client;

import static org.apache.commons.codec.CharEncoding.UTF_8;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/23
 */
public abstract class AbstractInvokeChainCode {
    private static final Log log = LogFactory.getLog(AbstractInvokeChainCode.class);

    protected static final ClientHelper clientHelper = new ClientHelper();
    protected static final ClientConfig config = ClientConfig.getConfig();
    protected HFClient client;
    protected Channel channel;
    protected Channel queryChannel;
    protected ChaincodeID chaincodeID;
    protected abstract void init(String peerOrg);

    public String invoke(String[] args) throws Exception{
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(args);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm.put("result", "success".getBytes(UTF_8)); /// This should be returned see
        transactionProposalRequest.setTransientMap(tm);
        Collection<Peer> endorsers = channel.getPeers();
        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest,
                endorsers);
        for (ProposalResponse response : transactionPropResp) {
        	
        	System.out.println("----"+response.getPeer().getUrl()+": "+response.getMessage());
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
                .getProposalConsistencySets(transactionPropResp);
        if (proposalConsistencySets.size() != 1) {
            log.error("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size());
        }
        if (failed.size() > 0) {
            log.error("endorse result failed size:"+failed.size());
        } else {
            log.debug("Successfully received transaction proposal responses.");
        }

        ProposalResponse resp = transactionPropResp.iterator().next();
        byte[] x = resp.getChaincodeActionResponsePayload();
        String resultAsString = null;
        if (x != null) {
            resultAsString = new String(x, "UTF-8");
        }
//        channel.sendTransaction(successful).thenApply(transactionEvent -> {
//            if (transactionEvent.isValid()) {
//                if(log.isDebugEnabled()){
//                    log.debug("Successfully send transaction proposal to orderer. Transaction ID: "
//                            + transactionEvent.getTransactionID());
//                }
//            } else {
//                if(log.isDebugEnabled()){
//                    log.debug("Failed to send transaction proposal to orderer");
//                }
//            }
//            return transactionEvent.getTransactionID();
//        }).get(config.getTransactionWaitTime(), TimeUnit.SECONDS);
        channel.sendTransaction(successful).thenApplyAsync(transactionEvent -> {
            if (transactionEvent.isValid()) {
                if(log.isDebugEnabled()){
                    log.debug("Successfully send transaction proposal to orderer. Transaction ID: "
                            + transactionEvent.getTransactionID());
                }
            } else {
                if(log.isDebugEnabled()){
                    log.debug("Failed to send transaction proposal to orderer");
                }
            }
            System.out.println("*******"+ transactionEvent.getEventHub().isConnected());
            return transactionEvent.getTransactionID();
        }).get(config.getTransactionWaitTime(), TimeUnit.SECONDS);
        return resultAsString;
    }

    // 安装链码
    public void install(List<String> peers) throws Exception{
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        // 链码tar压缩包路径
//        installProposalRequest.setChaincodeInputStream(AbstractInvokeChainCode.class.getResourceAsStream(ClientConfig.CHAINCODE_SRC_TAR_PATH+chaincodeID.getName()+".tar"));
        // 链码源码路径
        installProposalRequest.setChaincodeSourceLocation(new File(this.getClass().getResource("/").getPath() + ClientConfig.CHAINCODE_SRC_PATH));
        installProposalRequest.setChaincodeVersion(chaincodeID.getVersion());
        installProposalRequest.setChaincodeName(chaincodeID.getName());
        installProposalRequest.setChaincodePath(chaincodeID.getPath());
        installProposalRequest.setUserContext(clientHelper.getOrg("peerOrg1").getPeerAdmin());
        installProposalRequest.setProposalWaitTime(200000l);
        
        Collection<ProposalResponse> responses;
        Collection<Peer> peersFromOrg = channel.getPeers();
        Iterator<Peer> it = peersFromOrg.iterator();
        while(it.hasNext()) {
        	Peer tmp = it.next();
        	if (!peers.contains(tmp.getName())) {
        		it.remove();
        	}
        }
        responses = client.sendInstallProposal(installProposalRequest, peersFromOrg);
        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
            } else {
                failed.add(response);
            }
        }
        
        SDKUtils.getProposalConsistencySets(responses);
        
        if (failed.size() > 0) {
            ProposalResponse first = failed.iterator().next();
            System.err.println("failed count="+failed.size()+", 0: "+first.getMessage());
        }
        
    }

    // 实例化链码
    public String instantiation(String[] initArgs) throws Exception{
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        
        InstantiateProposalRequest transactionProposalRequest = client.newInstantiationProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn("init");
        transactionProposalRequest.setArgs(initArgs);
        transactionProposalRequest.setChaincodeName(chaincodeID.getName());
        transactionProposalRequest.setChaincodePath(chaincodeID.getPath());
        transactionProposalRequest.setChaincodeVersion(chaincodeID.getVersion());
        
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm.put("result", "success".getBytes(UTF_8)); /// This should be returned see
        transactionProposalRequest.setTransientMap(tm);
        
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromStream(this.getClass().getResourceAsStream(ClientConfig.CHAINCODE_ENDORSEMENTPOLICY_FILE));
//        chaincodeEndorsementPolicy.fromYamlFile(new File("/channels/chaincodeendorsementpolicy.yaml"));
        transactionProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        
        Collection<Peer> endorsers = channel.getPeers();
        Collection<ProposalResponse> transactionPropResp = channel.sendInstantiationProposal(transactionProposalRequest,
                endorsers);
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
                .getProposalConsistencySets(transactionPropResp);
        if (proposalConsistencySets.size() != 1) {
            log.error("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size());
        }
        if (failed.size() > 0) {
            log.error("endorse result failed size:"+failed.size());
        } else {
            log.debug("Successfully received transaction proposal responses.");
        }

        ProposalResponse resp = transactionPropResp.iterator().next();
        byte[] x = resp.getChaincodeActionResponsePayload();
        String resultAsString = null;
        if (x != null) {
            resultAsString = new String(x, "UTF-8");
        }
        channel.sendTransaction(successful).thenApply(transactionEvent -> {
            if (transactionEvent.isValid()) {
                if(log.isDebugEnabled()){
                    log.debug("Successfully send transaction proposal to orderer. Transaction ID: "
                            + transactionEvent.getTransactionID());
                }
            } else {
                if(log.isDebugEnabled()){
                    log.debug("Failed to send transaction proposal to orderer");
                }
            }
            return transactionEvent.getTransactionID();
        }).get(config.getTransactionWaitTime(), TimeUnit.SECONDS);
        return resultAsString;
    }
    
    public String query(String[] args) throws Exception{
        if(log.isDebugEnabled()){
            log.debug("AbstractInvokeChainCode--query--begin");
        }
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(args);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm.put("result", "success".getBytes(UTF_8));
        /// chaincode.
        transactionProposalRequest.setTransientMap(tm);
        Collection<ProposalResponse> transactionPropResp = queryChannel.sendTransactionProposal(transactionProposalRequest,
                queryChannel.getPeers());
        String resultAsString=null;
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                resultAsString=new String(response.getChaincodeActionResponsePayload(), "UTF-8");
            } else {
                failed.add(response);
            }
            if(log.isDebugEnabled()){
                log.debug("AbstractInvokeChainCode--query--resultAsString-->"+resultAsString);
            }
        }
        if(resultAsString==null) {
            resultAsString = "";
        }
        return resultAsString;
    }

}
