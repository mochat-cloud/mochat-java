package com.mochat.mochat.common.em.workmessage;

/**
 * 获取消息类型
 */
public enum MsgTypeEnum {


    TEXT(1, "文本"),

    IMAGE(2, "图片"),

    REVOKE(8, "撤回消息"),

    AGREE(9, "同意会话聊天内容"),

    VOICE(4, "音频"),

    VIDEO(5, "视频"),

    CARD(10, "名片"),

    LOCATION(11, "位置"),

    EMOTION(12, "表情"),

    FILE(7, "文件"),

    LINK(13, "链接"),

    WEAPP(6, "小程序"),

    CHATRECORD(14, "会话记录消息"),

    CHAT_RECORD_ITEM(15, "会话记录消息item"),

    TODO(16, "待办消息"),

    VOTE(17, "投票消息"),

    COLLECT(18, "填表消息"),

    RED_PACKET(19, "红包消息"),
    REDPACKET(19, "红包消息"),

    MEETING(20, "会议邀请消息"),

    DOC_MSG(21, "在线文档消息"),
    DOCMSG(21, "在线文档消息"),

    MARKDOWN(22, "MarkDown"),

    PICTURE_TEXT(3, "图文"),
    NEWS(3, "图文"),

    CALENDAR(23, "日程消息"),

    MIXED(24, "混合消息"),

    MEETING_VOICE_CALL(25, "音频存档消息"),

    VOIP_DOC_SHARE(26, "音频共享文档消息"),

    DISAGREE(27,"不同意会话聊天内容"),

    EXTERNAL_REDPACKET(28,"红包消息");

    private int code;
    private String msg;

    MsgTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static boolean isOther(int code) {
        return code < 1 || code > 7;
    }

    public static MsgTypeEnum getEnum(int code) {
        MsgTypeEnum[] enums = MsgTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (code == enums[i].code) {
                return enums[i];
            }
        }
        return MsgTypeEnum.TEXT;
    }

}
