package com.mochat.mochat.model.workroom;

import lombok.Data;

/**
 * @description:客户群成员管理-列表
 * @author: Huayu
 * @time: 2020/12/16 10:05
 */
@Data
public class WorkContactRoomIndex {
    private String   workContactRoomId; // 客户群成员ID
    private String   name;    // 成员名称
    private String   avatar;  // 成员头像地址
    private Integer  isOwner; // 是否是群主(1-是2-否)
    private String   joinTime;   // 入群时间
    private String[] otherRooms; // 所在其它群
    private String   outRoomTime;// 退群时间
    private Integer  joinScene;  // 入群方式枚举
    private String   joinSceneText; // 入群方式文本

}
