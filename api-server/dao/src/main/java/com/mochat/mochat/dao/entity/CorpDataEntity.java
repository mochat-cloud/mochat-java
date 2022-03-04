package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @description:企业数据信息
 * @author: Huayu
 */
@TableName("mc_corp_day_data")
@Data
public class CorpDataEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer corpId;//企业id
    private Integer addContactNum;//新增客户数
    private Integer addRoomNum;//新增社群数
    private Integer addIntoRoomNum;//新增入群数
    private Integer lossContactNum;//流失客户数
    private Integer quitRoomNum;//退群数
    private Date date;//日期
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
}
