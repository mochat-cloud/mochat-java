package com.mochat.mochat.model.workcontact;

import lombok.Data;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/12 3:49 下午
 * @description 客户标签分组管理 - 分组列表 VO
 */
@Data
public class ContactTagGroupIndexVO {

    /**
     * 分组 id
     */
    private int groupId;

    /**
     * 分组名称
     */
    private String groupName = "";
}
