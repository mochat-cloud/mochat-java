package com.mochat.mochat.common.em.workemployee;

/**
 * 获取状态枚举
 */
public enum EmployeeStatusEnum {

    /**
     * @Message("已激活")
     */
    ACTIVE(1,"已激活"),

    /**
     * @Message("已禁用")
     */
    DISABLED(2,"已禁用"),

    /**
     * @Message("未激活")
     */
    NOTACTIVE(4,"未激活"),

    /**
     * @Message("退出企业")
     */
    QUIT(5,"退出企业");

    private int code;
    private String msg;

    EmployeeStatusEnum(int code, String msg) {
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
        EmployeeStatusEnum[] values = values();
        for (EmployeeStatusEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }
}
