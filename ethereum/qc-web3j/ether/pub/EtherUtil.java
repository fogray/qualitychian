package com.inspur.ether.pub;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.loushang.waf.ComponentFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

import com.inspur.ether.trans.ITransRecordService;
import com.inspur.pub.cache.CacheUtil;
import com.inspur.pub.fileUtil.FileIoTool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EtherUtil {

    private static Log log = LogFactory.getLog(EtherUtil.class);
    private static ITransRecordService transRecordService = (ITransRecordService) ComponentFactory.getBean("transRecordService");
    
    private static ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(30);
    
    public static Admin adminClient = null;
    public static Web3j web3jClient = null;
    private static final String WALLET_DIR = "/tmp/inspurwallet/keys/";
    
//    static {
        // 监听区块信息
//        watchingBlock();
//    }
    public static void initAdminClient() {
        if (adminClient == null) {
            synchronized(EtherUtil.class) {
                if (adminClient == null) {
                    try {
                        adminClient = Admin.build(new HttpService(CacheUtil.getOrganParamsValue("ETHEREUM_RPC_URL")));
                    } catch(Exception e) {
                        log.error("EtherUtil initAdminClient error.", e);
                        adminClient = null;
                    }
                }
            }
//            adminClient = Admin.build(new HttpService("http://192.168.73.101:7545"));
        }
    }
    public static void initWeb3jClient() {
        if (web3jClient == null) {
            synchronized(EtherUtil.class) {
                if (web3jClient == null) {
                    try {
                        web3jClient = Web3j.build(new HttpService(CacheUtil.getOrganParamsValue("ETHEREUM_RPC_URL")));
                    } catch(Exception e) {
                        log.error("EtherUtil initWeb3jClient error.", e);
                        web3jClient = null;
                    }
                }
            }
//            web3jClient = Web3j.build(new HttpService("http://192.168.73.101:7545"));
        }
    }
    
    /**
     * 创建以太坊账户
     * @param pwd
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     * @throws CipherException
     * @throws IOException
     */
    public static Map<String, String> newAccount(String pwd) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        Map<String, String> account = new HashMap<String, String>();
        Credentials credentials = null;
        String privKey = null;
        File keyFile = null;
        File dir = new File(WALLET_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        do {
            String jsonFileName = WalletUtils.generateLightNewWalletFile(pwd, new File(WALLET_DIR));
            keyFile = new File(WALLET_DIR+jsonFileName);
            String content = FileIoTool.read(keyFile.getAbsolutePath());
            JSONObject obj = JSONObject.fromObject(content);
            Object kdf = obj.getJSONObject("crypto").get("kdf");
            if (kdf instanceof JSONArray) {
                kdf = ((JSONArray) kdf).get(0);
                obj.getJSONObject("crypto").put("kdf", kdf);
                FileIoTool.write(keyFile.getAbsolutePath(), obj.toString());
            }
            credentials = WalletUtils.loadCredentials(pwd, keyFile);
            privKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
            if (WalletUtils.isValidPrivateKey(privKey)) {
                break;
            }
            keyFile.deleteOnExit();
        } while(true);
        account.put("address", credentials.getAddress());
        account.put("privatekey", "0x"+privKey);
        account.put("wallet", FileIoTool.read(keyFile.getAbsolutePath()));
        account.put("pwd", pwd);
        return account;
    }
    
    /**
     * 获取账户余额
     * @param user
     * @return
     * @throws IOException
     */
    public static String getUserBalance(String user) throws IOException {
        initAdminClient();
        EthGetBalance balance = adminClient.ethGetBalance(user, DefaultBlockParameter.valueOf("latest")).send();
        return balance.getBalance().toString(10);
    }
    
    /**
     * 获取最新区块
     * @return
     */
    public static EthBlock getLatestBlock() {
        initAdminClient();
        try {
            return adminClient.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).send();
        } catch (IOException e) {
            log.error("getLatestBlock error.", e);
            return null;
        }
    }
    
    /**
     * 获取最新区块Hash
     * @return
     */
    public static String getLatestBlockHash() {
        EthBlock latestBlock = getLatestBlock();
        if (latestBlock != null) {
            return latestBlock.getBlock().getHash();
        } 
        return "0x0000000000000000000000000000000000000000000000000000000000000000";
    }
    
    /**
     * 监听区块信息
     */
