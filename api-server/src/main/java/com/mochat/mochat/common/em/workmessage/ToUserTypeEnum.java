package com.mochat.mochat.common.em.workmessage;

/**
 * @author: yangpengwei
 * @time: 2020/11/27 9:49 上午
 * @description 会话对象类型
 */
public enum ToUserTypeEnum {

    EMPLOYEE(0,"员工"),
    CONTACT(1,"外部联系人"),
    ROOM(2,"群");

    private int code;
    private String msg;

    ToUserTypeEnum(int code, String msg) {
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
