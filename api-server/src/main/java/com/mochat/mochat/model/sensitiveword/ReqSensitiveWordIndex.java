package com.mochat.mochat.model.sensitiveword;

import lombok.Data;

/**
 * @description:敏感词库列表
 * @author: Huayu
 * @time: 2021/1/27 11:44
 */
@Data
public class ReqSensitiveWordIndex {
    private String keyWords;//关键字
    private Integer groupId;//分组id
    private Integer page;//页码
    private Integer perPage;//每页条数
}
