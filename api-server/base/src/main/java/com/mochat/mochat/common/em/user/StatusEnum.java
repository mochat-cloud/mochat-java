package com.mochat.mochat.common.em.user;

public enum StatusEnum {
    /**
     * @Message("未启用")
     */
    NOT_ENABLED(0, "未启用"),

    /**
     * @Message("正常")
     */
    NORMAL(1, "正常"),

    /**
     * @Message("禁用")
     */
    DISABLE(2, "禁用");

    private int code;
    private String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 根据 code 值获取对应的msg
     *
     * @param code
     * @return msg
     */
    public static String getTypeByCode(Integer code) {
        StatusEnum[] values = values();
        for (StatusEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }
}
