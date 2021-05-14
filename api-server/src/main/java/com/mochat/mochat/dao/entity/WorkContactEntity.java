package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @Description 客户
 * @Author zhaojinjian
 * @Date 2020-11-26
 */
@TableName("mc_work_contact")
public class WorkContactEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 企业表ID（mc_crop.id）
     */
    private Integer corpId;

    /**
     * 外部联系人external_userid
     */
    private String wxExternalUserid;

    /**
     * 外部联系人姓名
     */
    private String name;

    /**
     * 外部联系人昵称
     */
    private String nickName;

    /**
     * 外部联系人的头像
     */
    private String avatar;

    /**
     * 跟进状态（1.未跟进 2.跟进中 3.已拒绝 4.已成交 5.已复购）
     */
    private Integer followUpStatus;

    /**
     * 外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
     */
    private Integer type;

    /**
     * 外部联系人性别 0-未知 1-男性 2-女性
     */
    private Integer gender;

    /**
     * 外部联系人在微信开放平台的唯一身份标识（微信unionid）
     */
    private String unionid;

    /**
     * 外部联系人的职位，如果外部企业或用户选择隐藏职位，则不返回，仅当联系人类型是企业微信用户时有此字段
     */
    private String position;

    /**
     * 外部联系人所在企业的简称，仅当联系人类型是企业微信用户时有此字段
     */
    private String corpName;

    /**
     * 外部联系人所在企业的主体名称
     */
    private String corpFullName;

    /**
     * 外部联系人的自定义展示信息
     */
    private String externalProfile;

    /**
     * 外部联系人编号
     */
    private String businessNo;

    private Date createdAt;
    private Date updatedAt;

    @TableLogic
    private Date deletedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getWxExternalUserid() {
        return wxExternalUserid;
    }

    public void setWxExternalUserid(String wxExternalUserid) {
        this.wxExternalUserid = wxExternalUserid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? "" : nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? "" : avatar;
    }

    public Integer getFollowUpStatus() {
        return followUpStatus == null ? 0 : followUpStatus;
    }

    public void setFollowUpStatus(Integer followUpStatus) {
        this.followUpStatus = followUpStatus;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid == null ? "" : unionid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position == null ? "" : position;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName == null ? "" : corpName;
    }

    public String getCorpFullName() {
        return corpFullName;
    }

    public void setCorpFullName(String corpFullName) {
        this.corpFullName = corpFullName == null ? "" : corpFullName;
    }

    public String getExternalProfile() {
        return externalProfile;
    }

    public void setExternalProfile(String externalProfile) {
        this.externalProfile = externalProfile == null ? "[]" : externalProfile;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo == null ? "" : businessNo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}
