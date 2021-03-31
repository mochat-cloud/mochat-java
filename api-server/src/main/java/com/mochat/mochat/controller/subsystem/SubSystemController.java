package com.mochat.mochat.controller.subsystem;

import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.ISubSystemService;
import com.mochat.mochat.model.subsystem.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @description: 子账号管理
 * @author: zhaojinjian
 * @create: 2020-11-17 11:29
 **/
@RestController
public class SubSystemController {

    @Autowired
    private ISubSystemService subSystem;

    /**
     * @Description: 子账户管理 - 列表
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/18
     */
    @GetMapping("/user/index")
    public ApiRespVO index(APIGetSubSystemRequest param, @RequestAttribute ReqPerEnum permission) {
        GetSubSystemPage subSystemPage = subSystem.getSubSystemPageList(param, permission);
        return ApiRespUtils.getApiRespOfOk(subSystemPage);

    }

    /**
     * @Description: 创建子账号
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/20
     */
    @PostMapping("/user/store")
    public ApiRespVO saveSubSystem(@RequestBody AddSubSystemRequest parem) {
        subSystem.saveSubsystem(parem);
        return ApiRespUtils.getApiRespOfOk(new ArrayList<String>());
    }

    /**
     * @Description: 更新员工账户登录密码
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @PostMapping("/user/passwordUpdate")
    public ApiRespVO passwordUpdate(@RequestBody PasswordUpdateRequest parem) {
        parem.verifyParam();
        Integer userId = AccountService.getUserId();
        subSystem.updatePassword(parem, userId);
        return ApiRespUtils.getApiRespOfOk(new ArrayList<String>());
    }

    /**
     * @Description: 子账户信息更新
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @PutMapping("/user/update")
    public ApiRespVO update(@RequestBody UpdateSubSystemRequest parem) {
        boolean result = subSystem.update(parem);
        return ApiRespUtils.getApiRespOfOk(new ArrayList<String>());
    }

    /**
     * @Description: 获取登录用户信息详情
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @GetMapping("/user/loginShow")
    public ApiRespVO loginShow() {
        Map<String, Integer> corpIdAndEmpIdMap = AccountService.getCorpIdAndEmpIdMap();
        Integer userId = corpIdAndEmpIdMap.get("userId");
        Integer empId = corpIdAndEmpIdMap.get("empId");
        //要判断empId在缓存里存不存在
        LoginShowRresponse result = subSystem.getLoginShowInfo(userId, empId);
        return ApiRespUtils.getApiRespOfOk(result);
    }

    /**
     * @Description:设置子账户状态禁用或启用
     * @Param: userId：子账户ID(多个用英文半角逗号连接)，status：账户状态(1-启用2-禁用)
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @PutMapping("/user/statusUpdate")
    public ApiRespVO setStatus(@RequestBody Map<String,Object> mapData) {
        Integer status = Integer.valueOf(mapData.get("status").toString());
        String userId = mapData.get("userId").toString();
        if (!userId.isEmpty()) {
            String[] userIds = userId.split(",");
            boolean result = subSystem.setStatus(userIds, status);
        }
        return ApiRespUtils.getApiRespOfOk(new ArrayList<String>());
    }

    /**
     * @Description: 获取子账户详情
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @GetMapping("/user/show")
    public ApiRespVO getUserInfo(int userId) {
        GetSubSystemInfoResponse result = subSystem.getSubSystemInfo(userId);
        return ApiRespUtils.getApiRespOfOk(result);
    }
}
