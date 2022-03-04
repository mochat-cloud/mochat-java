package com.mochat.mochat.config.ex;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.ex.BaseException;

public class ParamException extends BaseException {

    private int code;
    private String msg;

    public ParamException(){
        this(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
    }

    public ParamException(String msg){
        this(RespErrCodeEnum.INVALID_PARAMS.getCode(), msg);
    }

    public ParamException(int code, String msg){
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
