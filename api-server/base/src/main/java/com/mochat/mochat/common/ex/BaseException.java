package com.mochat.mochat.common.ex;

import com.mochat.mochat.common.em.RespErrCodeEnum;

/**
 * 自定义异常基类
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/21
 */
public class BaseException extends RuntimeException implements IHttpCode {

    private IHttpCode iHttpCode = RespErrCodeEnum.SERVER_ERROR;

    public static BaseException getInstance(IHttpCode iHttpCode) {
        BaseException exception = new BaseException();
        exception.iHttpCode = iHttpCode;
        return exception;
    }

    @Override
    public int getStatus() {
        return iHttpCode.getStatus();
    }

    @Override
    public int getCode() {
        return iHttpCode.getCode();
    }

    @Override
    public String getMsg() {
        return iHttpCode.getMsg();
    }
}
