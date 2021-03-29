package com.mochat.mochat.model.workroom;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @description:客户群成员信息
 * @author: Huayu
 * @time: 2020/12/18 11:46
 */
@Data
public class WXWorkRoomInfoModel {
    private String userid;//群成员id
    private Integer type; //成员类型 1- 企业成员  2- 外部联系人
    private String unionid;
    private Timestamp join_time;//入群时间
    private Integer   join_scene;//入群方式 1 - 由成员邀请入群（直接邀请入群） 2 - 由成员邀请入群（通过邀请链接入群）3 - 通过扫描群二维码入群
}
