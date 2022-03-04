package com.mochat.mochat.common.em.workcontactroom;

public enum Status {

    /**
     * @Message("正常")
     */
    NORMAL("1","正常"),

    /**
     * @Message("退群")
     */
    QUIT("2","退群");

    private String code;
    private String msg;

    Status(String code, String msg) {
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
