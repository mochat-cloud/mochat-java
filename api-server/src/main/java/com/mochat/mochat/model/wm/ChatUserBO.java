package com.mochat.mochat.model.wm;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 2:19 下午
 * @description 会话对象信息
 *
 * 用于 运营-聊天记录-会话对象列表
 */
@Data
public class ChatUserBO {

    /**
     * 员工ID
     */
    private int workEmployeeId;
    
    /**
     * 类型 0内部员工 1外部客户 2群
     */
    private int toUsertype;
    
    /**
     * 聊天对象的id(员工ID/客户ID/群ID)
     */
    private int toUserId;
    
    /**
     * 对方名称
     */
    private String name;
    
    /**
     * 对方头像
     */
    private String avatar;
    
    /**
     * 消息
     */
    private String content;
    
    /**
     * 最近一条消息时间
     */
    private String msgDataTime;
}
