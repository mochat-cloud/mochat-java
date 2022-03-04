package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/5/19 3:43 下午
 * @description 企业应用实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("mc_work_agent")
public class WorkAgentEntity extends Model<WorkAgentEntity> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer corpId;
    private String wxAgentId;
    private String wxSecret;
    private String name;
    private String squareLogoUrl;
    private String description;
    private Integer close;
    private String redirectDomain;
    private Integer reportLocationFlag;
    private Integer isReportenter;
    private String homeUrl;
    private Integer type;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
}
