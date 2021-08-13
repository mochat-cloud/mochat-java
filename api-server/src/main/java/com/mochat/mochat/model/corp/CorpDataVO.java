package com.mochat.mochat.model.corp;

import lombok.Data;

@Data
public class CorpDataVO {
    private Integer id;
    private Integer corpId;//企业id
    private Integer addContactNum;//新增客户数
    private Integer addRoomNum;//新增社群数
    private Integer addIntoRoomNum;//新增入群数
    private Integer lossContactNum;//流失客户数
    private Integer quitRoomNum;//退群数
    private String date;//日期
}
