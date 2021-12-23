package com.mochat.mochat.common.em;

import com.mochat.mochat.common.ex.IHttpCode;

public enum RespChatErrCodeEnum implements IHttpCode {

    CHAT_NO_CORP(400001,"企业信息不存在"),
    AUTH_LOGIN_FAILED(400002,"用户或密码错误"),
    NO_FIND_TENANT(400003,"未查询到租户信息");

    private int code;
    private String msg;

    RespChatErrCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * http 头部状态码
     */
    @Override
    public int getStatus() {
        return RespErrCodeEnum.SERVER_ERROR.getStatus();
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
