package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description:业务日志
 * @author: Huayu
 * @time: 2021/1/28 11:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mc_business_log" )
public class BusinessLogEntity extends Model<BusinessLogEntity> {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 相应业务id
     */
    private Integer businessId;

    /**
     * 参数
     */
    private String  params;

    /**
     * 事件
     */
    private Integer event;

    /**
     * 操作人 id (mc_work_employee.id)
     */
    private Integer operationId;

    private Date createdAt;
    private Date updatedAt;
}
