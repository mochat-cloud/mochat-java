package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @Description 客户
 * @Author zhaojinjian
 * @Date 2020-11-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("mc_work_contact")
public class WorkContactEntity extends Model<WorkContactEntity> {

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

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;

    @TableLogic
    private Date deletedAt;
}
