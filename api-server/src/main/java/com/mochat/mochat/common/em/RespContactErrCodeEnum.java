package com.mochat.mochat.common.em;

import com.mochat.mochat.common.ex.IHttpCode;

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
public enum RespContactErrCodeEnum implements IHttpCode {

    CONTACT_NO_TAG_GROUP(300001,"标签组不存在"),
    CONTACT_TAG_GROUP_ALREADY_EXISTS(300002,"标签组已存在");

    private int code;
    private String msg;

    RespContactErrCodeEnum(int code, String msg) {
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
