package com.inspur.fabric.sdk.base;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.Serializable;
import java.util.Set;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/9/18
 */
public class BaseUser implements User,Serializable {

    private static final long serialVersionUID = 6115195439508592160L;

    private String name;

    private Set<String> roles;

    private String account;

    private String affiliation;

    private Enrollment enrollment = null;

    private String secret;

    private String mspId;

    private String orgName;

    public BaseUser(String name, String mspId){
        this.name = name;
        this.mspId = mspId;
    }

    public BaseUser(String name, String mspId, String affiliation){
        this.name = name;
        this.mspId = mspId;
        this.affiliation = affiliation;
    }

    public BaseUser(String name, String secret, String mspId, String affiliation){
        this.name = name;
        this.secret = secret;
        this.mspId = mspId;
        this.affiliation = affiliation;
    }


    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }
}
