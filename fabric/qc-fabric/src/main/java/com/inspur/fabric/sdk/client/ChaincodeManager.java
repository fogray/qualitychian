package com.inspur.fabric.sdk.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/21
 */
public class ChaincodeManager {
    private static final Log log = LogFactory.getLog(ChaincodeManager.class);

    private static ChaincodeManager instance;

    public static ChaincodeManager getInstance(){
        if(instance==null){
            synchronized (ChaincodeManager.class){
                if(instance==null){
                    instance = new ChaincodeManager();
                }
            }
        }
        return instance;
    }

    private ChaincodeManager(){}

    /**
     * 企业备案信息上链
     * @param key 企业编码 例：91442000686409571K
     * @param value 企业相关信息 例：{"AUTH_DATE":"2017-12-18","AUTH_URL":"/res/qc/img/2018/01/04/2018010420312312866.jpg","CREDIT_CODE":"QI1712001716","CREDIT_RATING":"AAA","CREDIT_VALIDITY":"2018-12-17","MALL_LINK":"http://mall.gree.com/","ORGANIZE_ADDR":"广东省中山市民众镇民众大道北39号","ORGANIZE_CODE":"91442000686409571K","ORGANIZE_LOGO":"/res/qc/img/2018/01/15/2018011519034216139.png","ORGANIZE_NAME":"格力电器（中山）小家电制造有限公司","ORGANIZE_NAME_SIMPLE":"格力中山小家电","ORGANIZE_PHOTO_URL":"/res/qc/img/2018/01/04/2018010420283041547.jpg,/res/qc/img/2018/01/04/2018010420283907641.jpg,/res/qc/img/2018/01/04/2018010420284640369.jpg","RATIN_NAME":"中国检验检疫学会质量诚信建设委员会","SERVICE_TEL":"4008 365 315"}
     * @param user 当前用户
     * @return failed：失败 success：成功
     */
    public String uploadOrgInfo(String key, String value, String user){
        String res = "failed";
        try {
            res = ChaincodeImpl.getInstance("qc_org_cc","qcorgcc",user)
                    .invoke(new String[]{"setOrgInfo",key,value});
        } catch (Exception e) {
            log.error("uploadOrgInfo error:",e);
        }
        return res;
    }

    /**
     * 查询企业备案信息
     * @param key 企业编码 例：91442000686409571K
     * @return
     */
    public String getOrgInfo(String key){
        String res = "";
        try{
            res = ChaincodeImpl.getInstance("qc_org_cc","qcorgcc","peerOrg2Admin")
                    .query(new String[]{"getOrgInfo",key});
        }catch (Exception e){
            log.error("getOrgInfo error:",e);
        }
        return res;
    }

    /**
     * 查询企业备案信息上链的历史信息
     * @param key 企业编码 例：91442000686409571K
     * @return 例：[{"tx_id":"cd0e6ead8d5f53018aadb4e084b466446afe60d564335ea326a0fa95339e88cf","value":"YWFh","timestamp":{"seconds":1536733939,"nanos":381000000}}]
     */
    public String getOrgHistory(String key) {
        String res = "";
        try {
            res = ChaincodeImpl.getInstance("qc_org_cc","qcorgcc","peerOrg2Admin")
                    .query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("getOrgHistory error:",e);
        }
        return res;
    }

    /**
     * 删除企业备案信息
     * @param key 企业编码 例：91442000686409571K
     * @return
     */
    public String deleteOrgInfo(String key){
        String res = "failed";
        try{
            res = ChaincodeImpl.getInstance("qc_org_cc","qcorgcc","peerOrg1Admin")
                    .invoke(new String[]{"deleteOrgInfo",key});
        }catch (Exception e){
            log.error("deleteOrgInfo error:",e);
        }
        return res;
    }

