package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/2 5:07 下午
 * @description mc_work_room (客户群表)
 */
@Data
@TableName("mc_work_room")
public class WorkRoomEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 企业表ID（mc_corp.id）
     */
    private Integer corpId;

    /**
     * 客户群ID
     */

    private String wxChatId;

    /**
     * 客户群名称
     */

    private String name;

    /**
     * 群主ID（work_employee.id）
     */

    private Integer ownerId;

    /**
     * 群公告
     */

    private String notice;

    /**
     * 客户群状态（0 - 正常 1 - 跟进人离职 2 - 离职继承中 3 - 离职继承完成）
     */

    private Integer status;

    private Date createTime;
    /**
     * 群成员上限
     */

    private Integer roomMax;

    /**
     * 分组id（work_room_group.id）
     */

    private Integer roomGroupId;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
	private Date deletedAt;

    @TableField(exist = false)
    private List<WorkContactRoomEntity> workContactRoom;
}
