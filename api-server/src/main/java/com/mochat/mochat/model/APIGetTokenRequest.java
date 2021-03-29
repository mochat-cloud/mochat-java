package com.mochat.mochat.model;

public class APIGetTokenRequest {

    private String corpid;//企业id
    private String corpsecret;//企业秘钥

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getCorpsecret() {
        return corpsecret;
    }

    public void setCorpsecret(String corpsecret) {
        this.corpsecret = corpsecret;
    }

}