    /**
     * 产品备案信息上链
     * @param key 产品编码 例：6937671717293
     * @param value 产品备案信息 例：{"ORGANIZE_CODE":"91442000686409571K","PRODUCT_BRAND":"大松（TOSOT）","PRODUCT_CODE":"6937671717293","PRODUCT_FEATURES":"IH电磁加热,整面盖可拆洗,复合传热内胆,三段智能保温,多种烹饪功能","PRODUCT_NAME":"IH智能电饭煲","PRODUCT_PATENT":"专利号201120358328.2一种具有显示板与主板密封连接结构的面盖组件,专利号CN201120379432.X电饭煲的面板组件及电饭煲,专利号CN201220363117.2阻尼铰链结构及包括该结构的盖和电饭煲","PRODUCT_PHOTO_URL":"/res/qc/img/2018/01/05/2018010508562375506.jpg,/res/qc/img/2018/01/05/2018010508563159779.jpg,/res/qc/img/2018/01/05/2018010508563761549.jpg","PRODUCT_RATING":"A+","PRODUCT_SPEC":"GDCF-4001Cf","PRODUCT_STANDARD":"GB4706.1,GB4706.19,GB4706.14,GB4806.1","STANDARD_DESC":"该产品执行的标准优于国家规定的标准。 1.优质团体标准的电磁兼容要求同时满足强制性标准要求和推荐性标准要求，这意味着更高的安全性能；\n2.煮饭性能杜绝了米浆流出的情况，使用起来更加省心； 3.保温功能参考日本电饭煲的保温测试，保温性能要求更高\n4.新增了提手负重，开合盖耐久性，高低温循环，内锅涂层耐腐蚀性的要求。提高了电饭煲的耐用性。 5.待机功率指标进行细分，更高的能效，更低的能耗；\n6.新增了拆解及可再生利用性、绿色包装的要求，更符合环保理念； 7.新增了多项有害物质指标要求。","STANDARD_PHOTO_URL":"/res/qc/img/2018/01/03/2018010313560627679.png,/res/qc/img/2018/01/03/2018010313561487311.jpg","TESTER":"卢瑞林","TEST_CODE":"DQ1702120","TEST_DATE":"2017-12-24","TEST_FLAG":"合格","TEST_ORG_NAME":"广东产品质量监督检验研究院","TEST_RESULT":"合格","TEST_RESULT_URL":"/res/sdct/img/null/2017122920080110312.jpg,/res/sdct/img/null/2017122920081929786.jpg,/res/sdct/img/null/2017122920083432705.jpg","TEST_STANDARD":"T/CAGDE002-2017《电饭煲》"}
     * @param user 当前用户
     * @return failed：失败 success：成功
     */
    public String uploadProductInfo(String key, String value, String user){
        String res = "failed";
        try{
            if(log.isDebugEnabled()){
                log.debug("uploadProductInfo--key-->"+key);
                log.debug("uploadProductInfo--value-->"+value);
            }
            res = ChaincodeImpl.getInstance("qc_product_cc","qcproductcc",user)
                    .invoke(new String[]{"setProductInfo",key,value,"trace1"});
        }catch (Exception e){
            log.error("uploadProductInfo error:",e);
        }
        return res;
    }

    /**
     * 查询产品备案信息
     * @param key 产品编码 例：6937671717293
     * @return
     */
    public String getProductInfo(String key){
        String res = "";
        try {
            res = ChaincodeImpl.getInstance("qc_product_cc","qcproductcc","peerOrg2Admin")
                    .query(new String[]{"getProductInfo",key});
        } catch (Exception e) {
            log.error("getProductInfo error:",e);
        }
        return res;
    }

    /**
     * 查询产品备案信息上链的历史信息
     * @param key 产品编码 例：6937671717293
     * @return 例：[{"tx_id":"cd0e6ead8d5f53018aadb4e084b466446afe60d564335ea326a0fa95339e88cf","value":"YWFh","timestamp":{"seconds":1536733939,"nanos":381000000}}]
     */
    public String getProductHistory(String key) {
        String res = "";
        try {
            res = ChaincodeImpl.getInstance("qc_product_cc","qcproductcc","peerOrg2Admin")
                    .query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("getProductHistory error:",e);
        }
        return res;
    }

