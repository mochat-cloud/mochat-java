package com.mochat.mochat.common.em.contactfield;

public enum StatusEnum {

    /**
     * @Message("不展示")
     */
    NO_EXHIBITION("0","不展示"),

    /**
     * @Message("展示")
     */
    EXHIBITION("1","展示");

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
