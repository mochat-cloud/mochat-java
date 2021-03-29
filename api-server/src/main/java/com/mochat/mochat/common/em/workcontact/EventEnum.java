package com.mochat.mochat.common.em.workcontact;

/**
 * 互动轨迹事件枚举.
 */
public enum EventEnum {

    /**
     * @Message("添加客户")
     */
    CREATE(1, "添加客户"),

    /**
     * @Message("打标签")
     */
    TAG(2, "打标签"),

    /**
     * @Message("修改客户信息")
     */
    INFO(3, "修改客户信息"),

    /**
     * @Message("编辑用户画像")
     */
    USER_PORTRAIT(4, "编辑用户画像");

    private Integer code;
    private String msg;

    EventEnum(Integer code, String msg) {
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
    public static String getByCode(int code) {
        EventEnum[] values = values();
        for (EventEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }
}
