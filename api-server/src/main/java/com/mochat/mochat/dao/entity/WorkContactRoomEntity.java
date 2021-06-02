package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @author: yangpengwei
 * @time: 2020/12/2 5:16 下午
 * @description mc_work_contact (客户 - 客户群 关联表)
 */
@Data
@TableName("mc_work_contact_room")
public class WorkContactRoomEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String wxUserId;
    private Integer contactId;
    private Integer employeeId;
    private String unionid;
    @TableField(exist = false)
    private String roomCase;
    private Integer roomId;
    private Integer joinScene;
    private Integer type;
    private Integer status;
    private Date joinTime;

    /**
     * 退群时间, 格式: 时间戳
     */
    private String outTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
}