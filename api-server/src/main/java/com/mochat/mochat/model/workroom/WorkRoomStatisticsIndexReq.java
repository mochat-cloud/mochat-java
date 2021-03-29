package com.mochat.mochat.model.workroom;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:客户群管理-统计分页请求数据
 * @author: Huayu
 * @time: 2020/12/12 15:24
 */
@Data
public class WorkRoomStatisticsIndexReq implements Serializable {
    @NotNull(message = "客户群ID")
    private Integer workRoomId;	//客户群ID
    @NotNull(message = "统计类型")
    private Integer type;	//统计类型(1-日期2-周3-月)
    private String startTime;//开始时间
    private String endTime;	//结束时间
    private Integer page;//页码 默认值：1
    private Integer perPage;//每页条数默认值: 10





}
