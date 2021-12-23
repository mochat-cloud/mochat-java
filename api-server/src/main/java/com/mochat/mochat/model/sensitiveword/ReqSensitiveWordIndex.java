package com.mochat.mochat.model.sensitiveword;

import com.mochat.mochat.common.api.ReqPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:敏感词库列表
 * @author: Huayu
 * @time: 2021/1/27 11:44
 */
@Data
@EqualsAndHashCode
public class ReqSensitiveWordIndex extends ReqPageDto {
    private String keyWords;//关键字
    private Integer groupId;//分组id
}
