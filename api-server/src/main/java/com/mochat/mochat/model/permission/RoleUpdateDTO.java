package com.mochat.mochat.model.permission;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoleUpdateDTO {

    @NotNull(message = "角色 id 不能为空")
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
