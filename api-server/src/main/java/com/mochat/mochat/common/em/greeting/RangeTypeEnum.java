package com.mochat.mochat.common.em.greeting;

/**
 * @description:好友问候语
 * @author: Huayu
 * @time: 2021/2/1 16:57
 */
public enum RangeTypeEnum {

    /**
     * @Message("全体成员")
     */
    ALL(1,"全体成员"),

    /**
     * @Message("指定企业成员")
     */
    ASSIGN(2,"指定企业成员");

    private Integer code;
    private String msg;

    RangeTypeEnum(Integer code, String msg) {
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
        RangeTypeEnum[] values = values();
        for (RangeTypeEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }
}
