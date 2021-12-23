package com.mochat.mochat.common.ex;

/**
 * http 状态码接口
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/21
 */
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
