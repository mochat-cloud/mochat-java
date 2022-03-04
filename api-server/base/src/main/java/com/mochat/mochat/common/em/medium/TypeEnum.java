package com.mochat.mochat.common.em.medium;

/**
 * 获取类型文本
 */
public enum TypeEnum {


    /**
     * @Message("文本")
     */
    TEXT(1,"文本"),

    /**
     * @Message("图片")
     */
    PICTURE(2,"图片"),

    /**
     * @Message("图文")
     */
    PICTURE_TEXT(3,"图文"),

    /**
     * @Message("音频")
     */
    VOICE(4,"音频"),

    /**
     * @Message("视频")
     */
    VIDEO(5,"视频"),

    /**
     * @Message("小程序")
     */
    MINI_PROGRAM(6,"小程序"),

    /**
     * @Message("文件")
     */
    FILE(7,"文件");

    private final Integer code;
    private final String msg;

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
    public static String getTypeByCode(Integer code) {
        TypeEnum[] values = values();
        for (TypeEnum e : values) {
            if (e.getCode().equals(code)) {
                return e.getMsg();
            }
        }
        return "";
    }
}
