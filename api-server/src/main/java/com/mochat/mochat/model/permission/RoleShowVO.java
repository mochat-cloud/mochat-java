package com.mochat.mochat.model.permission;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 10:13 上午
 * @description 角色管理 - 角色添加
 */
@Data
public class RoleShowVO {

    /**
     * 角色id 复制权限添加角色时 【传角色id】否则 不传
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String remarks;

    /**
     * 部门数据权限 1-是（查看部门数据） 2-否（查看个人数据）
     */
    private Integer dataPermission;

}
