package com.inspur.fabric.client;

import com.inspur.fabric.base.BaseOrg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.codec.CharEncoding.UTF_8;

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
