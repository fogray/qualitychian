/*
 *  Copyright 2016, 2017 DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.inspur.fabric.base;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A local file-based key value store.
 */
public class BaseStore {
    private String file;
    private Log log = LogFactory.getLog(BaseStore.class);
    public BaseStore(File file) {
        this.file = file.getAbsolutePath();
    }

    /**
     * Get the value associated with name.
     *
     * @param name
     * @return value associated with the name
     */
    public String getValue(String name) {
        Properties properties = loadProperties();
        return properties.getProperty(name);
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
            input.close();
        } catch (FileNotFoundException e) {
            log.warn(String.format("Could not find the file \"%s\"", file));
        } catch (IOException e) {
            log.warn(String.format("Could not load keyvalue store from file \"%s\", reason:%s",
                    file, e.getMessage()));
        }

        return properties;
    }

    /**
     * Set the value associated with name.
     *
     * @param name  The name of the parameter
     * @param value Value for the parameter
     */
    public void setValue(String name, String value) {
        Properties properties = loadProperties();
        try{
            OutputStream output = new FileOutputStream(file);
            properties.setProperty(name, value);
            properties.store(output, "");
            output.close();

            File f = new File(file);
            operationFile(f);
        } catch (IOException e) {
            log.warn(String.format("Could not save the keyvalue store, reason:%s", e.getMessage()));
        }
    }

    public void operationFile(File file) {
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            String filename = file.getName();
            // tmpfile为缓存文件，代码运行完毕后此文件将重命名为源文件名字。
            File tmpfile = new File(file.getParentFile().getAbsolutePath()
                    + "\\" + filename + ".tmp");

            BufferedWriter writer = new BufferedWriter(new FileWriter(tmpfile));

            boolean flag = false;
            String str = null;
            while (true) {
                str = reader.readLine();

                if (str == null)
                    break;

                if (str.contains("\\u0000")) {
                    flag = true;
                }else{
                    writer.write(str + "\n");
                }
            }

            is.close();

            writer.flush();
            writer.close();

            if (flag) {
                file.delete();
                tmpfile.renameTo(new File(file.getAbsolutePath()));
            } else
                tmpfile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Map<String, BaseUser> members = new HashMap<>();

    /**
     * Get the user with a given name
     * @param name
     * @param org
     * @return user
     */
    public BaseUser getMember(String name, String org) {

        // Try to get the baseUser state from the cache
        BaseUser baseUser = members.get(BaseUser.toKeyValStoreName(name, org));
        if (null != baseUser) {
            return baseUser;
        }

        // Create the baseUser and try to restore it's state from the key value store (if found).
        baseUser = new BaseUser(name, org, this);

        return baseUser;

    }

    /**
     * Get the user with a given name
     * @param name
     * @param org
     * @param mspId
     * @param privateKeyFile
     * @param certificateFile
     * @return user
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public BaseUser getMember(String name, String org, String mspId, File privateKeyFile,
                              File certificateFile){

        try {
            // Try to get the baseUser state from the cache
            BaseUser baseUser = members.get(BaseUser.toKeyValStoreName(name, org));
            if (null != baseUser) {
                return baseUser;
            }

            // Create the baseUser and try to restore it's state from the key value store (if found).
            baseUser = new BaseUser(name, org, this);
            baseUser.setMspId(mspId);

            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");

            PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));

            baseUser.setEnrollment(new SampleStoreEnrollement(privateKey, certificate));

            baseUser.saveState();

            return baseUser;
        } catch (Exception e) {
            log.error("BaseStore--getMember--err-->",e);
            e.printStackTrace();
        }
        return null;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Reader pemReader = new StringReader(new String(data));

        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }

        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);

        return privateKey;
    }

    static final class SampleStoreEnrollement implements Enrollment, Serializable {

        private static final long serialVersionUID = -2784835212445309006L;
        private final PrivateKey privateKey;
        private final String certificate;


        SampleStoreEnrollement(PrivateKey privateKey, String certificate)  {


            this.certificate = certificate;

            this.privateKey =  privateKey;
        }

        @Override
        public PrivateKey getKey() {

            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }

    }

}