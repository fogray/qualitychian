package com.inspur.fabric.client;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.Common.*;
import org.hyperledger.fabric.protos.ledger.rwset.Rwset.NsReadWriteSet;
import org.hyperledger.fabric.protos.ledger.rwset.Rwset.TxReadWriteSet;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset.*;
import org.hyperledger.fabric.protos.msp.Identities.SerializedIdentity;
import org.hyperledger.fabric.protos.peer.ChaincodeEventOuterClass.ChaincodeEvent;
import org.hyperledger.fabric.protos.peer.FabricProposal.ChaincodeAction;
import org.hyperledger.fabric.protos.peer.FabricProposal.ChaincodeProposalPayload;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse.Endorsement;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse.ProposalResponsePayload;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse.Response;
import org.hyperledger.fabric.protos.peer.FabricTransaction.*;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.helper.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockDecoder {
	
	public static Map<String, Object> decodeTransaction(TransactionInfo txInfo) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ProcessedTransaction processedTrans = txInfo.getProcessedTransaction();
		int validationCode = processedTrans.getValidationCode();
		result.put("validationCode", validationCode);
		result.put("transactionEnvelope", decodeBlockDataEnvelope(processedTrans.getTransactionEnvelope()));
		
		return result;
	}
	
	public static Map<String, Object> decodeBlockDataEnvelope(Envelope envelop) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ByteString txSignature = envelop.getSignature();
		result.put("signature",  txSignature.toByteArray());
		
		Map<String, Object> payloadMap = new HashMap<String, Object>();
		Payload payload = Payload.parseFrom(envelop.getPayload());
		Header payloadHeader  = payload.getHeader();
		Map<String, Object> header = decodeHeader(payloadHeader);
		payloadMap.put("header", header);
		int headerType = (Integer)((Map<String,Object>)header.get("channelHeader")).get("type");
		if (headerType == 1) {//CONFIG
//			decodeConfigEnvelope(payload.getData());
		} if (headerType == 3) {//ENDORSER_TRANSACTION
			payloadMap.put("data", decodeEndorserTransaction(payload.getData()));
			
		} else {
			throw new InvalidProtocolBufferException("Only able to decode ENDORSER_TRANSACTION and CONFIG type blocks");
		}
		result.put("payload", payloadMap);
		return result;
	}
	
	public static Map<String, Object> decodeEndorserTransaction(ByteString data) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		Transaction tx = Transaction.parseFrom(data);
		List<TransactionAction> txActions = tx.getActionsList();
		List<Map<String, Object>> actions = new ArrayList<Map<String, Object>>();
		for (TransactionAction txacAction : txActions) {
			Map<String, Object> action = new HashMap<String, Object>();
			action.put("header", decodeSignatureHeader(txacAction.getHeader()));
			action.put("payload", decodeChaincodeActionPayload(txacAction.getPayload()));
			actions.add(action);
		}
		result.put("actions", actions);
		return result;
	}
	
	public static Map<String, Object> decodeChaincodeActionPayload(ByteString payload) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ChaincodeActionPayload actionPayload = ChaincodeActionPayload.parseFrom(payload);
		result.put("chaincodeProposalPayload", decodeChaincodeProposalPayload(actionPayload.getChaincodeProposalPayload()));
		result.put("action", decodeChaincodeEndorsedAction(actionPayload.getAction()));
		
		return result;
	}
	
	public static Map<String, Object> decodeChaincodeProposalPayload(ByteString payload) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ChaincodeProposalPayload propsalPayload = ChaincodeProposalPayload.parseFrom(payload);
		result.put("input", propsalPayload.getInput().toByteArray());
		return result;
	}
	
	public static Map<String, Object> decodeChaincodeEndorsedAction(ChaincodeEndorsedAction endorseAction) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("proposalResponsePayload", decodeProposalResponsePayload(endorseAction.getProposalResponsePayload()));
		List<Map<String, Object>> endorsements = new ArrayList<Map<String, Object>>();
		List<Endorsement> endorsementList = endorseAction.getEndorsementsList();
		for (Endorsement endor : endorsementList) {
			endorsements.add(decodeEndorsement(endor));
		}
		result.put("endorsements", endorsements);
		return result;
	}

	public static Map<String, Object> decodeEndorsement(Endorsement endor) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("endorser", decodeIdentity(endor.getEndorser()));
		result.put("signature", endor.getSignature());
		return result;
	}

	public static Map<String, Object> decodeProposalResponsePayload(ByteString payload) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ProposalResponsePayload responsePayload = ProposalResponsePayload.parseFrom(payload);
		result.put("propsalHash", Utils.toHexString(responsePayload.getProposalHash()));
		result.put("extension", decodeChaincodeAction(responsePayload.getExtension()));
		return result;
	}

	public static Map<String, Object> decodeChaincodeAction(ByteString actionBytes) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ChaincodeAction txacAcChaincodeAc = ChaincodeAction.parseFrom(actionBytes);
		result.put("results", decodeReadWriteSets(txacAcChaincodeAc.getResults()));
		result.put("events", decodeChaincodeEvents(txacAcChaincodeAc.getEvents()));
		result.put("response", decodeResponse(txacAcChaincodeAc.getResponse()));
		return result;
	}

	public static Map<String, Object> decodeResponse(Response response) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", response.getStatus());
		result.put("message", response.getMessage());
		result.put("payload", response.getPayload());
		return result;
	}

	public static Map<String, Object> decodeChaincodeEvents(ByteString events) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ChaincodeEvent chaincodeEvt = ChaincodeEvent.parseFrom(events);
		result.put("chaincodeId", chaincodeEvt.getChaincodeId());
		result.put("txId", chaincodeEvt.getTxId());
		result.put("eventName", chaincodeEvt.getEventName());
		result.put("payload", chaincodeEvt.getPayload());
		return result;
	}

	
	public static Map<String, Object> decodeReadWriteSets(ByteString rwsets) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		TxReadWriteSet txRwSets = TxReadWriteSet.parseFrom(rwsets);
		result.put("dataModel", txRwSets.getDataModel());
		
		if (txRwSets.getDataModel().KV == TxReadWriteSet.DataModel.KV) {
			List<NsReadWriteSet> nsRwList = txRwSets.getNsRwsetList();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> tmp = null;
			for (NsReadWriteSet tmpNsRwset : nsRwList) {
				tmp = new HashMap<String, Object>();
				tmp.put("namespapce", tmpNsRwset.getNamespace());
				tmp.put("rwset", decodeKVRWSet(tmpNsRwset.getRwset()));
				list.add(tmp);
			}
			result.put("nsRWSet", list);
		} else {
			// not able to decode this type of rw set, return the array of byte[]
			result.put("nsRWSet", txRwSets.getNsRwsetList());
		}
		
		return result;
	}

	/**
	 * 解码：Header
	 * @param header
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static Map<String, Object> decodeKVRWSet(ByteString kvRWSet) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();

		KVRWSet kvRwSet = KVRWSet.parseFrom(kvRWSet);
		List<KVRead> reads = kvRwSet.getReadsList();
		List<Map<String, Object>> kvReads = new ArrayList<Map<String, Object>>();
		for (KVRead read : reads) {
			kvReads.add(decodeKVRead(read));
		}
		result.put("reads", kvReads);
		
		List<RangeQueryInfo> querinfoList = kvRwSet.getRangeQueriesInfoList();
		List<Map<String, Object>> queriesInfo = new ArrayList<Map<String, Object>>();
		for (RangeQueryInfo querinfo : querinfoList) {
			queriesInfo.add(decodeRangeQueryInfo(querinfo));
		}
		result.put("rangeQueriesInfo", queriesInfo);
		
		List<KVWrite> writes = kvRwSet.getWritesList();
		List<Map<String, Object>> kvWrites = new ArrayList<Map<String, Object>>();
		for (KVWrite write : writes) {
			kvWrites.add(decodeKVWrite(write));
		}
		result.put("writes", kvWrites);
		
		return result;
	}

	/**
	 * 解码：KVRead
	 * @param read
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static Map<String, Object> decodeKVRead(KVRead read) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		String key = read.getKey();
		Version version  = read.getVersion();
		if (version != null) {
			Map<String, Object> tmpVer = new HashMap<String, Object>();
			tmpVer.put("blockNum", version.getBlockNum());
			tmpVer.put("txNum", version.getTxNum());
			result.put("version", tmpVer);
		} else {
			result.put("version", null);
		}
		result.put("key", key);
		return result;
	}

	/**
	 * 解码：RangeQueryInfo
	 * @param rangeQueryInfo
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static Map<String, Object> decodeRangeQueryInfo(RangeQueryInfo rangeQueryInfo) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		String startKey = rangeQueryInfo.getStartKey();
		String endKey = rangeQueryInfo.getEndKey();
		boolean itrExhausted = rangeQueryInfo.getItrExhausted();
		result.put("startKey", startKey);
		result.put("endKey", endKey);
		result.put("itrExhausted", itrExhausted);
		
		QueryReads queryReads = rangeQueryInfo.getRawReads();
		Map<String, Object> readsInfo = new HashMap<String, Object>();
		if (queryReads != null) {
			List<Map<String, Object>> reads_info = new ArrayList<Map<String, Object>>();
			List<KVRead> kvReadList = queryReads.getKvReadsList();
			for (KVRead kvRead : kvReadList) {
				reads_info.add(decodeKVRead(kvRead));
			}
			readsInfo.put("kvReads", reads_info);
		}
		result.put("readsInfo", readsInfo);
		
		QueryReadsMerkleSummary queryReadsMerkleSummary = rangeQueryInfo.getReadsMerkleHashes();
		if (queryReadsMerkleSummary != null) {
			Map<String, Object> readsMerkle = new HashMap<String, Object>();
			readsMerkle.put("maxDegree", queryReadsMerkleSummary.getMaxDegree());
			readsMerkle.put("maxLevel", queryReadsMerkleSummary.getMaxLevel());
			readsMerkle.put("maxLevelHashes", queryReadsMerkleSummary.getMaxLevelHashesList());
			result.put("readsMerkleHashes", readsMerkle);
		}
		
		return result;
	}

	/**
	 * 解码：KVWrite
	 * @param kvWrite
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static Map<String, Object> decodeKVWrite(KVWrite kvWrite) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("key", kvWrite.getKey());
		result.put("isDelete", kvWrite.getIsDelete());
		result.put("value", kvWrite.getValue().toStringUtf8());
		return result;
	}
	
	
	/**
	 * 解码：Header
	 * @param header
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static Map<String, Object> decodeHeader(Header header) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("channelHeader", decodeChannelHeader(header.getChannelHeader()));
		result.put("signatureHeader", decodeSignatureHeader(header.getSignatureHeader()));
		return result;
	}
	
	/**
	 * 解码：channelHeader
	 * @param channelHeader
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static Map<String, Object> decodeChannelHeader(ByteString channelHeader) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		ChannelHeader ch = ChannelHeader.parseFrom(channelHeader);
		result.put("type", ch.getType());
		result.put("version", ch.getVersion());
		result.put("channel_id", ch.getChannelId());
		result.put("timestamp", ch.getTimestamp());
		result.put("tx_id", ch.getTxId());
		result.put("epoch", ch.getEpoch());
		// TODO need to decode
		result.put("extension", ch.getExtension().toByteArray());
		return result;
	}

	/**
	 * 解码：channelHeader
	 * @param channelHeader
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static Map<String, Object> decodeSignatureHeader(ByteString signatureHeader) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		SignatureHeader ch = SignatureHeader.parseFrom(signatureHeader);
		result = decodeIdentity(ch.getCreator());
		result.put("nonce", ch.getNonce().toByteArray());
		return result;
	}
	
	public static Map<String, Object> decodeIdentity(ByteString identity) throws InvalidProtocolBufferException {
		Map<String, Object> result = new HashMap<String, Object>();
		SerializedIdentity res = SerializedIdentity.parseFrom(identity);
		res.getMspid();
		res.getIdBytes().toStringUtf8();
		result.put("mspId", res.getMspid());
		result.put("idBytes", res.getIdBytes().toStringUtf8());
		return result;
	}
}
