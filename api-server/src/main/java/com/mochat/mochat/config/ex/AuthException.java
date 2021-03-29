package com.mochat.mochat.config.ex;

import com.mochat.mochat.common.em.RespErrCodeEnum;

/**
 * @description:权限类异常
 * @author: Huayu
 * @time: 2020/12/29 15:38
 */
public class AuthException extends BaseException {

    private int code;
    private String msg;

    public AuthException(){
        this(0);
    }

    public AuthException(int code){
        this(code, RespErrCodeEnum.AUTH_FAILED.getMsg());
    }

    public AuthException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public AuthException(IHttpCode httpCode){
        this(httpCode.getCode(), httpCode.getMsg());
    }

    @Override
    public int getStatus() {
        return RespErrCodeEnum.AUTH_FAILED.getStatus();
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
