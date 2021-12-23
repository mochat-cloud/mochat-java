package com.mochat.mochat.model.contact;

import lombok.Data;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/2/19 4:51 下午
 * @description 客户详情接口 VO
 */
@Data
public class ContactTrackVO {
    private Integer id;
    private String content;
    private String createdAt;
}
