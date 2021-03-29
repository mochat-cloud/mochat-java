package com.mochat.mochat.model.contact;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2021/2/19 4:51 下午
 * @description 客户详情接口 VO
 */
@Data
public class ContactDetailVO {
    private Integer id;
    private String name;
    private String avatar;
    private Integer corpId;
}
