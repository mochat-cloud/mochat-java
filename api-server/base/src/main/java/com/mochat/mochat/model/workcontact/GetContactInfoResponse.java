package com.mochat.mochat.model.workcontact;

import com.mochat.mochat.common.em.workemployee.GenderEnum;
import com.mochat.mochat.model.workcontacttag.GetContactTapModel;

import java.util.List;

/**
 * @author zhaojinjian
 * @ClassName GetContactInfoResponse.java
 * @Description TODO
 * @createTime 2020/12/2 16:34
 */

public class GetContactInfoResponse {
    private String  name;
    private Integer  gender;
    private String  genderText;
    private String  remark;
    private List<GetContactTapModel> tag;

    private String  description;
    private String  businessNo;
    private String []  roomName;

    private String  lastContact;
    private String  contactTimes;
    private String []  employeeName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<GetContactTapModel> getTag() {
        return tag;
    }

    public void setTag(List<GetContactTapModel> tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String[] getRoomName() {
        return roomName;
    }

    public void setRoomName(String[] roomName) {
        this.roomName = roomName;
    }

    public String getLastContact() {
        return lastContact;
    }

    public void setLastContact(String lastContact) {
        this.lastContact = lastContact;
    }

    public String getContactTimes() {
        return contactTimes;
    }

    public void setContactTimes(String contactTimes) {
        this.contactTimes = contactTimes;
    }

    public String[] getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String[] employeeName) {
        this.employeeName = employeeName;
    }
}
