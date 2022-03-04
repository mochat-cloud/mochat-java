package com.mochat.mochat.dao.entity.wm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mc_work_message_1")
public class WorkMsgEntity {

    /**
     * 企业 id
     */
    @TableId(type = IdType.AUTO)
    private Integer corpId;

    /**
     * id
     */
    private Integer id;

    /**
     * seq 查询用
     */
    private Integer seq;

    /**
     * msgid
     */
    private String msgId;

    /**
     * 消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)
     * 注: 切换企业日志已被过滤, 不存储
     */
    private Integer action;

    /**
     * 发送者微信 id
     */
    private String from;

    /**
     * 接收者微信 id 数组
     */
    private String tolist;

    /**
     * 接收方类型 0通讯录 1外部联系人 2群
     */
    private Integer tolistType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 内容
     */
    private String content;

    /**
     * 时间
     */
    private String msgTime;

    /**
     * 微信群 id
     */
    private String wxRoomId;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
}