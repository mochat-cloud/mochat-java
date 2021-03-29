package com.mochat.mochat.model.workcontact;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.config.ex.ParamException;
import lombok.EqualsAndHashCode;

/**
 * @description: 获取客户列表的条件
 * @author: zhaojinjian
 * @create: 2020-11-26 14:26
 **/

@EqualsAndHashCode(callSuper = false)
public class GetContactRequest extends RequestPage {
    /**
     * 关键词（可选）（客户姓名、昵称）
     */
    private String keyWords;
    /**
     * 备注
     */
    private String remark;
    /**
     * 用户画像属性id（可选）
     */
    private Integer fieldId;
    /**
     * 属性类型（有用户画像属性id时必填）
     */

    private String fieldType;
    /**
     * 用户画像筛选值值（有用户画像属性id时必填）
     */
    private String fieldValue;
    /**
     * 客户性别（可选）（0-未知 1-男性 2-女性 3-全部）
     */
    private Integer gender;
    /**
     * 客户来源（可选）
     */
    private String addWay;
    /**
     * 群聊id（可选）（逗号分隔的字符串 如1,2,3）
     */
    private String roomId;
    /**
     * 客户持群数（可选）（0-无群 1-一个 2-多个 3-全部）
     */
    private String groupNum;
    /**
     * 部门成员id（可选）（逗号分隔的字符串 如1,2,3）
     */
    private String employeeId;
    /**
     * 开始时间（可选）（如：2020-09-21 11:05:23）
     */
    private String startTime;
    /**
     * 结束时间（可选）（如：2020-09-21 11:05:23）
     */
    private String endTime;
    /**
     * 客户编码（可选）
     */
    private String businessNo;

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getFieldId() {
        return fieldId == null ? 0 : fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;

    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender == null ? 3 : gender;
    }

    public String getAddWay() {
        return addWay;
    }

    public void setAddWay(String addWay) {
        this.addWay = addWay;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum == null ? "3" : groupNum.trim();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public void verifyParam() {
        if (this.fieldId != null && !this.fieldId.equals("0") && this.fieldType.isEmpty()) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
        if (this.fieldType != null && !this.fieldType.equals("0") && this.fieldValue.isEmpty()) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
        if (this.addWay.equals("全部")) {
            this.addWay = "";
        }
    }
}
