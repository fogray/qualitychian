package com.inspur.api.fabric.chaincode.pub;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.security.CryptoPrimitives;
import org.json.JSONObject;

import com.inspur.fabric.client.AppraisalChainCode;
import com.inspur.fabric.client.ClientConfig;
import com.inspur.fabric.client.ClientHelper;
import com.inspur.fabric.client.SetupUsers;

import sun.misc.BASE64Decoder;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/8
 */
public class ChaincodeUtil {

    private static ChaincodeUtil util = null;
    private static final Log log = LogFactory.getLog(ChaincodeUtil.class);
    protected static final ClientHelper clientHelper = new ClientHelper();
    protected static final ClientConfig config = ClientConfig.getConfig();
    public static ChaincodeUtil getInstance(){
        if (null == util){
            try {
                SetupUsers.invoke();
                util = new ChaincodeUtil();
            } catch (Exception e) {
                log.error("ChaincodeUtil--err-->",e);
                util = null;
            }
        }
        return util;
    }

    public ChaincodeUtil() throws Exception {
    }

    /**
     * 上传企业备案信息
     * @param key 企业编码 例：91442000686409571K
     * @param value 企业相关信息 例：{"AUTH_DATE":"2017-12-18","AUTH_URL":"/res/qc/img/2018/01/04/2018010420312312866.jpg","CREDIT_CODE":"QI1712001716","CREDIT_RATING":"AAA","CREDIT_VALIDITY":"2018-12-17","MALL_LINK":"http://mall.gree.com/","ORGANIZE_ADDR":"广东省中山市民众镇民众大道北39号","ORGANIZE_CODE":"91442000686409571K","ORGANIZE_LOGO":"/res/qc/img/2018/01/15/2018011519034216139.png","ORGANIZE_NAME":"格力电器（中山）小家电制造有限公司","ORGANIZE_NAME_SIMPLE":"格力中山小家电","ORGANIZE_PHOTO_URL":"/res/qc/img/2018/01/04/2018010420283041547.jpg,/res/qc/img/2018/01/04/2018010420283907641.jpg,/res/qc/img/2018/01/04/2018010420284640369.jpg","RATIN_NAME":"中国检验检疫学会质量诚信建设委员会","SERVICE_TEL":"4008 365 315"}
     * @return
     */
    public String uploadOrgInfo(String key, String value){
        String res = "failed";
        try {
            if(log.isDebugEnabled()){
                log.debug("uploadOrgInfo--key-->"+key);
                log.debug("uploadOrgInfo--value-->"+value);
            }
            res = OrgChainCode.getInstance("peerOrg2").invoke(new String[]{"setOrgInfo",key,value});
        } catch (Exception e) {
            log.error("uploadOrgInfo--err-->",e);
            e.printStackTrace();
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
        try {
            res = OrgChainCode.getInstance("peerOrg2").query(new String[]{"getOrgInfo",key});
        } catch (Exception e) {
            log.error("getOrgInfo--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 查询企业备案历史信息
     * @param key 企业编码 例：91442000686409571K
     * @return
     */
    public String getOrgHistory(String key) {
        String res = "";
        try {
            res = OrgChainCode.getInstance("peerOrg2").query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("getOrgHistory--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 删除企业备案信息
     * @param key 企业编码 例：91442000686409571K
     * @return
     */
    public String deleteOrgInfo(String key){
        String res = "";
        try{
            res = OrgChainCode.getInstance("peerOrg1").invoke(new String[]{"deleteOrgInfo",key});
        }catch (Exception e){
            log.error("deleteOrgInfo--error-->",e);
        }
        return res;
    }

    /**
     * 上传产品备案信息
     * @param key 产品编码 例：6937671717293
     * @param value 产品备案信息 例：{"ORGANIZE_CODE":"91442000686409571K","PRODUCT_BRAND":"大松（TOSOT）","PRODUCT_CODE":"6937671717293","PRODUCT_FEATURES":"IH电磁加热,整面盖可拆洗,复合传热内胆,三段智能保温,多种烹饪功能","PRODUCT_NAME":"IH智能电饭煲","PRODUCT_PATENT":"专利号201120358328.2一种具有显示板与主板密封连接结构的面盖组件,专利号CN201120379432.X电饭煲的面板组件及电饭煲,专利号CN201220363117.2阻尼铰链结构及包括该结构的盖和电饭煲","PRODUCT_PHOTO_URL":"/res/qc/img/2018/01/05/2018010508562375506.jpg,/res/qc/img/2018/01/05/2018010508563159779.jpg,/res/qc/img/2018/01/05/2018010508563761549.jpg","PRODUCT_RATING":"A+","PRODUCT_SPEC":"GDCF-4001Cf","PRODUCT_STANDARD":"GB4706.1,GB4706.19,GB4706.14,GB4806.1","STANDARD_DESC":"该产品执行的标准优于国家规定的标准。 1.优质团体标准的电磁兼容要求同时满足强制性标准要求和推荐性标准要求，这意味着更高的安全性能；\n2.煮饭性能杜绝了米浆流出的情况，使用起来更加省心； 3.保温功能参考日本电饭煲的保温测试，保温性能要求更高\n4.新增了提手负重，开合盖耐久性，高低温循环，内锅涂层耐腐蚀性的要求。提高了电饭煲的耐用性。 5.待机功率指标进行细分，更高的能效，更低的能耗；\n6.新增了拆解及可再生利用性、绿色包装的要求，更符合环保理念； 7.新增了多项有害物质指标要求。","STANDARD_PHOTO_URL":"/res/qc/img/2018/01/03/2018010313560627679.png,/res/qc/img/2018/01/03/2018010313561487311.jpg","TESTER":"卢瑞林","TEST_CODE":"DQ1702120","TEST_DATE":"2017-12-24","TEST_FLAG":"合格","TEST_ORG_NAME":"广东产品质量监督检验研究院","TEST_RESULT":"合格","TEST_RESULT_URL":"/res/sdct/img/null/2017122920080110312.jpg,/res/sdct/img/null/2017122920081929786.jpg,/res/sdct/img/null/2017122920083432705.jpg","TEST_STANDARD":"T/CAGDE002-2017《电饭煲》"}
     * @return
     */
    public String uploadProductInfo(String key, String value){
        String res = "failed";
        try{
            if(log.isDebugEnabled()){
                log.debug("uploadProductInfo--key-->"+key);
                log.debug("uploadProductInfo--value-->"+value);
            }
            res = ProductChainCode.getInstance("peerOrg2").invoke(new String[]{"setProductInfo",key,value,"trace1"});
        }catch (Exception e){
            log.error("uploadProductInfo--err-->",e);
            e.printStackTrace();
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
            res = ProductChainCode.getInstance("peerOrg2").query(new String[]{"getProductInfo",key});
        } catch (Exception e) {
            log.error("getProductInfo--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 查询产品备案历史信息
     * @param key 产品编码 例：6937671717293
     * @return
     */
    public String getProductHistory(String key) {
        String res = "";
        try {
            res = ProductChainCode.getInstance("peerOrg2").query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("getProductHistory--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 删除产品备案信息
     * @param key 产品编码 例：6937671717293
     * @return
     */
    public String deleteProductInfo(String key){
        String res = "";
        try{
            res = ProductChainCode.getInstance("peerOrg1").invoke(new String[]{"deleteProductInfo",key});
        }catch (Exception e){
            log.error("deleteProductInfo--error-->",e);
        }
        return res;
    }

    /**
     * 上传检测检验信息
     * @param key
     * @param value
     * @return
     */
    public String uploadCheckInfo(String key, String value){
        String res = "failed";
        try {
            res = CheckChainCode.getInstance("peerOrg1").setCheckInfo(key, value);
        } catch (Exception e) {
            log.error("ChaincodeUtil--uploadCheckInfo--err-->",e);
        }
        return res;
    }

    /**
     * 查询检验检测信息
     * @param json {"PRODUCT_CODE":"XXX","BATCH_CODE":"XXX"}
     * @return
     */
    public String getCheckInfo(String json){
        String res = "";
        try {
            res = CheckChainCode.getInstance("peerOrg1").getCheckInfo(json);
        } catch (Exception e) {
            log.error("ChaincodeUtil--getCheckInfo--err-->",e);
        }
        return res;
    }

    /**
     * 查询检验检测历史信息
     * @param json {"PRODUCT_CODE":"","BATCH_CODE":""}
     * @return
     */
    public String getCheckHistory(String json){
        String res = "";
        try {
            res = CheckChainCode.getInstance("peerOrg1").getHistory(json);
        } catch (Exception e) {
            log.error("getCheckHistory--err-->",e);
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
            res = CheckChainCode.getInstance("peerOrg1").deleteCheckInfo(key);
        } catch (Exception e) {
            log.error("ChaincodeUtil--deleteCheckInfo--err-->",e);
        }
        return res;
    }

    /**
     * 上传下发码码段信息
     * @param key 顺序码首位 例：0000000000
     * @param value 下发码码段信息 例：{"END":"0000000000","ORGANIZE_CODE":"91442000686409571K","SEND_ORGANIZE_CODE":"51100000088551127E","START":"0000000000","SUM":1,"VERSION":"1"}
     * @return
     */
    public String uploadCodes(String key, String value){
        String res = "failed";
        try{
            if(log.isDebugEnabled()){
                log.debug("uploadCodes--key-->"+key);
                log.debug("uploadCodes--value-->"+value);
            }
            res = QCGChainCode.getInstance("peerOrg1").invoke(new String[]{"setCodes",key,value});
        }catch (Exception e){
            log.error("uploadCodes--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 查询下发码码段信息
     * @param key 顺序码首位 例：0000000000
     * @return
     */
    public String getCodes(String key){
        String res = "";
        try {
            res = QCGChainCode.getInstance("peerOrg1").query(new String[]{"getCodes",key});
        } catch (Exception e) {
            log.error("getCodes--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 查询下发码码段历史信息
     * @param key 顺序码首位 例：0000000000
     * @return
     */
    public String getCodesHistory(String key) {
        String res = "";
        try {
            res = QCGChainCode.getInstance("peerOrg1").query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("getCodesHistory--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    public String uploadTraceInfo(String key, String value){
        String res = "failed";
        try{
            if(log.isDebugEnabled()){
                    log.debug("uploadTraceInfo--key-->"+key);
                log.debug("uploadTraceInfo--value-->"+value);
            }
            res = GreeChainCode.getInstance("peerOrg3").invoke(new String[]{"setTraceInfo",key,value,"trace1"});
        }catch (Exception e){
            log.error("uploadTraceInfo--err-->",e);
            e.printStackTrace();
        }
        return res;
    }


    public String getBlockInfo(String txId,String channelName){
        String res = MoniterUtil.getInstance().getBlockInfo(txId,channelName);
        return res;
    }

    public String deleteBigCode(String key){
        String res = "";
        try{
            res = QCGChainCode.getInstance("peerOrg1").invoke(new String[]{"deleteCodes",key});
        }catch (Exception e){
            log.error("deleteBigCode--error-->",e);
        }
        return res;
    }

    public String deleteTraceInfo(String key){
        if (log.isDebugEnabled()){
            log.debug("deleteTraceInfo--begin");
        }
        String res = "";
        try{
            res = GreeChainCode.getInstance("peerOrg3").invoke(new String[]{"deleteTraceInfo",key});
            if (log.isDebugEnabled()){
                log.debug("deleteTraceInfo--end--res-->"+res);
            }
        }catch (Exception e){
            log.error("deleteTraceInfo--error-->",e);
        }
        return res;
    }

    /**
     * 上传存证信息
     * @param digest 原始文件摘要
     * @param data 鉴真信息（用于鉴真的产品信息）
     * @return
     */
    public String uploadAppraisalInfo(String digest,String data){
        String res = "failed";
        try {
            AppraisalChainCode.getInstance("peerOrg2").invoke(new String[]{"setAppraisalInfo",digest,data});
            res = "success";
        }catch (Exception e){
            log.error("uploadAppraisalInfo--err-->",e);
        }
        return res;
    }

    /**
     * 产品鉴真
     * @param qualityCode 质量码
     * @param data 鉴真信息（用于鉴真的产品信息）
     * @return {"result":"true"}表示产品是真品，{"result":"false"}表示产品非真
     */
    public String checkAppraisalInfo(String qualityCode,String data){
        String res = "{\"result\":\"false\"}";
        try {
            String hash1 = getSHA256StrJava(data);
            String ai  = AppraisalChainCode.getInstance("peerOrg2").query(new String[]{"getTraceInfo",qualityCode});
            JSONObject jo = new JSONObject(ai);
            String hash2 = jo.getString("HASH");
            if(hash1.equals(hash2)){
                res = "{\"result\":\"true\"}";
            }
        } catch (Exception e) {
            log.error("getAppraisalInfo--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取存证信息
     * @param digest
     * @return
     */
    public String getAppraisalInfo(String digest){
        String res = "";
        try {
            res = AppraisalChainCode.getInstance("peerOrg2").query(new String[]{"getAppraisalInfo",digest});
        } catch (Exception e) {
            log.error("getAppraisalInfo--err-->",e);
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
            res = AppraisalChainCode.getInstance("peerOrg2").query(new String[]{"queryAppraisalKeys","{\"selector\":{\"$and\": [{\"USER_ID\":\""+userId+"\"}]}}"});
        }catch (Exception e){
            log.error("queryAppraisalByUser--err-->",e);
        }
        return res;
    }

    public String uploadElectricInfo(String key, String value){
        String res = "failed";
        try{
            res = ElectricChainCode.getInstance("peerOrg4").invoke(new String[]{"setTraceInfo",key,value,"trace1"});
        }catch (Exception e){
            log.error("uploadElectricInfo--err-->",e);
        }
        return res;
    }


    public String deleteElectricInfo(String key){
        if (log.isDebugEnabled()){
            log.debug("deleteElectricInfo--begin");
        }
        String res = "";
        try{
            res = ElectricChainCode.getInstance("peerOrg4").invoke(new String[]{"deleteTraceInfo",key});
        }catch (Exception e){
            log.error("deleteElectricInfo--error-->",e);
        }
        return res;
    }

    public String uploadBuildInfo(String key, String value){
        String res = "failed";
        try{
            res = BuildChainCode.getInstance("peerOrg5").invoke(new String[]{"setTraceInfo",key,value,"trace1"});
        }catch (Exception e){
            log.error("uploadBuildInfo--err-->",e);
        }
        return res;
    }

    public String deleteBuildInfo(String key){
        if (log.isDebugEnabled()){
            log.debug("deleteBuildInfo--begin");
        }
        String res = "";
        try{
            res = BuildChainCode.getInstance("peerOrg5").invoke(new String[]{"deleteTraceInfo",key});
            if (log.isDebugEnabled()){
                log.debug("deleteBuildInfo--end--res-->"+res);
            }
        }catch (Exception e){
            log.error("deleteBuildInfo--error-->",e);
        }
        return res;
    }

    /**
     * 上传阿胶追溯信息
     * @param key
     * @param value
     * @return
     */
    public String uploadEjiaoInfo(String key, String value) {
        String res = "failed";
        try {
            res = EjiaoChainCode.getInstance("peerOrg6").setTraceInfo(key, value);
        } catch (Exception e) {
            log.error("ChaincodeUtil--uploadEjiaoInfo--err-->",e);
        }
        return res;
    }

    /**
     * 获取阿胶追溯信息
     * @param json {"PRODUCT_CODE":"XXX","BAR_CODE":"XXX"}
     * @return
     */
    public String getEjiaoInfo(String json) {
        String res = "";
        try {
            res = EjiaoChainCode.getInstance("peerOrg6").getTraceInfo(json);
        } catch (Exception e) {
            log.error("ChaincodeUtil--getEjiaoInfo--err-->",e);
        }
        return res;
    }

    /**
     * 获取阿胶追溯历史信息
     * @param key
     * @return
     */
    public String getEjiaoHistory(String key) {
        String res = "";
        try {
            res = EjiaoChainCode.getInstance("peerOrg6").getHistory(key);
        } catch (Exception e) {
            log.error("ChaincodeUtil--getEjiaoInfo--err-->",e);
        }
        return res;
    }

    /**
     * 删除阿胶追溯信息
     * @param key
     * @return
     */
    public String deleteEjiaoInfo(String key) {
        String res = "failed";
        try {
            res = EjiaoChainCode.getInstance("peerOrg6").deleteTraceInfo(key);
        } catch (Exception e) {
            log.error("ChaincodeUtil--deleteEjiaoInfo--err-->",e);
        }
        return res;
    }

    /**
     * 上传产品追溯信息
     * @param key 顺序码首位 例：0000000000
     * @param value 产品追溯信息 例：{"BATCH_CODE":"21806840","END":"0000000000","ITEM_NAME":"IH智能电饭煲","ORGANIZE_CODE":"91442000686409571K","PROCESS_DATE":"2017-12-07","PROCESS_JSON":[{"MANAGER":"覃高胜","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"生产","PROCESS_WORK":"元器件装配"},{"MANAGER":"陆秀敏","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"2","PROCESS_TYPE":"生产","PROCESS_WORK":"发热盘装配"},{"MANAGER":"周英","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"3","PROCESS_TYPE":"生产","PROCESS_WORK":"主板组件插线"},{"MANAGER":"杨健新","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"4","PROCESS_TYPE":"生产","PROCESS_WORK":"连接插座"},{"MANAGER":"黎继业","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"5","PROCESS_TYPE":"生产","PROCESS_WORK":"装底座"},{"MANAGER":"梁利芳","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"6","PROCESS_TYPE":"生产","PROCESS_WORK":"面盖转接到支撑环组件"},{"MANAGER":"牛伟","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"7","PROCESS_TYPE":"生产","PROCESS_WORK":"内胆装配"},{"MANAGER":"张洪琼","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"8","PROCESS_TYPE":"生产","PROCESS_WORK":"整机质检"},{"MANAGER":"农吉利","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"9","PROCESS_TYPE":"生产","PROCESS_WORK":"成品包装"},{"MANAGER":"蔺忠","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"仓储","PROCESS_WORK":"入库"},{"MANAGER":"张廷春","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"2","PROCESS_TYPE":"仓储","PROCESS_WORK":"出库"},{"MANAGER":"运隆物流","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"物流","PROCESS_WORK":"配送"},{"MANAGER":"广州销售公司","PROCESS_DATE":"2017-12-07","PROCESS_ORDER":"1","PROCESS_TYPE":"销售","PROCESS_WORK":"经销"}],"PRODUCT_BRAND":"大松（TOSOT）","PRODUCT_CODE":"6937671717293","PRODUCT_SPEC":"GDCF-4001Cf","SERVICE_TEL":"4008 365 315","START":"0000000000","TRACE_CODE_END":"0000000000","TRACE_CODE_START":"0000000000"}
     * @param channel 追溯信息所在链（trace1：格力；trace3：家电链；trace4：建材链；trace5：药材链；trace7：家居链）
     * @param chainOrganMap {CHAINCODE_NAME:'', CHAINCODE_PATH:'', CHAINCODE_VERSION:'', CHAIN_ORGAN_NAME:''}
     * @return
     */
    public String uploadTraceInfo(String key, String value, String channel, Map<String, Object> chainOrganMap){

		String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
		String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
		String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");
		String chaincodeVer = (String)chainOrganMap.get("CHAINCODE_VERSION");
		String peerOrg = (String)chainOrganMap.get("CHAIN_ORGAN_NAME");
		
        String res = "";
        if ("1".equals(traceCodeType)) {
        	res = Trace1ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).setTraceInfo(key, value);
        	
        } else if ("2".equals(traceCodeType)) {
        	res = Trace2ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).setTraceInfo(key, value);
        	
        } else if ("3".equals(traceCodeType)) {
        	res = Trace3ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).setTraceInfo(key, value);
        }
        return res;
    }

    /**
     * 查询产品追溯信息
     * @param key 顺序码首位 例：0000000000
     * @param channel 追溯信息所在链（trace1：格力；trace3：家电链；trace4：建材链；trace5：药材链；trace7：家居链）
     * @return
     */
//    public String getTraceInfo(String key, String channel, String chaincode){
	public String getTraceInfo(String key, String channel, Map chainOrganMap){

		String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
		String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
		String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");
		String chaincodeVer = (String)chainOrganMap.get("CHAINCODE_VERSION");
		String peerOrg = (String)chainOrganMap.get("CHAIN_ORGAN_NAME");
		
		String res = "";
        if ("1".equals(traceCodeType)) {
        	res = Trace1ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).getTraceInfo(key);
        	
        } else if ("2".equals(traceCodeType)) {
        	res = Trace2ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).getTraceInfo(key);
        	
        } else if ("3".equals(traceCodeType)) {
        	res = Trace3ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).getTraceInfo(key);
        }
        return res;
    }

    /**
     * 获取产品追溯信息历史信息
     * @param key 顺序码首位 例：0000000000
     * @param channel 追溯信息所在链（trace1：格力；trace3：家电链；trace4：建材链；trace5：药材链；trace7：家居链）
     * @return
     */
//    public String getTraceHistory(String key, String channel, String chaincode){
	public String getTraceHistory(String key, String channel, Map chainOrganMap){

		String traceCodeType = (String)chainOrganMap.get("TRACE_CODE_TYPE");
		String chaincodeName = (String)chainOrganMap.get("CHAINCODE_NAME");
		String chaincodePath = (String)chainOrganMap.get("CHAINCODE_PATH");
		String chaincodeVer = (String)chainOrganMap.get("CHAINCODE_VERSION");
		String peerOrg = (String)chainOrganMap.get("CHAIN_ORGAN_NAME");
		
		String res = "";
        if ("1".equals(traceCodeType)) {
        	res = Trace1ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).getTraceHistory(key);
        	
        } else if ("2".equals(traceCodeType)) {
        	res = Trace2ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).getTraceHistory(key);
        	
        } else if ("3".equals(traceCodeType)) {
        	res = Trace3ChainCode.getInstance(peerOrg, channel, chaincodeName, chaincodePath, chaincodeVer).getTraceHistory(key);
        }

        return res;
    }

    /**
     * 对字符串进行哈希（SHA256算法）
     * @param str
     * @return
     */
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }
    public static void main(String[] args){
        String res = getSHA256StrJava("abcdefg");
        System.out.println("res-->"+res);
    }

    /**
     * 对输入流信息哈希（SHA256算法）
     * @param input
     * @return
     */
    public String getDigestFromStream(InputStream input){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            byte[] buffer = new byte[1024];
            messageDigest = MessageDigest.getInstance("SHA-256");
            int numRead;
            do{
                numRead = input.read(buffer);
                if (numRead>0){
                    messageDigest.update(buffer,0,numRead);
                }
            }while (numRead!=-1);
            input.close();
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    public String signByPrivateKey(String plainText){
        String signature = "";
        try {
            String priKey = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgayGeERW4jdaj7sye\n" +
                    "ZUYNYsySeYVAei7/3nw7hmGkUMShRANCAAQo3KJGbgFwZ//AK/DgDjIt0idTrGxC\n" +
                    "M3XjN/rEin/Bf7fpmy1Yh+sqG9sdXKBbQq2xRIwGk2DDAQqXZ8nlxxaF";
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(priKey));
            KeyFactory keyf = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyf.generatePrivate(priPKCS8);
            CryptoPrimitives crypto = new CryptoPrimitives();
            crypto.init();
            byte[] b = crypto.sign(privateKey, DatatypeConverter.parseHexBinary(plainText));
            signature = byte2Hex(b);
        } catch (Exception e) {
            log.error("signByPrivateKey--error-->",e);
        }
        return signature;
    }

    public String checkSignature(String plainText,String signture){
        String res = "";
        try{
            String pemHex = "2D2D2D2D2D424547494E202D2D2D2D2D0A4D4949436A444343416A4B6741774942416749554245567773537830546D7164627A4E776C654E42427A6F4954307777436759494B6F5A497A6A3045417749770A667A454C4D416B474131554542684D4356564D78457A415242674E5642416754436B4E6862476C6D62334A7561574578466A415542674E564241635444564E680A62694247636D467559326C7A59323878487A416442674E5642416F54466B6C7564475679626D5630494664705A47646C64484D7349456C75597934784444414B0A42674E564241735441316458567A45554D4249474131554541784D4C5A586868625842735A53356A623230774868634E4D5459784D5445784D5463774E7A41770A5768634E4D5463784D5445784D5463774E7A4177576A426A4D517377435159445651514745774A56557A45584D4255474131554543424D4F546D3979644767670A5132467962327870626D45784544414F42674E564241635442314A68624756705A326778477A415A42674E5642416F54456B6835634756796247566B5A3256790A49455A68596E4A70597A454D4D416F474131554543784D44513039514D466B77457759484B6F5A497A6A3043415159494B6F5A497A6A304441516344516741450A4842754B73414F34336873344A4770466669474D6B422F7873494C54734F766D4E32576D77707350485A4E4C36773848576533784350517464472F584A4A765A0A2B433735364B457355424D337977355054666B7538714F42707A43427044414F42674E56485138424166384542414D4342614177485159445652306C424259770A464159494B7759424251554841774547434373474151554642774D434D41774741315564457745422F7751434D414177485159445652304F42425945464F46430A6463555A346573336C746943674156446F794C66567050494D42384741315564497751594D4261414642646E516A32716E6F492F784D55646E3176446D6447310A6E4567514D43554741315564455151654D427943436D31356147397A6443356A62323243446E6433647935746557687663335175593239744D416F47434371470A534D343942414D43413067414D4555434944663948626C34786E337A3445774E4B6D696C4D396C58324671346A5770416152564239374F6D56456579416945410A32356144505148474771324176684B54307776743038635831475447434962666D754C704D774B516A33383D0A2D2D2D2D2D454E44202D2D2D2D2D0A";
            byte[] pemCert = DatatypeConverter.parseHexBinary(pemHex);
            CryptoPrimitives crypto = new CryptoPrimitives();
            crypto.init();
            boolean rb = crypto.verify(pemCert,"SHA256withECDSA",DatatypeConverter.parseHexBinary(signture),DatatypeConverter.parseHexBinary(plainText));
            if(rb){
                res = "ture";
            }else {
                res = "false";
            }
        }catch (Exception e){
            log.error("checkSignature--error-->",e);
        }
        return res;
    }

    /**
     * 上传药材链流通信息
     * @param key
     * @param value
     * @return
     */
    public String uploadCirculationInfo(String key, String value){
        String res = "failed";
        try{
            res = CirculationChainCode.getInstance("peerOrg2").setTraceInfo(key, value);
        }catch (Exception e){
            log.error("uploadCirculationInfo--err-->",e);
        }
        return res;
    }

    /**
     * 获取药材链流通信息
     * @param json {"PRODUCT_CODE":"XXX","TRACE_CODE":"XXX"}
     * @return
     */
    public String getCirculationInfo(String json){
        String res = "";
        try {
            res = CirculationChainCode.getInstance("peerOrg2").getTraceInfo(json);
        } catch (Exception e) {
            log.error("getCirculationInfo--err-->",e);
        }
        return res;
    }

    /**
     * 获取药材链流通历史信息
     * @param json {"PRODUCT_CODE":"","TRACE_CODE":""}
     * @return
     */
    public String getCirculationHistory(String json){
        String res = "";
        try {
            res = CirculationChainCode.getInstance("peerOrg2").getHistory(json);
        } catch (Exception e) {
            log.error("getCirculationHistory--err-->",e);
        }
        return res;
    }

    /**
     * 上传原料信息到药材链
     * @param key
     * @param value
     * @return
     */
    public String uploadMaterialInfo(String key, String value) {
        String res = "failed";
        try {
            res = MaterialChainCode.getInstance("peerOrg2").setMaterialInfo(key, value);
        } catch (Exception e) {
            log.error("uploadMaterialInfo--err-->",e);
        }
        return res;
    }

    /**
     * 查询药材链原料信息
     * @param json {"PRODUCT_CODE":"XXX","MATERIAL_BATCH":"XXX"}
     * @return
     */
    public String getMaterialInfo(String json) {
        String res = "";
        try {
            res = MaterialChainCode.getInstance("peerOrg2").getMaterialInfo(json);
        } catch (Exception e) {
            log.error("getMaterialInfo--err-->",e);
        }
        return res;
    }

    /**
     * 查询药材链原料历史信息
     * @param json {"PRODUCT_CODE":"","MATERIAL_BATCH":""}
     * @return
     */
    public String getMaterialHistory(String json) {
        String res = "";
        try {
            res = MaterialChainCode.getInstance("peerOrg2").getHistory(json);
        } catch (Exception e) {
            log.error("getMaterialHistory--err-->",e);
        }
        return res;
    }

    /**
     * 删除药材链原料信息
     * @param key
     * @return
     */
    public String deleteMaterialInfo(String key) {
        String res = "";
        try {
            res = MaterialChainCode.getInstance("peerOrg2").deleteMaterialInfo(key);
        } catch (Exception e) {
            log.error("deleteMaterialInfo--err-->",e);
        }
        return res;
    }



}
