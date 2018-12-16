package com.inspur.api;

import java.util.HashMap;
import java.util.Map;

import com.inspur.api.fabric.chaincode.pub.ChaincodeUtil;
import com.inspur.api.fabric.chaincode.pub.QCGChainCode;
import com.inspur.api.fabric.chaincode.pub.Trace1ChainCode;

public class Test {

	public static void main(String[] args) {
		try {
			String res = "";
			
			// 1、上传质量码码段
//			// 质量码开始号
//			String codeStart = "0000010000";
//			// 质量码段数据JSON
//			String codeInfo = "{\"END\":\"0000019999\",\"ORGANIZE_CODE\":\"O111111\",\"SEND_ORGANIZE_CODE\":\"O11111\",\"START\":\"0000010000\",\"SUM\":10000,\"VERSION\":\"1\"}";
//			// 质量码段写链, 返回success说明写链成功
//			res = ChaincodeUtil.getInstance().uploadCodes(codeStart, codeInfo);
			
			// 2、企业备案上链
//			// 企业信用代码
//			String orgCode = "O111111";
//			// 企业备案数据
//			String orgInfo = "{\"ORGANIZE_CODE\":\"O111111\",\"ORGANIZE_NAME\":\"测试企业\"}";
//			// 企业备案数据写链,返回success说明写链成功
//			res = ChaincodeUtil.getInstance().uploadOrgInfo(orgCode, orgInfo);
//			System.out.println(res);
//			// 查询企业备案信息
//			res = ChaincodeUtil.getInstance().getOrgInfo(orgCode);
//			System.out.println(res);
    		
    		// 3、产品备案
//			// 产品条码
//			String productCode = "P00001";
//			// 产品备案数据
//			String productInfo = "{\"PRODUCT_CODE\": \"P0001\", \"ORGANIZE_CODE\":\"O111111\"}";
//			// 产品备案数据写链，返回success说明写链成功
//			res = ChaincodeUtil.getInstance().uploadProductInfo(productCode, productInfo);
//			System.out.println(res);
//			// 查询产品备案信息
//			res = ChaincodeUtil.getInstance().getProductInfo(productCode);
//			System.out.println(res);
    		
    		// 4、生产溯源上传
//			// 溯源质量码
			String traceCode = "0000010000";
//			// 生产溯源数据
			String traceInfo = "{\"START\":\"0000010000\", \"END\":\"0000019999\", \"ORGANIZE_CODE\":\"O11111\", \"PRODUCT_CODE\":\"P0001\"}";
			// 生产溯源数据写链，返回success说明写链成功
			res = Trace1ChainCode.getInstance("peerOrg3", "trace4", "qc_medicine_cc", "qcmedicinecc", "1.2")
			    						.setTraceInfo(traceCode, traceInfo);
			System.out.println(res);
			// 查询生产溯源信息
			res = Trace1ChainCode.getInstance("peerOrg3", "trace4", "qc_medicine_cc", "qcmedicinecc", "1.2")
								.getTraceInfo(traceCode);
			System.out.println(res);

//			Map<String, Object> chainOrganMap = new HashMap<String, Object>();
//			chainOrganMap.put("TRACE_CODE_TYPE", "1");
//			chainOrganMap.put("CHAINCODE_NAME", "qc_medicine_cc");
//			chainOrganMap.put("CHAINCODE_PATH", "qcmedicinecc");
//			chainOrganMap.put("CHAINCODE_VERSION", "1.2");
//			chainOrganMap.put("CHAIN_ORGAN_NAME", "peerOrg3");
//			String res1 = ChaincodeUtil.getInstance().uploadTraceInfo(traceCode, traceInfo
//					, "trace4", chainOrganMap);
//			System.out.println(res1);
//			// 查询生产溯源信息
//			res = ChaincodeUtil.getInstance().getTraceInfo(traceCode, "trace4", chainOrganMap);
//			System.out.println(res);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
