package com.mochat.mochat.dao.entity.wm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mc_work_message_index")
public class WorkMsgIndexEntity {

    /**
     * 企业 id
     */
    @TableId(type = IdType.AUTO)
    private Integer corpId;

    /**
     * 接收方 id
     */
    private Integer toId;

    /**
     * 接收方类型
     */
    private Integer toType;

    /**
     * 发送方 id
     */
    private Integer fromId;

    /**
     * flag
     */
    private String flag;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
}