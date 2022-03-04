package com.mochat.mochat.model.permission;

import lombok.Data;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/11 10:13 上午
 * @description 菜单管理 - 菜单下拉列表树形结构 VO
 */
@Data
public class MenuItemVO {

    /**
     * 菜单 id
     */
    private Integer menuId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 所填菜单级别 1-一级 2-二级 3-三级 4-四级 5-四级的操作
     */
    private Integer level;

    /**
     * 数据权限 1-启用, 2-不启用（查看企业下数据）
     */
    private Integer dataPermission;

    /**
     * 父级 id
     */
    private Integer parentId;

    /**
     * 子菜单
     */
    private List<MenuItemVO> children;

}