//    private static void watchingBlock() {
//        initWeb3jClient();
//        if (web3jClient != null) {
//            web3jClient.blockObservable(false).
//                    subscribe(block -> {
//                        gNonce = block.getBlock().getNonce().add(BigInteger.valueOf(1));
//                    });
//        }
//    }
    public static BigInteger gNonce = null;
    /**
     * 转账
     * @param fromTo Map: fromUserId、fromUserAddr、fromUserPwd、toUserId、toUserAddr
     * @param value
     * @param transferType 转账类型
     * @param uniqueId 交易唯一id：如任务回答id、五彩石兑换流水id
     * @return
     */
    private static Map<String, BigInteger> preTransFrom = new HashMap<String, BigInteger>();
    public static void transfer(Map<String,String> fromTo, BigDecimal value, String transferType,String uniqueId) {
        stpe.execute(new Runnable() {
            @Override
            public void run() {
                String userId = fromTo.get("fromUserId");
                String from = fromTo.get("fromUserAddr");
                String fromUserPriv = fromTo.get("fromUserPriv");
                String toUserId = fromTo.get("toUserId"); 
                String to = fromTo.get("toUserAddr");
                initWeb3jClient();
                String errorMsg = null;
                try {
                   EthSendTransaction transactionResponse = null;
                   synchronized(from.intern()) {
                       BigInteger gasPrice = BigInteger.valueOf(0);
                       BigInteger gasLimit = BigInteger.valueOf(21000);
                       BigDecimal weis = Convert.toWei(value, Unit.ETHER);
                       
                       if (!preTransFrom.containsKey(from)) {
                           EthGetTransactionCount ethGetTransactionCount = web3jClient.ethGetTransactionCount(
                                   from, DefaultBlockParameterName.LATEST).sendAsync().get();
                           gNonce = ethGetTransactionCount.getTransactionCount();
                       } else {
                           gNonce = preTransFrom.get(from);
                       }
                       Credentials credentials = Credentials.create(fromUserPriv);
                       RawTransaction rawT = RawTransaction.createEtherTransaction(gNonce, gasPrice, gasLimit, to, weis.toBigInteger());
                       byte[] signedMessage = TransactionEncoder.signMessage(rawT, credentials);
                       String hexValue = Numeric.toHexString(signedMessage);
                       transactionResponse = web3jClient.ethSendRawTransaction(hexValue).sendAsync().get();
                       gNonce = gNonce.add(BigInteger.valueOf(1));
                       preTransFrom.put(from, gNonce);
                   }
                   if(transactionResponse.hasError()){
                       String message=transactionResponse.getError().getMessage();
                       log.error("EtherUtil transfer error. transferType："+transferType+" error message="+transactionResponse.getError().getMessage());
                       throw new Exception(message);
                   }else{
                       String hash=transactionResponse.getTransactionHash();
                       log.debug("transferType="+transferType+"：transaction hash="+hash+", from="+from+", to="+to+", value="+value);
                       Map<String, Object> bean = new HashMap<String, Object>();
                       bean.put("USER_ID", userId);
                       bean.put("TO_USER_ID", toUserId);
                       bean.put("VALUE", value);
                       bean.put("STATUS", "1");
                       bean.put("TRANSACTION_HASH", hash);
                       bean.put("CREATE_TIME", new Timestamp(System.currentTimeMillis()));
                       bean.put("UPDATE_TIME", new Timestamp(System.currentTimeMillis()));
                       bean.put("TRANSFER_TYPE", transferType);
                       bean.put("ANSWER_ID", uniqueId);
                       bean.put("NOTE", null);
                       transRecordService.insertTrasnRecord(bean);
                   }
                } catch(Exception e) {
                    log.error("EtherUtil transfer error.", e);
                    errorMsg = e.getMessage().substring(0, 200);
                    Map<String, Object> bean = new HashMap<String, Object>();
                    bean.put("USER_ID", userId);
                    bean.put("TO_USER_ID", toUserId);
                    bean.put("VALUE", value);
                    bean.put("STATUS", "0");
                    bean.put("TRANSACTION_HASH", null);
                    bean.put("CREATE_TIME", new Timestamp(System.currentTimeMillis()));
                    bean.put("UPDATE_TIME", new Timestamp(System.currentTimeMillis()));
                    bean.put("TRANSFER_TYPE", transferType);
                    bean.put("ANSWER_ID", uniqueId);
                    bean.put("NOTE", errorMsg);
                    transRecordService.insertTrasnRecord(bean);
                }
            }
        });
    }
    
}
