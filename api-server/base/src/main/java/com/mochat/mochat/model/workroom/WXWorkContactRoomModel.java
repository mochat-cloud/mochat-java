package com.mochat.mochat.model.workroom;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description:微信客户群头表
 * @author: Huayu
 * @time: 2020/12/19 9:07
 */
@Data
public class WXWorkContactRoomModel {
    private Integer id;
    private String chatId;
    private Integer ownerId;
    private Map<String,Object> WXWorkContactRoomInfoModelListMap;
}
