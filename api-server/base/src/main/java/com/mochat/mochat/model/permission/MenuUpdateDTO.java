package com.mochat.mochat.model.permission;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/11 10:13 上午
 * @description 菜单管理 - 修改菜单接口参数
 */
@Data
public class MenuUpdateDTO {
    
    /**
     * 菜单 id
     */
    @NotNull(message = "菜单 id 不能为空")
    private Integer menuId;
    
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
}
