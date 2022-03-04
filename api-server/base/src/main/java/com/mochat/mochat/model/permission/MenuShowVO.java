package com.mochat.mochat.model.permission;

import lombok.Data;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/11 10:13 上午
 * @description 菜单管理 - 菜单列表 VO
 */
@Data
public class MenuShowVO {

    /**
     * 菜单 id
     */
    private Integer menuId;

    /**
     * 一级菜单 id
     */
    private String firstMenuId;

    /**
     * 二级菜单 id
     */
    private String secondMenuId;

    /**
     * 三级菜单 id
     */
    private String thirdMenuId;

    /**
     * 四级菜单 id
     */
    private String fourthMenuId;

    /**
     * 所填菜单级别 1-一级 2-二级 3-三级 4-四级 5-四级的操作
     */
    private Integer level;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否为页面菜单 1-是 2-否
     */
    private Integer isPageMenu;

    /**
     * 地址
     */
    private String linkUrl;

    /**
     * 地址 链接类型【1-内部链接(默认)2-外部链接】
     */
    private Integer linkType;

    /**
     * 数据权限 1-启用, 2-不启用（查看企业下数据）
     */
    private Integer dataPermission;

    /**
     * 菜单状态
     */
    private String status;
}
