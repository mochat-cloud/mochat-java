package com.mochat.mochat.common.em.workcontactemployee;

public enum Status {

    /**
     * @Message("正常")
     */
    NORMAL(1,"正常"),

    /**
     * @Message("删除")
     */
    REMOVE(2,"删除"),

    /**
     * @Message("拉黑")
     */
    BLACKLIST(3,"拉黑");

    private int code;
    private String msg;

    Status(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public final int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static Status getByCode(int code) {
        Status[] values = values();
        for (Status e : values) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
