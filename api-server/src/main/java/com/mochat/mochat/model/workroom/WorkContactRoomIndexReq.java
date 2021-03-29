package com.mochat.mochat.model.workroom;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:客户群成员管理-列表
 * @author: Huayu
 * @time: 2020/12/16 8:51
 */
@Data
public class WorkContactRoomIndexReq {

    /**
     * 客户群ID
     */
    @NotNull(message = "workRoomId不能为空")
    private Integer workRoomId;

    /**
     * 成员状态(1-正常2-退群) 允许值: 1, 2
     */
    private Integer status;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 页码   默认值: 1
     */
    private Integer page;

    /**
     * 每页条数 默认值: 10
     */
    private Integer perPage;

}
