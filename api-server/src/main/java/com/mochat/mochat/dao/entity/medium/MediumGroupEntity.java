package com.mochat.mochat.dao.entity.medium;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @description:媒体库分组
 * @author: Huayu
 * @time: 2020/12/4 18:32
 */
@TableName("mc_medium_group")
@Data
public class MediumGroupEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;//id
    private Integer corp_id;//企业表ID
    private String name;//名称
    @TableField("`order`")
    private Integer order;//排序
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
}
