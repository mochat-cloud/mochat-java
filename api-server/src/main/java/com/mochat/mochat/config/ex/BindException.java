package com.mochat.mochat.config.ex;

import com.mochat.mochat.common.em.RespErrCodeEnum;

/**
 * @description:
 * @author: Andy
 * @time: 2020/12/3 18:22
 */
public class BindException extends BaseException{

    private int code;
    private String msg;

    public BindException(){
        this(0);
    }

    public BindException(int code){
        this(code, RespErrCodeEnum.INVALID_PARAMS.getMsg());
    }

    public BindException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getStatus() {
        return RespErrCodeEnum.INVALID_PARAMS.getStatus();
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
