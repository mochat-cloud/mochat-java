package com.mochat.mochat.common.em.workcontactroom;

public enum JoinSceneEnum {

    /**
     * @Message("由成员邀请入群（直接邀请入群）")
     */
    DIRECT_INVITE("1","由成员邀请入群（直接邀请入群）"),

    /**
     * @Message("由成员邀请入群（通过邀请链接入群）")
     */
    LINK_INVITE("2","由成员邀请入群（通过邀请链接入群）"),

    /**
     * @Message("通过扫描群二维码入群")
     */
    QRCODE("3","通过扫描群二维码入群");

    private String code;
    private String msg;

    JoinSceneEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
