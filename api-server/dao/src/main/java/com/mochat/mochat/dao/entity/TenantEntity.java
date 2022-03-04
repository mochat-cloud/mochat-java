package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @description:租户表
 * @author: Huayu
 * @time: 2020/11/28 10:20
 */
@TableName("mc_tenant")
@Data
public class TenantEntity {
    private Integer id;
    private String name;
    private Integer status;
    private String logo;
    private String loginBackground;
    private String url;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
    private String copyright;

    /**
     * serverIps 数据库字段类型为 json
     */
    private String serverIps;
}
