package com.inspur.api.fabric.chaincode.pub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.fabric.client.ClientConfig;
import com.inspur.fabric.client.ClientHelper;
import com.inspur.fabric.client.SetupMoutaiUsers;

/**
 * @author zhang_lan@inspur.com
 * @description 茅台链码操作类
 * @date 2017/12/8
 */
public class ChaincodeMoutaiUtil {

	private static final Log log = LogFactory.getLog(ChaincodeMoutaiUtil.class);
    private static volatile ChaincodeMoutaiUtil util = null;
    protected static final ClientHelper clientHelper = new ClientHelper();
    protected static final ClientConfig config = ClientConfig.getConfig();
  
    public static ChaincodeMoutaiUtil getInstance(){
        if (null == util){
            try {
            	SetupMoutaiUsers.invoke();
                util = new ChaincodeMoutaiUtil();
            } catch (Exception e) {
                log.error("ChaincodeMoutaiUtil--err-->",e);
                util = null;
            }
        }
        return util;
    }

    public ChaincodeMoutaiUtil() throws Exception {
    }
    
    /**
     * 茅台写链瓶写入数据
     * @param key 实例:UID/UII
     * @param value 实例: {"UID":"1111","PRODUCT_TIME":"2018-6-6 11:36:29","WRITE_CHAIN_TIME":"2018-6-6 11:36:34"}
     * @return
     */
    public String uploadBottleInfo(String uid, String value){
        String res = "failed";
        try {
            if(log.isDebugEnabled()){
                log.debug("ChaincodeMoutaiUtil.uploadMouInfo--uid-->"+uid);
                log.debug("ChaincodeMoutaiUtil.uploadMouInfo--value-->"+value);
            }
            res = Moutai1ChainCode.getInstance("peerMoutai").invoke(new String[]{"setBottleId", uid, value});//调用方法
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
            	log.error("ChaincodeMoutaiUtil.uploadMouInfo--err-->",e);				
			}
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * 瓶箱绑定,设置箱码
     * @param uid
     * @param value
     * @return
     */
    public String uploadSetBoxIdInfo(String uid, String boxId){
        String res = "failed";
        try {
            if(log.isDebugEnabled()){
                log.debug("ChaincodeMoutaiUtil.uploadMouInfo--uid-->"+uid);
                log.debug("ChaincodeMoutaiUtil.uploadMouInfo--value-->"+boxId);
            }
            res = Moutai1ChainCode.getInstance("peerMoutai").invoke(new String[]{"setBoxId", uid, boxId});//调用方法
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
            	log.error("ChaincodeMoutaiUtil.uploadSetBoxIdInfo--err-->",e);				
			}
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 查询瓶码信息
     * @param key 企业编码 例：91442000686409571K
     * @return
     */
    public String getBottleWineInfo(String key){
        String res = "";
        try {
            res = Moutai1ChainCode.getInstance("peerMoutai").query(new String[]{"getWineInfo",key});
        } catch (Exception e) {
            log.error("getWineInfo--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 查询瓶历史信息
     * @param key 企业编码 例：91442000686409571K
     * @return
     */
    public String getBottleHistory(String key) {
        String res = "";
        try {
            res = Moutai1ChainCode.getInstance("peerMoutai").query(new String[]{"getHistory",key});
        } catch (Exception e) {
            log.error("getHistory--err-->",e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 删除瓶信息
     * @param key uid 例：91442000686409571K
     * @return
     */
    public String deleteBottleWineInfo(String key){
        String res = "";
        try{
            res = Moutai1ChainCode.getInstance("peerMoutai").invoke(new String[]{"deleteWineInfo",key});
        }catch (Exception e){
            log.error("deleteWineInfo--error-->",e);
        }
        return res;
    }
    
    /**
     * 产品出库,调用第二个链码
     * @param uid 瓶的id/瓶码
     * @param boxId 箱码
     * @param date 出库时间
     * @return
     */
    public String uploadSetWineInfo(String uid, String boxId, String date){
        String res = "failed";
        try {
            if(log.isDebugEnabled()){
                log.debug("uploadSetWineInfo--uid-->"+uid);
                log.debug("uploadSetWineInfo--boxid-->"+boxId);
                log.debug("uploadSetWineInfo--date-->"+date);
            }
            res = Moutai2ChainCode.getInstance("peerMoutai").invoke(new String[]{"setWineInfo", uid, date,boxId});//调用方法
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
            	log.error("uploadSetWineInfo--err-->",e);				
			}
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * 查询出库信息
     * @param key
     * @return
     */
    public String getOutWineInfo(String key){
        String res = "";
        try {
            res = Moutai2ChainCode.getInstance("peerMoutai").query(new String[]{"getWineInfo",key});
        } catch (Exception e) {
            log.error("getOutWineInfo--err-->",e);
            e.printStackTrace();
        }
        return res;
    }
    /**
     * 删除出库信息
     * @param key uid 例：91442000686409571K
     * @return
     */
    public String deleteOutWineInfo(String key){
        String res = "";
        try{
            res = Moutai1ChainCode.getInstance("peerMoutai").invoke(new String[]{"deleteWineInfo",key});
        }catch (Exception e){
            log.error("deleteWineInfo--error-->",e);
        }
        return res;
    }
    
}
