package com.inspur.fabric.sdk.base;

import com.inspur.fabric.sdk.user.IFabricUserService;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.loushang.waf.ComponentFactory;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/20
 */
public class UserManager {
    private static final Log log = LogFactory.getLog(UserManager.class);

    private static UserManager instance = null;

    private final IFabricUserService fabricUserService = (IFabricUserService) ComponentFactory.getBean("fabricUserService");

    public static UserManager getInstance(){
        if(instance==null){
            synchronized (UserManager.class){
                if(instance==null){
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    private UserManager(){
    }

    public String registerUser(String userName, BaseOrg org) throws Exception {

        String affiliationPrefix = org.getName().replaceAll("peer","");
        affiliationPrefix = affiliationPrefix.toLowerCase();
        RegistrationRequest request = new RegistrationRequest(userName,"org1.department1");
        String secret = org.getCaClient().register(request, org.getAdmin());
        return secret;
    }

    public BaseEnrollment enrollUser(String user, String secret, BaseOrg org) throws Exception {
        Enrollment enrollment = org.getCaClient().enroll(user,secret);
        BaseEnrollment be = new BaseEnrollment(enrollment.getKey(),enrollment.getCert());
        return be;
    }

    public BaseUser register(String userName) throws Exception {
        if (log.isDebugEnabled()){
            log.debug("UserManager register begin");
        }
        BaseUser user = getUserFromDatabase(userName);
        //如果未从数据库中查到相应数据，则进行用户的注册和登记
        if(user.getEnrollment() == null){
            BaseOrg org = FabricManager.getConfig().getOrg(user.getOrgName());
            if (org ==null){
                log.error("UserManager register org=null   orgName-->"+user.getOrgName());
            }else {
                log.error("UserManager register org!=null   orgName-->"+user.getOrgName());
            }
            String secret = registerUser(userName,org);
            BaseEnrollment enrollment = enrollUser(userName, secret, org);
            if(enrollment!=null){
                saveUserData(userName,secret, HexBin.encode(enrollment.getKey().getEncoded()),enrollment.getCert());
            }
            user.setEnrollment(enrollment);
        }
        return user;
    }

    /**
     * 从uc_user_info表中根据USER_ID获取ORGAN_NAME，MSP_ID，FABRIC_SECRET，FABRIC_KEY，FABRIC_CERT
     * 如果FABRIC_SECRET为空说明该用户尚未在fabric中注册
     * @param userName
     * @return
     */
    private BaseUser getUserFromDatabase(String userName) throws Exception {
        log.error("UserManager getUserFromDatabase begin userName-->"+userName);
        BaseUser user = null;
        if(userName.endsWith("Admin")){
            try {
                String orgName = userName.replaceAll("Admin","");
                log.error("UserManager getUserFromDatabase orgName-->"+orgName);
                user = FabricManager.getConfig().getOrg(orgName).getPeerAdmin();
                return user;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //模拟查数据库未查到秘钥证书信息，表明该用户还未在fabric中注册
        Map<String, Object> userInfo = fabricUserService.getUserInfo(userName);
        if (userInfo==null){
            userInfo = fabricUserService.getAdminInfo(userName);
            if (userInfo==null){
                return null;
            }

        }
        log.error("UserManager getUserFromDatabase userInfo-->"+userInfo);
        String mspId = (String)userInfo.get("MSP_ID");
        String orgName = (String)userInfo.get("ORGAN_NAME");
        user = new BaseUser(userName,mspId);
        user.setOrgName(orgName);

        String secret = (String)userInfo.get("FABRIC_SECRET");
        String key = (String)userInfo.get("FABRIC_KEY");
        String cert = (String)userInfo.get("FABRIC_CERT");
        if(secret!=null && !"".equals(secret)){
            PrivateKey pk = getPrivateKey(key);
            BaseEnrollment enrollment = new BaseEnrollment(pk,cert);
            user.setEnrollment(enrollment);
        }
        return user;
    }

    private void saveUserData(String userName,String secret,String privateKey,String cert){
        //模拟将user信息插入用户表中
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID",userName);
        map.put("FABRIC_SECRET",secret);
        map.put("FABRIC_KEY",privateKey);
        map.put("FABRIC_CERT",cert);
        fabricUserService.updateUserInfo(map);
    }

    private PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = HexBin.decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey pk = keyFactory.generatePrivate(keySpec);
        return pk;
    }


}
