package com.inspur.fabric.sdk.base;

import com.inspur.fabric.client.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.io.*;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/19
 */
public class FabricManager {

    private static final Log log = LogFactory.getLog(FabricManager.class);

    private static final String DEFAULT_CONFIG = "/fabric.properties";
    private static final String PROPBASE = "org.hyperledger.fabric.sdk.";
    private static final String GOSSIPWAITTIME = PROPBASE + "GossipWaitTime";
    private static final String INVOKEWAITTIME = PROPBASE + "InvokeWaitTime";
    private static final String DEPLOYWAITTIME = PROPBASE + "DeployWaitTime";
    private static final String PROPOSALWAITTIME = PROPBASE + "ProposalWaitTime";
    private static final String INTEGRATIONTESTS_ORG = PROPBASE + "integration.org.";
    private static final Pattern orgPat = Pattern.compile("^" + Pattern.quote(INTEGRATIONTESTS_ORG) + "([^\\.]+)\\.mspid$");
    private static final String INTEGRATIONTESTSTLS = PROPBASE + "integration.tls";
    private static final String CERTPATH = "/crypto-config/peerOrganizations/DNAME/ca/ca.DNAME-cert.pem";

    private static final ConcurrentHashMap<String, BaseOrg> orgs = new ConcurrentHashMap<>();

    private static final Properties sdkProperties = new Properties();
    private boolean runningTLS;
    private boolean runningFabricCATLS;
    private boolean runningFabricTLS;

    private static FabricManager manager;
    public static FabricManager getConfig(){
        if (null==manager){
            synchronized (FabricManager.class){
                if (null==manager){
                    manager = new FabricManager();
                }
            }

        }
        return manager;
    }

