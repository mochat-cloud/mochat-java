package com.mochat.mochat.controller.contact;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.service.contact.IWorkContactTagGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/12 3:19 下午
 * @description 客户标签分组管理
 */
@Slf4j
@RestController
@RequestMapping("/workContactTagGroup")
@Validated
public class WorkContactTagGroupController {

    @Autowired
    IWorkContactTagGroupService workContactTagGroupService;

    /**
     * 分组列表
     */
    @GetMapping("/index")
    public ApiRespVO getIndex(ReqPageDto req) {
        return ApiRespUtils.ok(workContactTagGroupService.getGroupList(req));
    }

    /**
     * 分组详情
     */
    @GetMapping("/detail")
    public ApiRespVO getDetail(@NotNull(message = "标签组 id 不能为 null") Integer groupId) {
        return ApiRespUtils.ok(workContactTagGroupService.getGroupDetail(groupId));
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/destroy")
    public ApiRespVO deleteGroup(@RequestBody JSONObject paramJson) {
        Integer groupId = paramJson.getInteger("groupId");
        if (groupId == null || groupId <= 0) {
            throw new ParamException("标签组 Id 无效");
        }
        workContactTagGroupService.deleteGroup(groupId);
        return ApiRespUtils.ok("");
    }

    /**
     * 新增分组
     */
    @PostMapping("/store")
    public ApiRespVO createGroup(@RequestBody JSONObject paramJson) {
        String groupName = paramJson.getString("groupName");
        if (groupName == null || groupName.isEmpty()) {
            throw new ParamException("标签组名称不能为空");
        }
        workContactTagGroupService.createGroup(groupName);
        return ApiRespUtils.ok("");
    }

    /**
     * 编辑分组
     */
    @PutMapping("/update")
    public ApiRespVO updateGroup(@RequestBody JSONObject paramJson) {

        int isUpdate = paramJson.getIntValue("isUpdate");
        if (isUpdate > 1) {
            return ApiRespUtils.ok("");
        }

        Integer groupId = paramJson.getInteger("groupId");
        if (groupId == null || groupId <= 0) {
            throw new ParamException("标签组 Id 无效");
        }

        String groupName = paramJson.getString("groupName");
        if (groupName == null || groupName.isEmpty()) {
            throw new ParamException("标签组名称不能为空");
        }

        workContactTagGroupService.updateGroup(groupId, groupName, isUpdate);
        return ApiRespUtils.ok("");
    }

}
