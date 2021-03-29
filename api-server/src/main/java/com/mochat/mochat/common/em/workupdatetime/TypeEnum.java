package com.mochat.mochat.common.em.workupdatetime;

public enum TypeEnum {
    /**
     * @Message("通讯录")
     */
    EMPLOYEE(1,"通讯录"),

    /**
     * @Message("客户")
     */
    CONTACT(2,"客户"),

    /**
     * @Message("标签")
     */
    TAG(3,"标签"),

    /**
     * @Message("部门")
     */
    DEPARTMENT(4,"部门"),

    CHAT(5,"会放内容存档");

    private int code;
    private String msg;

    TypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
