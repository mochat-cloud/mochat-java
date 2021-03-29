package com.mochat.mochat.model.permission;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 10:13 上午
 * @description 角色管理 - 角色添加
 */
@Data
public class RoleStoreDTO {

    /**
     * 角色id 复制权限添加角色时 【传角色id】否则 不传
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 角色描述
     */
    @NotBlank(message = "角色描述不能为空")
    private String remarks;

    /**
     * 部门数据权限 1-是（查看部门数据） 2-否（查看个人数据）
     */
    @Range(min = 1, max = 2, message = "菜单数据权限参数无效")
    private Integer dataPermission;

}
