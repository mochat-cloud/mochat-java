package com.mochat.mochat.controller.permission;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.permission.RoleStoreDTO;
import com.mochat.mochat.model.permission.RoleUpdateDTO;
import com.mochat.mochat.service.permission.IRbacMenuService;
import com.mochat.mochat.service.permission.IRbacRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 9:39 上午
 * @description 角色管理 Controller
 */
@RestController
@RequestMapping("/role")
@Validated
public class RoleController {

    @Autowired
    private IRbacRoleService roleService;

    @Autowired
    private IRbacMenuService menuService;

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:32 上午
     * @description 角色下拉列表
     */
    @GetMapping("/select")
    public ApiRespVO select() {
        return ApiRespUtils.getApiRespOfOk(roleService.roleList());
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:35 上午
     * @description 角色修改
     */
    @PutMapping("/update")
    public ApiRespVO update(@RequestBody RoleUpdateDTO req) {
        roleService.updateRole(req);
        return ApiRespUtils.getApiRespOfOk();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:32 上午
     * @description 角色下拉列表
     */
    @GetMapping("/index")
    public ApiRespVO index(String name, RequestPage page) {
        return ApiRespUtils.getApiRespByPage(roleService.roleListPage(name, page));
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:36 上午
     * @description 角色删除
     */
    @DeleteMapping("/destroy")
    public ApiRespVO destroy(@RequestBody JSONObject req) {
        int roleId = req.getIntValue("roleId");
        if (roleId < 1) {
            throw new ParamException("roleId 不能为空");
        }
        roleService.removeById(roleId);
        return ApiRespUtils.getApiRespOfOk();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 11:28 上午
     * @description 角色添加
     */
    @PostMapping("/store")
    public ApiRespVO store(@RequestBody RoleStoreDTO req) {
        roleService.storeRole(req);
        return ApiRespUtils.getApiRespOfOk();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 11:28 上午
     * @description 角色状态修改
     */
    @PutMapping("/statusUpdate")
    public ApiRespVO statusUpdate(@RequestBody JSONObject req) {
        int roleId = req.getIntValue("roleId");
        if (roleId < 1) {
            throw new ParamException("roleId 不能为空");
        }

        int status = req.getIntValue("status");
        if (status < 1 || status > 2) {
            throw new ParamException("status 超出范围");
        }

        roleService.updateRoleStatus(roleId, status);
        return ApiRespUtils.getApiRespOfOk();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 11:28 上午
     * @description 角色详情
     */
    @GetMapping("/show")
    public ApiRespVO show(@NotNull(message = "角色 id 不能为空") Integer roleId) {
        return ApiRespUtils.getApiRespOfOk(roleService.showRole(roleId));
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:38 上午
     * @description 角色权限保存
     */
    @PostMapping("/permissionStore")
    public ApiRespVO permissionStore(@RequestBody JSONObject req) {

        int roleId = req.getIntValue("roleId");
        if (roleId < 1) {
            throw new ParamException("roleId 不能为空");
        }

        if (!req.containsKey("menuIds")) {
            throw new ParamException("menuIds 不能为空");
        }

        List<Integer> menuIds = req.getJSONArray("menuIds").toJavaList(Integer.class);

        roleService.updateRoleMenu(roleId, menuIds);
        return ApiRespUtils.getApiRespOfOk();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:59 上午
     * @description 角色权限列表
     */
    @GetMapping("/permissionByUser")
    public ApiRespVO permissionByUser() {
        return ApiRespUtils.getApiRespOfOk(menuService.menuListByUserId());
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:59 上午
     * @description 角色权限列表
     */
    @GetMapping("/permissionShow")
    public ApiRespVO permissionShow(@NotNull(message = "角色 id 不能为空") Integer roleId) {
        return ApiRespUtils.getApiRespOfOk(menuService.showRoleMenu(roleId));
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:59 上午
     * @description 角色查看人员
     */
    @GetMapping("/showEmployee")
    public ApiRespVO showEmployee(Integer roleId, RequestPage page) {
        return ApiRespUtils.getApiRespByPage(roleService.showRoleEmp(roleId, page));
    }
}
