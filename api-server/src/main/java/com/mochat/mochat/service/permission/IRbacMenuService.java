package com.mochat.mochat.service.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.dao.entity.permission.McRbacMenuEntity;
import com.mochat.mochat.model.permission.*;

import java.util.List;

public interface IRbacMenuService extends IService<McRbacMenuEntity> {

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 10:19 上午
     * @description 修改菜单
     *
     * @param req
     */
    void updateMenu(MenuUpdateDTO req);

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 11:09 上午
     * @description 添加菜单
     *
     * @param req
     */
    void storeMenu(MenuStoreDTO req);

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 2:14 下午
     * @description 菜单列表
     */
    Page<MenuPageItemVO> menuListPage(String name, RequestPage page);

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 3:55 下午
     * @description 菜单详情
     */
    MenuShowVO showMenu(Integer menuId);

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 5:34 下午
     * @description 更新菜单状态
     *
     * @param menuId 菜单 id
     * @param status 菜单状态
     */
    void updateMenuStatus(Integer menuId, Integer status);

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 5:42 下午
     * @description 菜单下已使用图标列表
     */
    List<String> menuIconList();

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 5:52 下午
     * @description 菜单下拉列表 树形结构
     */
    List<MenuItemVO> menuList();

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 11:10 上午
     * @description 用户权限列表
     */
    List<UserMenuItemVO> menuListByUserId();

    List<RoleMenuShowVO> showRoleMenu(Integer roleId);
}
