package com.mochat.mochat.model.workroom;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @description:微信客户群列表
 * @author: Huayu
 * @time: 2020/12/18 9:36
 */
@Data
public class WXWorkRoomModel {
    @JSONField(name="chat_id")
    private String chatId;//客户群ID
    private Integer status;//客户群状态
    private String  name;//群名
    private String  owner;//群主ID
    @JSONField(name="create_time")
    private Timestamp createTime;//群的创建时间
    private String    notice;//群的公告
    private List<WXWorkRoomInfoModel> WXWorkRoomInfoModel;
}
