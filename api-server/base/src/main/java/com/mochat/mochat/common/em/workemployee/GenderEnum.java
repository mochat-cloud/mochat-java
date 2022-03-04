package com.mochat.mochat.common.em.workemployee;

public enum GenderEnum {

    /**
     * @Message("未定义")
     */
    UNDEFINED(0,"未定义"),

    /**
     * @Message("男")
     */
    MAN(1,"男"),

    /**
     * @Message("女")
     */
    WOMAN(2,"女");

    private  int code;
    private  String msg;

    GenderEnum(int code, String msg) {
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
     * @author: zhaojinjian
     * @return msg
     */
    public static String getMsgByCode(int code) {
        GenderEnum[] genders = values();
        for (GenderEnum gender : genders) {
            if (gender.getCode() == code) {
                return gender.getMsg();
            }
        }
        return "未定义";
    }
}
