package com.mochat.mochat.controller.permission;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.model.permission.MenuStoreDTO;
import com.mochat.mochat.model.permission.MenuUpdateDTO;
import com.mochat.mochat.service.permission.IRbacMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/11 9:39 上午
 * @description 菜单管理 Controller
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IRbacMenuService menuService;

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 修改菜单
     */
    @PutMapping("/update")
    public ApiRespVO update(@RequestBody MenuUpdateDTO req) {
        menuService.updateMenu(req);
        return ApiRespUtils.ok();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 添加菜单
     */
    @PostMapping("/store")
    public ApiRespVO store(@RequestBody MenuStoreDTO req) {
        req.checkParam();
        menuService.storeMenu(req);
        return ApiRespUtils.ok();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 添加菜单
     */
    @DeleteMapping("/destroy")
    public ApiRespVO store(@RequestBody JSONObject req) {
        int menuId = req.getIntValue("menuId");
        if (menuId < 1) {
            throw new ParamException("menuId 不能为空");
        }
        menuService.removeById(menuId);
        return ApiRespUtils.ok();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 菜单列表
     */
    @GetMapping("/index")
    public ApiRespVO store(String name, ReqPageDto page) {
        return ApiRespUtils.okPage(menuService.menuListPage(name, page));
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 菜单详情
     */
    @GetMapping("/show")
    public ApiRespVO show(@NotNull(message = "菜单 id 不能为空") Integer menuId) {
        return ApiRespUtils.ok(menuService.showMenu(menuId));
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 菜单状态修改
     */
    @PutMapping("/statusUpdate")
    public ApiRespVO statusUpdate(@RequestBody JSONObject req) {
        int menuId = req.getIntValue("menuId");
        if (menuId < 1) {
            throw new ParamException("menuId 不能为空");
        }

        int status = req.getIntValue("status");
        if (status < 1 || status > 2) {
            throw new ParamException("status 超出范围");
        }
        menuService.updateMenuStatus(menuId, status);
        return ApiRespUtils.ok();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 菜单下已使用图标列表
     */
    @GetMapping("/iconIndex")
    public ApiRespVO iconIndex() {
        return ApiRespUtils.ok(menuService.menuIconList());
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:52 上午
     * @description 菜单下拉列表 树形结构
     */
    @GetMapping("/select")
    public ApiRespVO select() {
        return ApiRespUtils.ok(menuService.menuList());
    }

}
