package com.mochat.mochat.common.em;

import com.mochat.mochat.config.ex.IHttpCode;

/**
 * 错误code
 * 3位HTTP码 + 6位业务码[前3位为模块，后3位为业务]
 * 有其它错误码需求，即使补充
 * 业务模块码:
 * 100  -  公共模块
 * 100  -  授权模块
 * 200  -  通讯录模块
 * 300  -  外部联系人模块
 * 301  -  外部联系人 - 高级属性模块
 * 400  -  会话内容存档模块.
 * @Constants
 * @method static string getMessage(int $code)  获取错误码信息
 * @method static int getHttpCode(int $code) 获取错误码的httpCode
 */
public enum RespErrCodeEnum implements IHttpCode {
    /**
     * @Message("token失效")
     * @HttpCode("401")
     */
    TOKEN_INVALID(401,100001,"token失效"),
    /**
     * @Message("用户或密码错误")
     * @HttpCode("401")
     */
    AUTH_LOGIN_FAILED(401,100002,"用户或密码错误"),

    /**
     * @Message("非法token")
     * @HttpCode("401")
     */
    AUTH_TOKEN_INVALID(401,100003,"非法token"),

    /**
     * @Message("token过期")
     * @HttpCode("401")
     */
    AUTH_SESSION_EXPIRED(401,100004,"token过期"),

    /**
     * @Message("未认证,没有token")
     * @HttpCode("401")
     */
    AUTH_UNAUTHORIZED(401,100005,"未认证,没有token"),

    /**
     * @Message("认证失败")
     * @HttpCode("401")
     */
    AUTH_FAILED(401,100006,"认证失败"),

    /**
     * @Message("没有权限")
     * @HttpCode("403")
     */
    ACCESS_DENIED(403,100007,"没有权限"),

    /**
     * @Message("拒绝客户端请求")
     * @HttpCode("403")
     */
    ACCESS_REFUSE(403,100008,"拒绝客户端请求"),

    /**
     * @Message("禁止重复操作")
     * @HttpCode("403")
     */
    NO_REPETITION_OPERATION(403,100009,"禁止重复操作"),

    /**
     * @Message("客户端错误")
     * @HttpCode("400")
     */
    BAD_REQUEST(400,100010,"客户端错误"),

    /**
     * @Message("非法的Content-Type头")
     * @HttpCode("401")
     */
    INVALID_CONTENT_TYPE(401,100011,"非法的Content-Type头"),

    /**
     * @Message("资源未找到")
     * @HttpCode("404")
     */
    URI_NOT_FOUND(404,100012,"资源未找到"),

    /**
     * @Message("非法的参数")
     * @HttpCode("422")
     */
    INVALID_PARAMS(422,100013,"非法的参数"),

    /**
     * @Message("服务器异常")
     * @HttpCode("500")
     */
    SERVER_ERROR(500,100014,"服务器异常"),

    /**
     * @Message("服务器异常(third-party-api)")
     * @HttpCode("500")
     */
    THIRD_API_ERROR(500,100015,"服务器异常(third-party-api)");

    private Integer status;

    private Integer code;

    private String msg;


    RespErrCodeEnum(Integer status,Integer code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
