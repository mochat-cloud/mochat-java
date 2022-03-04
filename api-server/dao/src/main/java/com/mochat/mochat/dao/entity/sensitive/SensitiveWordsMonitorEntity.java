package com.mochat.mochat.dao.entity.sensitive;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @description:敏感词监控 - 列表
 * @author: Huayu
 * @time: 2021/2/4 10:42
 */
@Data
@TableName("mc_sensitive_word_monitor")
public class SensitiveWordsMonitorEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer corp_id;//企业id
    private Integer sensitiveWordId;//敏感词词库表id(mc_sensitive_word.id)
    private Integer sensitiveWordName;//敏感词词库表名称(mc_sensitive_word.name)
    private Integer source;//触发来源【1-客户2-员工】
    private Integer triggerId;//触发人id
    private String triggerName;//触发人名称
    private Date triggerTime;//触发时间
    private Integer receiverType;//接收者类型【1-成员2-外部联系人3-群聊】
    private Integer receiverId;//接收者ID
    private Integer receiverName;//接收者名称
    private Integer workMessageId;//触发消息ID【mc_work_message.id】
    private String chatContent;//会话内容
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;

}
