package com.mochat.mochat.model.workroom;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @description:客户群列表
 * @author: Huayu
 * @time: 2020/12/18 15:11
 */
@Data
public class WXWorkRoomIdsModel {
    @JSONField(name="chat_id")
    private String chatId;
    private Integer status;
}
