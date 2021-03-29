package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;


/**
 * @description: 子账号实体
 * @author: zhaojinjian
 * @create: 2020-11-17 11:32
 **/
@Data
@ToString
@TableName("mc_user")
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends Model<UserEntity> {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String phone;
    private String password;
    private String name;
    private Integer gender;
    private String department;
    private String position;
    private Date loginTime;
    private Integer status;
    private Integer tenantId;
    @TableField(value = "isSuperAdmin")
    private Integer isSuperAdmin;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
}
