package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author: yangpengwei
 * @time: 2020/12/4 2:28 下午
 * @description 成员统计表
 */
@Data
@TableName("mc_work_employee_statistic")
public class WorkEmployeeStatisticEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer corpId;
    private Integer employeeId;
    private Integer newApplyCnt;
    private Integer newContactCnt;
    private Integer chatCnt;
    private Integer messageCnt;
    private Integer replyPercentage;
    private Integer avgReplyTime;
    private Integer negativeFeedbackCnt;
    private Date synTime;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;
}