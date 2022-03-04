package com.mochat.mochat.common.em.workroom;

public enum StatusEnum {

    /**
     * @Message("正常")
     */
    NORMAL("0","正常"),

    /**
     * @Message("跟进人离职")
     */
    QUIT("1","跟进人离职"),

    /**
     * @Message("离职继承中")
     */
    QUIT_INHERIT("2","离职继承中"),

    /**
     * @Message("离职继承完成")
     */
    INHERIT_COMPLETE("3","离职继承完成");

    private String code;
    private String msg;

    StatusEnum(String code, String msg) {
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