    /**
     * 删除产品备案信息
     * @param key 产品编码 例：6937671717293
     * @return
     */
    public String deleteProductInfo(String key){
        String res = "failed";
        try{
            res = ChaincodeImpl.getInstance("qc_product_cc","qcproductcc","peerOrg1Admin")
                    .invoke(new String[]{"deleteProductInfo",key});
        }catch (Exception e){
            log.error("deleteProductInfo error:",e);
        }
        return res;
    }

    /**
     * 检验检测信息上链
     * @param key
     * @param value
     * @return failed：失败 success：成功
     */
    public String uploadCheckInfo(String key, String value, String user){
        String res = "failed";
        try {
            res = CheckChaincode.getInstance(user).setCheckInfo(key, value);
        } catch (Exception e) {
            log.error("uploadCheckInfo error:",e);
        }
        return res;
    }

    /**
     * 查询检验检测信息
     * @param key
     * @return
     */
    public String getCheckInfo(String key){
        String res = "";
        try {
            res = CheckChaincode.getInstance("peerOrg1Admin").getCheckInfo(key);
        } catch (Exception e) {
            log.error("getCheckInfo error:",e);
        }
        return res;
    }

    /**
     * 查询检验检测信息上链的历史信息
     * @param key
     * @return
     */
    public String getCheckHistory(String key){
        String res = "";
        try {
            res = CheckChaincode.getInstance("peerOrg1Admin").getHistory(key);
        } catch (Exception e) {
            log.error("getCheckHistory error:",e);
        }
        return res;
    }

    /**
     * 删除检验检测信息
     * @param key
     * @return
     */
    public String deleteCheckInfo(String key){
        String res = "";
        try {
            res = CheckChaincode.getInstance("peerOrg1Admin").deleteCheckInfo(key);
        } catch (Exception e) {
            log.error("deleteCheckInfo error:",e);
        }
        return res;
    }

    /**
     * 生成码码段信息上链
     * @param key 顺序码首位
     * @param value 生成码码段信息
     * @return failed：失败 success：成功
     */
    public String uploadCodes(String key, String value, String user){
        String res = "failed";
        try{
            log.error("uploadCodes--key-->"+key);
            log.error("uploadCodes--value-->"+value);
            res = ChaincodeImpl.getInstance("qc_generate_cc","qcgeneratecc",user)
                    .invoke(new String[]{"setCodes",key,value});
        }catch (Exception e){
            log.error("uploadCodes error:",e);
        }
        return res;
    }

    /**
     * 查询生成码码段信息
     * @param key 顺序码首位 例：0000000000
     * @return
     */
    public String getCodes(String key){
        String res = "";
        try {
            res = ChaincodeImpl.getInstance("qc_generate_cc","qcgeneratecc", "peerOrg1Admin")
                    .query(new String[]{"getCodes",key});
        } catch (Exception e) {
            log.error("getCodes error:",e);
        }
        return res;
    }

    /**
     * 查询生成码码段信息上链的历史信息
     * @param key 顺序码首位 例：0000000000
     * @return 例：[{"tx_id":"cd0e6ead8d5f53018aadb4e084b466446afe60d564335ea326a0fa95339e88cf","value":"YWFh","timestamp":{"seconds":1536733939,"nanos":381000000}}]
     */
    public String getCodesHistory(String key) {
        String res = "";
        try {
            res = ChaincodeImpl.getInstance("qc_generate_cc","qcgeneratecc", "peerOrg1Admin")
                    .query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("getCodesHistory error:",e);
        }
        return res;
    }

    /**
     * 删除生成码码段信息
     * @param key 顺序码首位 例：0000000000
     * @return
     */
    public String deleteCodes(String key){
        String res = "failed";
        try{
            res = res = ChaincodeImpl.getInstance("qc_generate_cc","qcgeneratecc", "peerOrg1Admin")
                    .invoke(new String[]{"deleteCodes",key});
        }catch (Exception e){
            log.error("deleteCodes error:",e);
        }
        return res;
    }
    public String deleteBigCode(String key){
        return deleteCodes(key);
    }

