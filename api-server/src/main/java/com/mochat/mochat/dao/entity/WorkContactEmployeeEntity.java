package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;

/**
 * @Description 通讯录和客户中间表
 * @Author idea
 * @Date 2020-11-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("mc_work_contact_employee")
public class WorkContactEmployeeEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 通讯录表ID（work_employee.id）
     */
    private Integer employeeId;

    /**
     * 客户表ID（work_contact.id）
     */
    private Integer contactId;

    /**
     * 员工对此外部联系人的备注
     */
    private String remark;

    /**
     * 员工对此外部联系人的描述
     */
    private String description;

    /**
     * 员工对此客户备注的企业名称
     */
    private String remarkCorpName;

    /**
     * 员工对此客户备注的手机号码
     */
    private String remarkMobiles;

    /**
     * 表示添加客户的来源
     * 0
     * 未知来源
     * 1
     * 扫描二维码
     * 2
     * 搜索手机号
     * 3
     * 名片分享
     * 4
     * 群聊
     * 5
     * 手机通讯录
     * 6
     * 微信联系人
     * 7
     * 来自微信的添加好友申请
     * 8
     * 安装第三方应用时自动添加的客服人员
     * 9
     * 搜索邮箱
     * 201
     * 内部成员共享
     * 202
     * 管理员/负责人分配
     */
    private Integer addWay;

    /**
     * 发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid
     */
    private String operUserid;

    /**
     * 企业自定义的state参数，用于区分客户具体是通过哪个「联系我」添加，由企业通过创建「联系我」方式指定
     */
    private String state;

    /**
     * 企业表ID（corp.id）
     */
    private Integer corpId;

    /**
     * 1.正常 2.删除 3.拉黑
     */
    private Integer status;

    /**
     * 员工添加此外部联系人的时间
     */
    private Date createTime;

    private Date createdAt;

    private Date updatedAt;

    @TableLogic
    private Date deletedAt;

    /**
     * 客户信息
     */
    @TableField(exist = false)
    private WorkContactEntity workContact;

    public Integer getStatus() {
        return status == null ? 1 : status;
    }
}
