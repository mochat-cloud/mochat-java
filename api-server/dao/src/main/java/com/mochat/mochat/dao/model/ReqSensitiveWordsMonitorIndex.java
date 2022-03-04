package com.mochat.mochat.dao.model;

import lombok.Data;

/**
 * @description:敏感词监控列表
 * @author: Huayu
 * @time: 2021/2/4 9:55
 */
@Data
public class ReqSensitiveWordsMonitorIndex {
    private String employeeId;//可选成员通讯录ID(多个用英文半角逗号连接)

    private Integer workRoomId;//客户群ID

    private Integer intelligentGroupId;//分组ID

    private String sensitiveWordIds;

    private String triggerStart;//触发时间-开始

    private String triggerEnd;//触发时间-结束

    private Integer receiverId;//接收者ID

    private Integer receiverType;//接收者类型

    private Integer triggerId;//触发人id

    private Integer source;//触发来源【1-客户2-员工】

    private Integer corpId;//企业Id

    private Integer page;//页码

    private Integer perPage;//每页条数

}
