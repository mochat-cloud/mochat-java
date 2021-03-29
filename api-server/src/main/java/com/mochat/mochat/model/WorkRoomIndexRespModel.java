package com.mochat.mochat.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:客户群列表返回model
 * @author: Huayu
 * @time: 2020/12/10 19:24
 */
@Data
public class WorkRoomIndexRespModel implements Serializable {
    /**
     * 客户群ID
     */
    private Integer workRoomId;

    /**
     * 客户群成员数量
     */
    private Integer memberNum;

    /**
     * 客户群名称
     */
    private String roomName;

    /**
     * 群主姓名
     */
    private String ownerName;

    /**
     * 所属分组
     */
    private String roomGroup;

    /**
     * 状态枚举
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 今日入群数量
     */
    private Integer inRoomNum;

    /**
     * 今日退群数量
     */
    private Integer outRoomNum;

    /**
     * 群公告
     */
    private String notice;

    /**
     * 创建时间
     */
    private String createTime;
}
