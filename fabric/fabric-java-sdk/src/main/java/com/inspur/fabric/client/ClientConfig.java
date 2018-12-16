package com.inspur.fabric.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.helper.Utils;

import com.inspur.api.pub.PropertiesLoaderUtils;
import com.inspur.fabric.base.BaseOrg;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/12/8
 */
public class ClientConfig {
    private static final Log log = LogFactory.getLog(ClientConfig.class);

    public static final String CHAINCODE_SRC_TAR_PATH = "/channels/chaincodes/tar/";
    public static final String CHAINCODE_SRC_PATH = "/channels/chaincodes/src/";
    public static final String CHAINCODE_ENDORSEMENTPOLICY_FILE = "/channels/chaincodes/chaincodeendorsementpolicy.yaml";
    private static final String DEFAULT_CONFIG = "fabric.properties";
    private static final String PROPBASE = "org.hyperledger.fabric.sdk.";
    private static final String GOSSIPWAITTIME = PROPBASE + "GossipWaitTime";
    private static final String INVOKEWAITTIME = PROPBASE + "InvokeWaitTime";
    private static final String DEPLOYWAITTIME = PROPBASE + "DeployWaitTime";
    private static final String PROPOSALWAITTIME = PROPBASE + "ProposalWaitTime";
    private static final String INTEGRATIONTESTS_ORG = PROPBASE + "integration.org.";
    private static final Pattern orgPat = Pattern.compile("^" + Pattern.quote(INTEGRATIONTESTS_ORG) + "([^\\.]+)\\.mspid$");
    private static final String INTEGRATIONTESTSTLS = PROPBASE + "integration.tls";

//    private static String CERTPATH = getChannelPath()+"/crypto-config/peerOrganizations/DNAME/ca/ca.DNAME-cert.pem";

    private static final HashMap<String, BaseOrg> orgs = new HashMap<>();

    private static Properties sdkProperties = new Properties();
    private boolean runningTLS;
    private boolean runningFabricCATLS;
    private boolean runningFabricTLS;

    private static ClientConfig config;
    public static ClientConfig getConfig(){
        if (null==config){
            config = new ClientConfig();
        }
        return config;
    }
    private ClientConfig(){
        File loadFile;
        FileInputStream configProps;
        try{
//            loadFile = new File(this.getClass().getResource("/").getPath()+DEFAULT_CONFIG).getAbsoluteFile();
//            PropertiesLoaderUtils.loadProperties(EncodedResource.class);
            sdkProperties = PropertiesLoaderUtils.loadAllProperties(DEFAULT_CONFIG);
//            configProps = new FileInputStream(this.getClass().getResourceAsStream(DEFAULT_CONFIG));
//            sdkProperties.load(configProps);
        }catch (Exception e){
            log.error("ClientConfig-->",e);
            e.printStackTrace();
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
                        orgs.put(orgName, new BaseOrg(orgName,val.trim()));
                    }
                }
            }

            for (Map.Entry<String, BaseOrg> entry : orgs.entrySet()) {
                final String orgName = entry.getKey();
                final BaseOrg org = entry.getValue();

                String peerNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".peer_locations");
                String[] ps = peerNames.split("[ \t]*,[ \t]*");
                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    org.addPeerLocation(nl[0],grpcTLSify(nl[1]));
                }
                final String domainName = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".domname");
                org .setDomainName(domainName);

                String ordererNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".orderer_locations");
                ps = ordererNames.split("[ \t]*,[ \t]*");
                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    org.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
                }

                String eventHubNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".eventhub_locations");
                ps = eventHubNames.split("[ \t]*,[ \t]*");
                for (String peer : ps) {
                    String[] nl = peer.split("[ \t]*@[ \t]*");
                    org.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
                }
                org.setCALocation(httpTLSify(sdkProperties.getProperty((INTEGRATIONTESTS_ORG + orgName + ".ca_location"))));

                if (runningFabricCATLS) {
                    String CERTPATH = getChannelPath()+"/crypto-config/peerOrganizations/DNAME/ca/ca.DNAME-cert.pem";
                    String cert = CERTPATH.replaceAll("DNAME", domainName);
                    File cf = new File(cert);
                    if (!cf.exists() || !cf.isFile()) {
                        throw new RuntimeException("TEST is missing cert file " + cf.getAbsolutePath());
                    }
                    Properties properties = new Properties();
                    properties.setProperty("pemFile", cf.getAbsolutePath());

                    properties.setProperty("allowAllHostNames", "true"); //testing environment only NOT FOR PRODUCTION!

                    org.setCAProperties(properties);
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

    private String getProperty(String property) {
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

        File cert = new File(getChannelPath()+"/crypto-config/ordererOrganizations/".replace("orderer", type)+ domainName+"/"+ type + "s"+
                "/"+name+ "/tls/server.crt");
        if (!cert.exists()) {
            throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
                    cert.getAbsolutePath()));
        }

        Properties ret = new Properties();
        ret.setProperty("pemFile", cert.getAbsolutePath());
        //      ret.setProperty("trustServerCertificate", "true"); //testing environment only NOT FOR PRODUCTION!
        ret.setProperty("hostnameOverride", name);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");
        ret.setProperty("ordererWaitTimeMilliSecs", "30000");

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

    public String getChannelPath() {
        String classPath = this.getClass().getResource("/").getPath();
        String channelPath = classPath.substring(0,classPath.lastIndexOf("/"));
        channelPath = channelPath.substring(0,channelPath.lastIndexOf("/"));
        return "D:/"+Constants.CHANNEL_PATH;
    }
}
