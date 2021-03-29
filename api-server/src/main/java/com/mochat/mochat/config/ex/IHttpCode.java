package com.mochat.mochat.config.ex;

public interface IHttpCode {

    /**
     * http 头部状态码
     */
    int getStatus();

    /**
     * httpResp 里统一的业务 code 码
     */
    int getCode();

    /**
     * httpResp 里统一的业务 msg
     */
    String getMsg();
}
