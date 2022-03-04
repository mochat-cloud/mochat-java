package com.mochat.mochat.common.em.contactfield;

public enum TypeEnum {

    /**
     * @Message("text")
     */
    TEXT(0, "文本"),
    /**
     * @Message("radio")
     */
    RADIO(1, "单选"),
    /**
     * @Message("checkbox")
     */
    CHECHBOX(2, "多选"),
    /**
     * @Message("select")
     */
    SELECT(3, "下拉"),
    /**
     * @Message("file")
     */
    FILE(4, "文件"),

    /**
     * @Message("textarea")
     */
    TEXTAREA(5,"文本域"),
    /**
     * @Message("date")
     */
    DATE(6, "日期"),
    /**
     * @Message("dateTime")
     */
    DATETIME(7, "日期时间"),
    /**
     * @Message("number")
     */
    NUMBER(8, "数字"),
    /**
     * @Message("phone")
     */
    PHONE(9, "手机号"),
    /**
     * @Message("mail")
     */
    MAIL(10, "邮箱"),
    /**
     * @Message("picture")
     */
    PICTURE(11, "图片");
    private Integer code;
    private String msg;

    TypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
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
    public static String getTypeByCode(int code) {
        TypeEnum[] values = values();
        for (TypeEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }
}
