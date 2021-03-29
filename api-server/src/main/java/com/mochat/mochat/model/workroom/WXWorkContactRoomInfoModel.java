package com.mochat.mochat.model.workroom;

import lombok.Data;

/**
 * @description:微信客户明细表
 * @author: Huayu
 * @time: 2020/12/20 14:51
 */
@Data
public class WXWorkContactRoomInfoModel {
    private Integer id;
    private String wxUserId;
    private String roomId;
    private Integer status;
}
