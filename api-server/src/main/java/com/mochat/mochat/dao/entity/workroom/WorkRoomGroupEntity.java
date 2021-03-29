package com.mochat.mochat.dao.entity.workroom;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @description:客户群分组实体
 * @author: Huayu
 * @time: 2020/12/8 14:47
 */
@TableName("mc_work_room_group")
@Data
public class WorkRoomGroupEntity {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer corpId;//企业表ID（mc_corp.id）
    private String name;//分组名称
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
}