    private FabricManager(){
        File loadFile;
        FileInputStream configProps;
        try{
            loadFile = new File(this.getClass().getResource("/").getPath()+DEFAULT_CONFIG).getAbsoluteFile();
            configProps = new FileInputStream(loadFile);
            sdkProperties.load(configProps);
        }catch (Exception e){
            log.error("FabricManager constructor err1-->",e);
        }finally {
            defaultProperty(GOSSIPWAITTIME, "5000");
            defaultProperty(INVOKEWAITTIME, "100000");
            defaultProperty(DEPLOYWAITTIME, "120000");
            defaultProperty(PROPOSALWAITTIME, "120000");

            String integrationtls = sdkProperties.getProperty(INTEGRATIONTESTSTLS, null);
            runningTLS = "true".equals(integrationtls);
            runningFabricCATLS = runningTLS;
            runningFabricTLS = runningTLS;

            for (Map.Entry<Object, Object> x : sdkProperties.entrySet()) {
                final String key = x.getKey() + "";
                final String val = x.getValue() + "";
                if (key.startsWith(INTEGRATIONTESTS_ORG)) {
                    Matcher match = orgPat.matcher(key);
                    if (match.matches() && match.groupCount() == 1) {
                        String orgName = match.group(1).trim();
                        try {
                            orgs.put(orgName, new BaseOrg(orgName));
                        } catch (Exception e) {
                            log.error("FabricManager constructor err2-->",e);
                        }
                    }
                }
            }

            for (Map.Entry<String, BaseOrg> entry : orgs.entrySet()) {
                final String orgName = entry.getKey();
                log.error("FabricManager constructor orgName-->"+orgName);
                final BaseOrg org = entry.getValue();
                final String mspId = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".mspid");
                org.setMspId(mspId);
                final String domainName = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".domname");
                final String caLocation = httpTLSify(sdkProperties.getProperty((INTEGRATIONTESTS_ORG + orgName + ".ca_location")));

                String peerNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".peer_locations");
                String[] ps = peerNames.split("[ \t]*,[ \t]*");
                Peers peers = new Peers(orgName,mspId,domainName);
                Orderers orderers = new Orderers();
                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    String peerName = nl[0];
                    String peerLocation = grpcTLSify(nl[1]);
                    peers.addPeer(peerName,orgName, peerLocation, caLocation);
                }

                String eventHubNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".eventhub_locations");
                ps = eventHubNames.split("[ \t]*,[ \t]*");
                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    String peerName = nl[0];
                    String eventHubLocation = grpcTLSify(nl[1]);
                    peers.getPeer(peerName).setPeerEventHubLocation(eventHubLocation);
                }

                String ordererNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".orderer_locations");
                ps = ordererNames.split("[ \t]*,[ \t]*");
                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    String ordererName = nl[0];
                    String ordererLocation = grpcTLSify(nl[1]);
                    orderers.addOrderer(ordererName, ordererLocation);
                }
                org.setPeers(peers);
                org.setOrderers(orderers);
                String tempPath = getChannelPath()+CERTPATH;
                String cert = tempPath.replaceAll("DNAME", domainName);
                File cf = new File(cert);
                if (!cf.exists() || !cf.isFile()) {
                    throw new RuntimeException("missing cert file " + cf.getAbsolutePath());
                }
                Properties properties = new Properties();
                properties.setProperty("pemFile", cf.getAbsolutePath());
                properties.setProperty("allowAllHostNames", "true");
                try {
                    org.setCaClient(HFCAClient.createNewInstance(caLocation,properties));
                } catch (Exception e) {
                    log.error("FabricManager constructor setCaClient error:",e);
                }

                BaseUser admin = new BaseUser("admin","adminpw",mspId,orgName+".admin");
                try {
                    admin.setOrgName(orgName);
                    admin.setEnrollment(org.getCaClient().enroll("admin","adminpw"));
                    org.setAdmin(admin);
                } catch (Exception e) {
                    log.error("FabricManager constructor setAdmin error:",e);
                }

                String path = getChannelPath();
                try{
                    BaseUser peerAdmin = getPeerAdmin(orgName+"Admin",orgName,mspId,
                            findFileSk(Paths.get(path,"/crypto-config/peerOrganizations/",
                                    domainName,String.format("/users/Admin@%s/msp/keystore/", domainName)).toFile()),
                            Paths.get(path,"/crypto-config/peerOrganizations/",domainName,
                                    String.format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", domainName,
                                            domainName)).toFile());
                    peerAdmin.setOrgName(orgName);
                    org.setPeerAdmin(peerAdmin);
                }catch (Exception e){
                    log.error("FabricManager constructor setPeerAdmin error:",e);
                }
            }
        }
    }

    private static void defaultProperty(String key, String value) {
        String ret = System.getProperty(key);
        if (ret != null) {
            sdkProperties.put(key, ret);
        } else {
            String envKey = key.toUpperCase().replaceAll("\\.", "_");
            ret = System.getenv(envKey);
            if (null != ret) {
                sdkProperties.put(key, ret);
            } else {
                if (null == sdkProperties.getProperty(key) && value != null) {
                    sdkProperties.put(key, value);
                }
            }
        }
    }

    private String grpcTLSify(String location) {
        location = location.trim();
        Exception e = Utils.checkGrpcUrl(location);
        if (e != null) {
            throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
        }
        return runningFabricTLS ?
                location.replaceFirst("^grpc://", "grpcs://") : location;

    }

    private String httpTLSify(String location) {
        location = location.trim();

        return runningFabricCATLS ?
                location.replaceFirst("^http://", "https://") : location;
    }

    public String getProperty(String property) {
        String ret = sdkProperties.getProperty(property);
        return ret;
    }

    public int getTransactionWaitTime() {
        return Integer.parseInt(getProperty(INVOKEWAITTIME));
    }

    public int getDeployWaitTime() {
        return Integer.parseInt(getProperty(DEPLOYWAITTIME));
    }

    public int getGossipWaitTime() {
        return Integer.parseInt(getProperty(GOSSIPWAITTIME));
    }

    public long getProposalWaitTime() {
        return Integer.parseInt(getProperty(PROPOSALWAITTIME));
    }

    public Collection<BaseOrg> getOrgsCollection() {
        return Collections.unmodifiableCollection(orgs.values());
    }

    public BaseOrg getOrg(String name){
        log.error("FabricManager getOrg name-->"+name);
        log.error("FabricManager getOrg orgs.size()-->"+orgs.size());
        return orgs.get(name);
    }

    public Properties getPeerProperties(String name) {
        return getEndPointProperties("peer", name);
    }

    public Properties getOrdererProperties(String name) {
        return getEndPointProperties("orderer", name);
    }

    public Properties getEventHubProperties(String name) {
        return getEndPointProperties("peer", name); //uses same as named peer
    }

    private Properties getEndPointProperties(final String type, final String name) {
        final String domainName = getDomainName(name);

        File cert = Paths.get(getChannelPath(), "crypto-config/ordererOrganizations".replace("orderer", type), domainName, type + "s",
                name, "tls/server.crt").toFile();
        if (!cert.exists()) {
            throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
                    cert.getAbsolutePath()));
        }

        Properties ret = new Properties();
        ret.setProperty("pemFile", cert.getAbsolutePath());
        ret.setProperty("hostnameOverride", name);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;
    }

    private String getDomainName(final String name) {
        int dot = name.indexOf(".");
        if (-1 == dot) {
            return null;
        } else {
            return name.substring(dot + 1);
        }
    }

    public BaseUser getPeerAdmin(String name, String org, String mspId, File privateKeyFile, File certificateFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        BaseUser peerAdmin = null;
        peerAdmin = new BaseUser(name,mspId,org+".peerAdmin");
        String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
        PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
        peerAdmin.setEnrollment(new BaseEnrollment(privateKey,certificate));
        return peerAdmin;
    }

    private PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        return privateKey;
    }

    public File findFileSk(File directory) {

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }

        return matches[0];

    }

    public List<String> getOrgNames() {
        return new ArrayList<String>(orgs.keySet());
    }

    public String getChannelPath() {
        String classPath = this.getClass().getResource("/").getPath();
        String channelPath = classPath.substring(0,classPath.lastIndexOf("/"));
        channelPath = channelPath.substring(0,channelPath.lastIndexOf("/"));
        return channelPath+ Constants.CHANNEL_PATH;
    }

}
