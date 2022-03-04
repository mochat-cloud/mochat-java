/**
 * This file is part of MoChat.
 * @link     https://mo.chat
 * @document https://mochat.wiki
 * @contact  group@mo.chat
 * @license  https://github.com/mochat-cloud/mochat-java/blob/master/LICENSE
 */

package com.mochat.mochat.common.em.businesslog;

public enum EventEnum {

    /**
     * @Message("新建渠道码")
     */
    CHANNEL_CODE_CREATE(100,"新建渠道码"),

    /**
     * @Message("编辑渠道码")
     */
    CHANNEL_CODE_UPDATE(101,"编辑渠道码"),

    /**
     * @Message("新建自动拉群")
     */
    ROOM_AUTO_PULL_CREATE(200,"新建自动拉群"),

    /**
     * @Message("编辑自动拉群")
     */
    ROOM_AUTO_PULL_UPDATE(201,"编辑自动拉群"),

    /**
     * @Message("新建欢迎语")
     */
    GREETING_CREATE(300,"新建欢迎语"),

    /**
     * @Message("编辑欢迎语")
     */
    GREETING_UPDATE(301,"编辑欢迎语"),

    /**
     * @Message("新建敏感词")
     */
    SENSITIVE_WORD_CREATE(302,"新建敏感词"),

    /**
     * @Message("编辑敏感词")
     */
    SENSITIVE_WORD_UPDATE(303,"编辑敏感词");

    private int code;
    private String msg;

    EventEnum(int code, String msg) {
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
    public static String getTypeByCode(Integer code) {
        EventEnum[] values = values();
        for (EventEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }

}
