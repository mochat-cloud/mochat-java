package com.mochat.mochat.model.emp;

import lombok.Data;

@Data
public class EmpEmployeeBO {

    /**
     * 成员id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 头像
     */
    private String thumbAvatar;
    
    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
     */
    private Integer status;

    /**
     * 外部联系人权限 1.是 2.否
     */
    private Integer contactAuth;
    
    /**
     * 性别（0表示未定义，1表示男性，2表示女性）
     */
    private String gender;
    
    /**
     * 聊天数
     */
    private Integer messageNums;
    
    /**
     * 发送消息数
     */
    private Integer sendMessageNums;
    
    /**
     * 已回复聊天占比
     */
    private Integer replyMessageRatio;
    
    /**
     * 新增客户数
     */
    private Integer addNums;
    
    /**
     * 发起申请数
     */
    private Integer applyNums;

    /**
     * 删除/拉黑客户数
     */
    private Integer invalidContact;

    /**
     * 平均首次回复时长
     */
    private Integer averageReply;

    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
     */
    private String statusName;
    
    /**
     * 外部联系人权限 1.是 2.否
     */
    private String contactAuthName;


}
