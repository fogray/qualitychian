package com.inspur.fabric.client;

import com.inspur.fabric.base.BaseOrg;
import com.inspur.fabric.base.BaseStore;
import com.inspur.fabric.base.BaseUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.File;
import java.util.Collection;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/8
 */
public class SetupUsers {
    private static final Log log = LogFactory.getLog(SetupUsers.class);
    private static final ClientHelper clientHelper = new ClientHelper();
    private static final ClientConfig clientConfig = ClientConfig.getConfig();
    private static final String TEST_ADMIN_NAME = "admin";
    private static final String TESTUSER_1_NAME = "user1";

    public static void main(String[] args) {
        try {
            invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void invoke() throws Exception {
        Collection<BaseOrg> orgsCollection = clientConfig.getOrgsCollection();

        File storeFile = new File(System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties");
        if (storeFile.exists()) { // For testing start fresh
            return;
        }

        final BaseStore store = new BaseStore(storeFile);

        for (BaseOrg org : orgsCollection) {
            org.setCAClient(HFCAClient.createNewInstance(org.getCALocation(), org.getCAProperties()));


            HFCAClient ca = org.getCAClient();
            final String mspid = org.getMSPID();
            ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            BaseUser admin = store.getMember(TEST_ADMIN_NAME, org.getName());
            if (!admin.isEnrolled()) { // Preregistered admin only needs to be
                admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
                admin.setMspId(mspid);
            }

            org.setAdmin(admin); // The admin of this org --
            if(log.isDebugEnabled()){
                log.debug("SetupUsers--Set admin");
            }

            BaseUser user = store.getMember(TESTUSER_1_NAME, org.getName());
            if (!user.isRegistered()) { // users need to be registered AND enrolled
                RegistrationRequest rr = new RegistrationRequest(user.getName(), "org1.department1");
                try {
                    user.setEnrollmentSecret(ca.register(rr, admin));
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
            if (!user.isEnrolled()) {
                try {
                    user.setEnrollment(ca.enroll(user.getName(), user.getEnrollmentSecret()));
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
                user.setMspId(mspid);
            }
            org.addUser(user); // Remember user belongs to this Org
            if(log.isDebugEnabled()){
                log.debug("SetupUsers--Set org user");
            }

            clientHelper.setPeerAdmin(store, org);
            if(log.isDebugEnabled()){
                log.debug("SetupUsers--Set peer admin");
            }
            if(log.isDebugEnabled()){
                log.debug("SetupUsers--Set up users for "+org.getName()+". OK!");
            }
        }
    }

}
