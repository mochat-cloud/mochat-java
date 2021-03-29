package com.mochat.mochat.model.permission;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 10:13 上午
 * @description 角色管理 - 角色分页列表
 */
@Data
public class RolePageItemVO {

    /**
     * 角色 id
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色人员数量
     */
    private Integer employeeNum;

    /**
     * 角色描述
     */
    private String remarks;

    /**
     * 更新时间
     */
    private String updatedAt;

    /**
     * 启用状态 1-启用 -2禁用
     */
    private Integer status;
}
