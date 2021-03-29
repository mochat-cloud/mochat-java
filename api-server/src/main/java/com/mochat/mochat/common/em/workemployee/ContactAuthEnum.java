package com.mochat.mochat.common.em.workemployee;

public enum ContactAuthEnum {

    /**
     * @Message("是")
     */
    YES(1, "是"),

    /**
     * @Message("否")
     */
    NO(2, "否");

    private int code;
    private String msg;

    ContactAuthEnum(int code, String msg) {
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
     * @param code
     * @return msg
     */
    public static String getMsgByCode(int code) {
        ContactAuthEnum[] values = values();
        for (ContactAuthEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "否";
    }
}
