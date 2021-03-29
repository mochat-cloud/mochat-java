package com.mochat.mochat.model.wm;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 2:19 下午
 * @description 会话内容存档配置 - 企业信息查看
 */
public class CorpShowBO {

    /**
     * 会话配置ID
     */
    private Integer id;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 后台企业ID
     */
    private Integer corpId;

    /**
     * 微信企业ID
     */
    private String wxCorpid;

    /**
     * 企业代码
     */
    private String socialCode;

    /**
     * 企业负责人
     */
    private String chatAdmin;

    /**
     * 企业负责人电话
     */
    private String chatAdminPhone;

    /**
     * 企业负责人身份证
     */
    private String chatAdminIdcard;

    /**
     * 状态 0未开通 大于1为已开通
     */
    private Integer chatApplyStatus;

    public CorpShowBO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getWxCorpid() {
        return wxCorpid;
    }

    public void setWxCorpid(String wxCorpid) {
        this.wxCorpid = wxCorpid;
    }

    public String getSocialCode() {
        return socialCode;
    }

    public void setSocialCode(String socialCode) {
        this.socialCode = socialCode;
    }

    public String getChatAdmin() {
        return chatAdmin;
    }

    public void setChatAdmin(String chatAdmin) {
        this.chatAdmin = chatAdmin;
    }

    public String getChatAdminPhone() {
        return chatAdminPhone;
    }

    public void setChatAdminPhone(String chatAdminPhone) {
        this.chatAdminPhone = chatAdminPhone;
    }

    public String getChatAdminIdcard() {
        return chatAdminIdcard;
    }

    public void setChatAdminIdcard(String chatAdminIdcard) {
        this.chatAdminIdcard = chatAdminIdcard;
    }

    public Integer getChatApplyStatus() {
        return chatApplyStatus;
    }

    public void setChatApplyStatus(Integer chatApplyStatus) {
        this.chatApplyStatus = chatApplyStatus;
    }
}
