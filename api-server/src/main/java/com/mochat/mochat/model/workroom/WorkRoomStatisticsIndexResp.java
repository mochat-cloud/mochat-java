package com.mochat.mochat.model.workroom;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:客户群管理-统计分页请求数据
 * @author: Huayu
 * @time: 2020/12/12 16:18
 */
@Data
public class WorkRoomStatisticsIndexResp implements Serializable {
     private String  time;   // 时间坐标
     private String addNum; // 新增成员数量
     private String  outNum; // 退群成员数量
     private Integer total;  // 当前群成员数
     private Integer outTotal; // 累计退群成员数

}
