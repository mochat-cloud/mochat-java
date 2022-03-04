package com.mochat.mochat.common.em.sensitivewordsmonitor;

/**
 * @description:接收者
 * @author: Huayu
 * @time: 2021/3/10 10:55
 */
public enum ReceiverTypeEnum {
    /**
     * @Message("员工")
     */
    EMPLOYEE(1,"员工"),

    /**
     * @Message("外部联系人")
     */
    CONTACT(2,"外部联系人"),

    /**
     * @Message("群聊")
     */
    ROOM(3,"群聊");

    private Integer code;
    private String msg;

    ReceiverTypeEnum(Integer code, String msg) {
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
        ReceiverTypeEnum[] values = values();
        for (ReceiverTypeEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }

}
