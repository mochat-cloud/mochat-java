package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mc_work_employee_department")
public class WorkEmployeeDepartmentEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer employeeId;
    private Integer departmentId;
    private Integer isLeaderInDept;
    @TableField("`order`")
    private Integer order;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;

}
