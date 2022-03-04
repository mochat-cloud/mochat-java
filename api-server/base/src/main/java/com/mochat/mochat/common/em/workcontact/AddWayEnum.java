package com.mochat.mochat.common.em.workcontact;

/**
 * 客户来源枚举.
 */
public enum AddWayEnum {

    OTHER_CHANNELS(0, "其他渠道"),

    SCAN_QR_CODE(1, "扫描二维码"),

    SEARCH_MOBILE_PHONE(2, "搜索手机号"),

    BUSINESS_CARD_SHARING(3, "名片分享"),

    GROUP_CHAT(4, "群聊"),

    MOBILE_PHONE_ADDRESS_BOOK(5, "手机通讯录"),

    WE_CHAT_CONTACT(6, "微信联系人"),

    ADD_FRIEND_FROM_WE_CHAT(7, "来自微信的添加好友申请"),

    IN_MEMBER_SHARE(201, "内部成员共享"),

    ADMIN_ASSIGNMENT(202, "管理员/负责人分配"),

    CHANNEL_CODE(1001, "渠道活码"),

    AUTO_GROUP(1002, "自动拉群");


    private int code;
    private String msg;

    AddWayEnum(int code, String msg) {
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
     *
     * @param code
     * @return msg
     */
    public static String getByCode(int code) {
        AddWayEnum[] values = values();
        for (AddWayEnum e : values) {
            if (e.getCode() == code) {
                return e.getMsg();
            }
        }
        return "";
    }
}