    /**
     * 存证信息上链
     * @param digest 原始文件摘要
     * @param data 鉴真信息（用于鉴真的产品信息）
     * @param user 当前用户
     * @return failed：失败 success：成功
     */
    public String uploadAppraisalInfo(String digest,String data,String user){
        String res = "failed";
        try {
            res = ChaincodeImpl.getInstance("qc_appraisal_cc","qcappraisalcc",user)
                    .invoke(new String[]{"setAppraisalInfo",digest,data});
        }catch (Exception e){
            log.error("uploadAppraisalInfo error:",e);
        }
        return res;
    }


    /**
     * 获取存证信息
     * @param digest 原始文件摘要
     * @return
     */
    public String getAppraisalInfo(String digest){
        String res = "";
        try {
            res = ChaincodeImpl.getInstance("qc_appraisal_cc","qcappraisalcc","peerOrg1Admin")
                    .query(new String[]{"getAppraisalInfo",digest});
        } catch (Exception e) {
            log.error("getAppraisalInfo error:",e);
        }
        return res;
    }

    /**
     * 根据用户获取存证列表
     * @param userId
     * @return
     */
    public String queryAppraisalByUser(String userId){
        String res = "";
        try{
            res = ChaincodeImpl.getInstance("qc_appraisal_cc","qcappraisalcc","peerOrg1Admin")
                    .query(new String[]{"queryAppraisalKeys","{\"selector\":{\"$and\": [{\"USER_ID\":\""+userId+"\"}]}}"});
        }catch (Exception e){
            log.error("queryAppraisalByUser error ",e);
        }
        return res;
    }

    /**
     * 产品追溯信息上链
     * @param key 顺序码首位 例：0000000000
     * @param value 产品追溯信息 例：{"BATCH_CODE":"21806840","END":"0000000000","ITEM_NAME":"IH智能电饭煲","ORGANIZE_CODE":"91442000686409571K","PROCESS_DATE":"2017-12-07","PROCESS_JSON":[{"MANAGER":"覃高胜","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"生产","PROCESS_WORK":"元器件装配"},{"MANAGER":"陆秀敏","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"2","PROCESS_TYPE":"生产","PROCESS_WORK":"发热盘装配"},{"MANAGER":"周英","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"3","PROCESS_TYPE":"生产","PROCESS_WORK":"主板组件插线"},{"MANAGER":"杨健新","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"4","PROCESS_TYPE":"生产","PROCESS_WORK":"连接插座"},{"MANAGER":"黎继业","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"5","PROCESS_TYPE":"生产","PROCESS_WORK":"装底座"},{"MANAGER":"梁利芳","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"6","PROCESS_TYPE":"生产","PROCESS_WORK":"面盖转接到支撑环组件"},{"MANAGER":"牛伟","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"7","PROCESS_TYPE":"生产","PROCESS_WORK":"内胆装配"},{"MANAGER":"张洪琼","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"8","PROCESS_TYPE":"生产","PROCESS_WORK":"整机质检"},{"MANAGER":"农吉利","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"9","PROCESS_TYPE":"生产","PROCESS_WORK":"成品包装"},{"MANAGER":"蔺忠","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"仓储","PROCESS_WORK":"入库"},{"MANAGER":"张廷春","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"2","PROCESS_TYPE":"仓储","PROCESS_WORK":"出库"},{"MANAGER":"运隆物流","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"物流","PROCESS_WORK":"配送"},{"MANAGER":"广州销售公司","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"销售","PROCESS_WORK":"经销"}],"PRODUCT_BRAND":"大松（TOSOT）","PRODUCT_CODE":"6937671717293","PRODUCT_SPEC":"GDCF-4001Cf","SERVICE_TEL":"4008 365 315","START":"0000000000","TRACE_CODE_END":"0000000000","TRACE_CODE_START":"0000000000"}
     * @param chainOrganMap 例：{CHAINCODE_NAME:'', CHAINCODE_PATH:'', TRACE_CODE_TYPE:''}
     * @param user 当前用户
     * @return failed：失败 success：成功
     */
    public String uploadTraceInfo(String key, String value, Map<String, Object> chainOrganMap, String user){

        String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
        String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
        String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");

        String res = "failed";
        if ("1".equals(traceCodeType)) {
            res = Trace1Chaincode.getInstance(chaincodeName, chaincodePath, user).setTraceInfo(key, value);

        } else if ("2".equals(traceCodeType)) {
            res = Trace2Chaincode.getInstance(chaincodeName, chaincodePath, user).setTraceInfo(key, value);

        } else if ("3".equals(traceCodeType)) {
            res = Trace3Chaincode.getInstance(chaincodeName, chaincodePath, user).setTraceInfo(key, value);
        }
        return res;
    }

