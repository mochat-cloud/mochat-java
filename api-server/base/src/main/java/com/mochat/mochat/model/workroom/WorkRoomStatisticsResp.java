package com.mochat.mochat.model.workroom;

import lombok.Data;

import java.util.List;

/**
 * @description:客户群管理-统计折线图
 * @author: Huayu
 * @time: 2020/12/14 18:01
 */
@Data
public class WorkRoomStatisticsResp {
    private Integer  addNum;
    private Integer  outNum;
    private Integer  total;
    private Integer  outTotal;
    private Integer  addNumRange;
    private Integer  outNumRange;
    private List<WorkRoomStatisticsIndexResp> list;
}
