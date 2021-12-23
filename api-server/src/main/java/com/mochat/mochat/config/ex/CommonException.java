package com.mochat.mochat.config.ex;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.ex.BaseException;
import com.mochat.mochat.common.ex.IHttpCode;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/12/7 14:27
 */
public class CommonException extends BaseException {

    private int code;
    private String msg;

    public CommonException(){
        this(RespErrCodeEnum.SERVER_ERROR.getCode());
    }
    public CommonException(String msg){
        this(RespErrCodeEnum.SERVER_ERROR.getCode(), msg);
    }

    public CommonException(int code){
        this(code, RespErrCodeEnum.SERVER_ERROR.getMsg());
    }

    public CommonException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public CommonException(IHttpCode httpCode){
        this(httpCode.getCode(), httpCode.getMsg());
    }

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
