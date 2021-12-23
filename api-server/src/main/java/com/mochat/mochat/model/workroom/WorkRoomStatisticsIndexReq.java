package com.mochat.mochat.model.workroom;

import com.mochat.mochat.common.api.ReqPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @description:客户群管理-统计分页请求数据
 * @author: Huayu
 * @time: 2020/12/12 15:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkRoomStatisticsIndexReq extends ReqPageDto {
    @NotNull(message = "客户群ID")
    private Integer workRoomId;    //客户群ID
    @NotNull(message = "统计类型")
    private Integer type;    //统计类型(1-日期2-周3-月)
    private String startTime;//开始时间
    private String endTime;    //结束时间
}
