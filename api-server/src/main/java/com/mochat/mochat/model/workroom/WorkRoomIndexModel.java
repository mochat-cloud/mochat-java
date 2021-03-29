package com.mochat.mochat.model.workroom;

import com.mochat.mochat.common.model.RequestPage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:客户群列表模型层
 * @author: Huayu
 * @time: 2020/12/10 15:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkRoomIndexModel extends RequestPage {
    private Integer roomGroupId;
    private String workRoomName;
    private String workRoomOwnerId;
    private Integer workRoomStatus;
    private String startTime;
    private String endTime;
}
