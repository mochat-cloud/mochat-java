package com.mochat.mochat.model.permission;

import lombok.Data;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 10:13 上午
 * @description 菜单管理 - 菜单列表 VO
 */
@Data
public class MenuPageItemVO {

    /**
     * 菜单序号
     */
    private String menuPath;

    /**
     * 菜单 id
     */
    private Integer menuId;

    /**
     * 父级菜单 id
     */
    private Integer parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 所填菜单级别 1-一级 2-二级 3-三级 4-四级 5-四级的操作
     */
    private Integer level;

    /**
     * 菜单等级名称
     */
    private String levelName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 菜单状态
     */
    private Integer status;

    /**
     * 最后操作人姓名
     */
    private String operateName;

    /**
     * 最后操作时间
     */
    private String updatedAt;

    /**
     * 子菜单
     */
    private List<MenuPageItemVO> children;

}
