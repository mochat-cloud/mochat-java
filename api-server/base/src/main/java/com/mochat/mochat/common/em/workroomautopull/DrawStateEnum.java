package com.mochat.mochat.common.em.workroomautopull;

public enum DrawStateEnum {

    /**
     * @Message("未开始")
     */
    NO_STARTED("1","未开始"),

    /**
     * @Message("拉人中")
     */
    DRAWING("2","拉人中"),

    /**
     * @Message("已拉满")
     */
    FULL("3","依拉曼");

    private String code;
    private String msg;

    DrawStateEnum(String code, String msg) {
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