    /**
     * 查询产品追溯信息
     * @param key 顺序码首位 例：0000000000
     * @param chainOrganMap 例：{CHAINCODE_NAME:'', CHAINCODE_PATH:'', TRACE_CODE_TYPE:'追溯类型'}
     * @return
     */
    public String getTraceInfo(String key, Map chainOrganMap){
        String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
        String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
        String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");

        String res = "";
        if ("1".equals(traceCodeType)) {
            res = Trace1Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceInfo(key);

        } else if ("2".equals(traceCodeType)) {
            res = Trace2Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceInfo(key);

        } else if ("3".equals(traceCodeType)) {
            res = Trace3Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceInfo(key);
        }
        return res;
    }

    /**
     * 根据产品编码和条码获取追溯信息
     * @param chainOrganMap 例：{CHAINCODE_NAME:'', CHAINCODE_PATH:''}
     * @param productCode 产品编码
     * @param barCode 条码
     * @return
     */
    public String getTraceInfoForType2(Map chainOrganMap, String productCode, String barCode){
        String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
        String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");
        String res = Trace2Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceInfoByCode(productCode, barCode);
        return res;
    }

    /**
     * 查询产品追溯信息上链的历史信息
     * @param key 顺序码首位 例：0000000000
     * @param chainOrganMap 例：{CHAINCODE_NAME:'', CHAINCODE_PATH:'', TRACE_CODE_TYPE:'追溯类型'}
     * @return
     */
    public String getTraceHistory(String key, Map chainOrganMap){
        String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
        String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
        String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");
        String res = "";
        if ("1".equals(traceCodeType)) {
            res = Trace1Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceHistory(key);

        } else if ("2".equals(traceCodeType)) {
            res = Trace2Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceHistory(key);

        } else if ("3".equals(traceCodeType)) {
            res = Trace3Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceHistory(key);
        }

        return res;
    }

    /**
     * 根据产品编码和条码查询产品追溯信息上链的历史信息
     * @param chainOrganMap 例：{CHAINCODE_NAME:'', CHAINCODE_PATH:''}
     * @param productCode 产品编码
     * @param barCode 条码
     * @return
     */
    public String getTraceHistoryForType2(Map chainOrganMap, String productCode, String barCode){
        String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
        String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");

        String res = Trace2Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").getTraceHistoryByCode(productCode, barCode);
        return res;
    }

    /**
     * 删除溯源信息
     * @param key
     * @param chainOrganMap
     * @return
     */
    public String deleteTraceInfo(String key, Map<String, Object> chainOrganMap){
        String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
        String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
        String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");

        String res = "";
        try{
            if ("1".equals(traceCodeType)) {
                res = Trace1Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").deleteTraceInfo(key);
            } else if ("2".equals(traceCodeType)) {
                res = Trace2Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").deleteTraceInfo(key);
            } else if ("3".equals(traceCodeType)) {
                res = Trace3Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").deleteTraceInfo(key);
            }
            if (log.isDebugEnabled()){
                log.debug("deleteTraceInfo--end--res-->"+res);
            }
        }catch (Exception e){
            log.error("deleteTraceInfo error:",e);
        }
        return res;
    }

