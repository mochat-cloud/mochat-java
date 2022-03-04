package com.mochat.mochat.model.sensitiveword;

import lombok.Data;

/**
 * @description:敏感词库列表
 * @author: Huayu
 * @time: 2021/1/27 18:02
 */
@Data
public class RespSensitiveWordIndex {
    private Integer sensitiveWordId;// 敏感词id
    private String  name;// 敏感词名称
    private String  employeeNum;//员工触发次数
    private String  contactNum;//客户触发次数
    private String  createdAt;//创建时间
    private String  status;//状态 1-开启,2-关闭
}
