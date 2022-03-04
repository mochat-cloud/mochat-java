package com.mochat.mochat.model;

import java.io.Serializable;

/**
 * @description:登录的token
 * @author: Huayu
 * @time: 2020/11/20 11:13
 */
public class LoginTokenModel implements Serializable {

    private String token;
    private String expire;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public LoginTokenModel(String token, String expire) {
        this.token = token;
        this.expire = expire;
    }
}
