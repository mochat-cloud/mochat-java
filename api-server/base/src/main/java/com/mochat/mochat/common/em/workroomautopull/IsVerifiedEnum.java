package com.mochat.mochat.common.em.workroomautopull;

public enum IsVerifiedEnum {

    /**
     * @Message("需验证")
     */
    VERIFICATION("1","需验证"),

    /**
     * @Message("直接通过")
     */
    PASS("2","直接通过");

    private String code;
    private String msg;

    IsVerifiedEnum(String code, String msg) {
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
