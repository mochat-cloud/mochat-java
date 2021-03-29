package com.mochat.mochat.common.em.sensitivewordsmonitor;

/**
 * @description:来源
 * @author: Huayu
 * @time: 2021/3/10 10:56
 */
public enum SourceEnum {
    /**
     * @Message("客户")
     */
    CONTACT(1,"客户"),

    /**
     * @Message("员工")
     */
    EMPLOYEE(2,"员工");

    private Integer code;
    private String msg;

    SourceEnum(Integer code, String msg) {
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
    public static String getTypeByCode(Integer code) {
        SourceEnum[] values = values();
        for (SourceEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }

}
