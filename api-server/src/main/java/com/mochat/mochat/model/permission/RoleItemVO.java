package com.mochat.mochat.model.permission;

import lombok.Data;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/11 10:13 上午
 * @description 角色管理 - 角色下拉列表
 */
@Data
public class RoleItemVO {

    /**
     * 菜单 id
     */
    private Integer roleId;

    /**
     * 菜单名称
     */
    private String name;
}