    /**
     * 使用couchdb语法查询追溯信息列表
     * @param param couchdb语法查询语句
     * @param chainOrganMap 例：{CHAINCODE_NAME:'', CHAINCODE_PATH:'', TRACE_CODE_TYPE:'追溯类型'}
     * @return 返回key值的数组
     */
    public String queryTraceKeys(String param, Map<String, Object> chainOrganMap){
        String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
        String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
        String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");

        String res = "";
        try {
            if ("1".equals(traceCodeType)) {
                res = Trace1Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").query(new String[]{"queryTraceKeys", param});
            } else if ("2".equals(traceCodeType)) {
                res = Trace2Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").query(new String[]{"queryTraceKeys", param});
            } else if ("3".equals(traceCodeType)) {
                res = Trace3Chaincode.getInstance(chaincodeName, chaincodePath, "peerOrg1Admin").query(new String[]{"queryTraceKeys", param});
            }
        } catch (Exception e) {
            log.error("queryTraceKeys error:",e);
        }
        return res;
    }

    /**
     * 上传原料信息到所属行业链
     * @param key
     * @param value
     * @param channelName 通道名 例：trace1
     * @param user 当前用户
     * @return failed：失败 success：成功
     */
    public String uploadMaterialInfo(String key, String value, String channelName, String user){
        String res = "failed";
        try {
            res = MaterialChaincode.getInstance(channelName, user).setMaterialInfo(key, value);
        } catch (Exception e) {
            log.error("uploadMaterialInfo error:", e);
        }
        return res;
    }

    /**
     * 查询所属行业链的原料信息
     * @param channelName 通道名 例：trace1
     * @param param {"PRODUCT_CODE":"XXX","MATERIAL_BATCH":"XXX"}
     * @return
     */
    public String getMaterialInfo(String channelName,String param) {
        String res = "";
        try {
            res = MaterialChaincode.getInstance(channelName,"peerOrg1Admin").getMaterialInfo(param);
        } catch (Exception e) {
            log.error("getMaterialInfo error:",e);
        }
        return res;
    }

    /**
     * 查询所属行业链原料信息上链的历史信息
     * @param channelName 通道名 例：trace1
     * @param param {"PRODUCT_CODE":"","MATERIAL_BATCH":""}
     * @return
     */
    public String getMaterialHistory(String channelName,String param) {
        String res = "";
        try {
            res = MaterialChaincode.getInstance(channelName,"peerOrg1Admin").getHistory(param);
        } catch (Exception e) {
            log.error("getMaterialHistory error:",e);
        }
        return res;
    }

    /**
     * 删除药材链原料信息
     * @param channelName 通道名 例：trace1
     * @param key
     * @return
     */
    public String deleteMaterialInfo(String channelName,String key) {
        String res = "failed";
        try {
            res = MaterialChaincode.getInstance(channelName,"peerOrg1Admin").deleteMaterialInfo(key);
        } catch (Exception e) {
            log.error("deleteMaterialInfo error:",e);
        }
        return res;
    }

    /**
     * 上传流通信息到所属行业链
     * @param key
     * @param value
     * @param channelName 例：trace1
     * @return failed：失败 success：成功
     */
    public String uploadDeliveryInfo(String key, String value, String channelName, String user){
        String res = "failed";
        try{
            res = DeliveryChaincode.getInstance(channelName, user).setTraceInfo(key, value);
        }catch (Exception e){
            log.error("uploadDeliveryInfo error:",e);
        }
        return res;
    }

    /**
     * 获取所属行业链的流通信息
     * @param param {"PRODUCT_CODE":"XXX","TRACE_CODE":"XXX"}
     * @return
     */
    public String getDeliveryInfo(String channelName, String param){
        String res = "";
        try {
            res = DeliveryChaincode.getInstance(channelName, "peerOrg1Admin").getTraceInfo(param);
        } catch (Exception e) {
            log.error("getDeliveryInfo error:",e);
        }
        return res;
    }

    /**
     * 查询所属链流通信息上链的历史信息
     * @param param {"PRODUCT_CODE":"","TRACE_CODE":""}
     * @return
     */
    public String getDeliveryHistory(String channelName, String param){
        String res = "";
        try {
            res = DeliveryChaincode.getInstance(channelName, "peerOrg1Admin").getHistory(param);
        } catch (Exception e) {
            log.error("getDeliveryHistory error:",e);
        }
        return res;
    }

}
