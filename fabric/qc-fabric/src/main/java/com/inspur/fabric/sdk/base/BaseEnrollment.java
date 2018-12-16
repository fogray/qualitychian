package com.inspur.fabric.sdk.base;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.security.PrivateKey;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/19
 */
public class BaseEnrollment implements Enrollment,Serializable {
    private static final long serialVersionUID = 6760301625416217957L;

    private final PrivateKey privateKey;

    private final String certificate;

    //这个组织代表该enrollment是由哪个组织注册登记得到的
    private String orgName;

    BaseEnrollment(PrivateKey privateKey, String certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }

    @Override
    public PrivateKey getKey() {
        return privateKey;
    }

    @Override
    public String getCert() {
        return certificate;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
