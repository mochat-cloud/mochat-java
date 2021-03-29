package com.mochat.mochat.model.workcontact;

import com.mochat.mochat.controller.contact.WorkContactTagGroupController;
import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2020/12/12 3:49 下午
 * @description 客户标签分组管理 - 分组列表 VO
 *
 * @see WorkContactTagGroupController
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
