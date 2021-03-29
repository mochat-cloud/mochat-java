package com.mochat.mochat.model.workcontact;

import com.mochat.mochat.common.em.workemployee.GenderEnum;

import java.util.List;

/**
 * @description:
 * @author: zhaojinjian
 * @create: 2020-11-27 14:57
 **/

public class ContactData {
    /**
     * 唯一标识
     */
    private Integer id;
    /**
     * 客户id
     */
    private Integer contactId;
    /**
     * 所属成员id
     */
    private Integer employeeId;
    /**
     * 是否是当前登录人的客户（1-是 2-否）
     */
    private Integer isContact;
    /**
     * 头像链接
     */
    private String avatar;
    /**
     * 性别（0-未知 1-男性 2-女性）
     */
    private Integer gender;
    /**
     * 性别
     */
    private String genderText;
    /**
     * 名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 客户编号
     */
    private String businessNo;
    /**
     * 所在群
     */
    private List<String> roomName;
    /**
     * 来源标识
     */
    private Integer addWay;
    /**
     * 来源
     */
    private String addWayText;
    /**
     * 标签
     */
    private List<String> tag;
    /**
     * 归属成员
     */
    private String employeeName;
    /**
     * 添加时间
     */
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getIsContact() {
        return isContact;
    }

    public void setIsContact(Integer isContact) {
        this.isContact = isContact;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getGenderText() {
        return genderText;
    }

    public void setGenderText(String genderText) {
        this.genderText = GenderEnum.getMsgByCode(this.gender);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public List<String> getRoomName() {
        return roomName;
    }

    public void setRoomName(List<String> roomName) {
        this.roomName = roomName;
    }

    public Integer getAddWay() {
        return addWay;
    }

    public void setAddWay(Integer addWay) {
        this.addWay = addWay;
    }

    public String getAddWayText() {
        return addWayText;
    }

    public void setAddWayText(String addWayText) {
        this.addWayText = addWayText;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
