package com.mochat.mochat.model.permission;

import lombok.Data;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 10:13 上午
 * @description 角色管理 - 角色添加
 */
@Data
public class RoleMenuShowVO {

    /**
     * 菜单 id
     */
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单级别 1-一级, 2-二级,3-三级,4-四级,5-五级
     */
    private Integer level;

    /**
     * 是否选中 1-未选中, 2-选中, 3-半选
     */
    private Integer checked;

    /**
     * 是否为前端菜单
     */
    private Integer isPageMenu;

    /**
     * 子级菜单
     */
    private List<RoleMenuShowVO> children;

}
