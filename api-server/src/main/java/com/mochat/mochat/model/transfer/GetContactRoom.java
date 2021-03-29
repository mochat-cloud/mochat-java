package com.mochat.mochat.model.transfer;

import lombok.Data;

/**
 * 根据账号获取，账号下所有客户的群
 */
@Data
public class GetContactRoom {
    private Integer contactId;
    private String roomName;
}
