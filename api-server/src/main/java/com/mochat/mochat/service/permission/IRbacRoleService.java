package com.mochat.mochat.service.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.dao.entity.permission.McRbacRoleEntity;
import com.mochat.mochat.model.permission.*;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/11 6:06 下午
 * @description 角色管理服务类
 */
public interface IRbacRoleService extends IService<McRbacRoleEntity> {

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 6:07 下午
     * @description 角色下拉列表
     */
    List<RoleItemVO> roleList();

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 6:24 下午
     * @description 角色修改
     */
    void updateRole(RoleUpdateDTO req);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/12 2:27 下午
     * @description 角色创建
     */
    void storeRole(RoleStoreDTO req);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 5:34 下午
     * @description 更新菜单状态
     *
     * @param roleId 角色 id
     * @param status 角色启用状态
     */
    void updateRoleStatus(Integer roleId, Integer status);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/12 3:32 下午
     * @description 角色详情
     */
    RoleShowVO showRole(Integer roleId);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/12 9:40 上午
     * @description 角色列表
     */
    Page<RolePageItemVO> roleListPage(String name, ReqPageDto page);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/12 10:56 上午
     * @description 角色权限保存
     */
    void updateRoleMenu(Integer roleId, List<Integer> menuIds);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/12 5:07 下午
     * @description 角色查看人员
     */
    Page<RoleEmpShowVO> showRoleEmp(Integer roleId, ReqPageDto page);
}
