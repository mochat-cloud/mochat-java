package com.mochat.mochat.model.subsystem;

import lombok.Data;

/**
 * @description: 获取子账户列表的条件
 * @author: zhaojinjian
 * @create: 2020-11-25 16:47
 **/
@Data
public class GetSubSystemList {
    private String phone;
    private Integer status;
    private String userName;
}
