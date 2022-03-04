package com.mochat.mochat.dao.vo;

import lombok.Data;

/**
 * 根据账号获取，账号下所有客户的群
 */
@Data
public class GetContactRoomVo {
    private Integer contactId;
    private String roomName;
}
