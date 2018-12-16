package com.inspur.fabric.sdk.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.codec.CharEncoding.UTF_8;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/19
 */
public abstract class AbstractInvokeChaincode {
    private static final Log log = LogFactory.getLog(AbstractInvokeChaincode.class);

    protected BaseChannel baseChannel;

    protected ChaincodeID chaincodeID;

    protected abstract void init(String chaincodeName, String chaincodePath, String version, String channelName, String userName);

    public String invoke(String[] args) throws Exception{
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        TransactionProposalRequest transactionProposalRequest = baseChannel.getClient().newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(args);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm.put("result", "success".getBytes(UTF_8)); /// This should be returned see
        transactionProposalRequest.setTransientMap(tm);
        Collection<Peer> endorsers = baseChannel.getChannel().getPeers();
        Collection<ProposalResponse> transactionPropResp = baseChannel.getChannel().sendTransactionProposal(transactionProposalRequest,
                endorsers);
        String resultAsString = null;
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                resultAsString = new String(response.getChaincodeActionResponsePayload(), "UTF-8");
            } else {
                failed.add(response);
            }
        }

        if (transactionPropResp==null){
            log.error("transactionPropResp is null!");
        }else{
            log.error("successful size is "+successful.size());
        }

//        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
//                .getProposalConsistencySets(transactionPropResp);


        baseChannel.getChannel().sendTransaction(successful).get(FabricManager.getConfig().getTransactionWaitTime(), TimeUnit.SECONDS);

        return resultAsString;
    }

    public String query(String[] args) throws Exception{
        TransactionProposalRequest transactionProposalRequest = baseChannel.getClient().newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(args);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm.put("result", "success".getBytes(UTF_8));

        transactionProposalRequest.setTransientMap(tm);

        Collection<ProposalResponse> transactionPropResp = baseChannel.getChannel().sendTransactionProposal(transactionProposalRequest,
                baseChannel.getChannel().getPeers());
        String resultAsString=null;

        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                resultAsString=new String(response.getChaincodeActionResponsePayload(), "UTF-8");
            }
        }
        if(resultAsString==null) {
            resultAsString = "";
        }
        return resultAsString;
    }

}
