package com.mochat.mochat.controller.contact;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.job.sync.WorkContactTagSyncLogic;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.IWorkContactTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author: yangpengwei
 * @time: 2020/12/14 6:21 下午
 * @description 客户标签管理
 */
@Slf4j
@RestController
@RequestMapping("/workContactTag")
@Validated
public class WorkContactTagController {

    @Autowired
    private IWorkContactTagService workContactTagService;
    @Autowired
    private WorkContactTagSyncLogic contactTagServiceSyncLogic;

    /**
     * 获取标签列表
     */
    @GetMapping("/index")
    public ApiRespVO getTagList(Integer groupId, RequestPage requestPage) {
        return ApiRespUtils.getApiRespOfOk(workContactTagService.getTagList(groupId, requestPage));
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/destroy")
    public ApiRespVO deleteTag(@RequestBody JSONObject paramJson) {
        String tagIds = paramJson.getString("tagId");
        if (tagIds == null || tagIds.isEmpty()) {
            throw new ParamException("标签 id 无效");
        }
        workContactTagService.deleteTag(tagIds);
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     * 同步标签
     */
    @PutMapping("/synContactTag")
    public ApiRespVO synContactTag() {
        contactTagServiceSyncLogic.onSync(AccountService.getCorpId());
        return ApiRespUtils.getApiRespOfOk();
    }

    /**
     * 所有标签
     */
    @GetMapping("/allTag")
    public ApiRespVO getAllTag(Integer groupId) {
        return ApiRespUtils.getApiRespOfOk(workContactTagService.getAllTag(groupId));
    }

    /**
     * 创建标签
     */
    @PostMapping("/store")
    public ApiRespVO createTag(@RequestBody JSONObject paramJson) {
        Integer groupId = paramJson.getInteger("groupId");
        if (groupId == null || groupId <= 0) {
            throw new ParamException("标签组 id 无效");
        }
        String tagNames = paramJson.getString("tagName");
        if (tagNames == null || tagNames.isEmpty() || "[]".equals(tagNames)) {
            throw new ParamException("标签名不能为空");
        }
        workContactTagService.createTag(groupId, tagNames);
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     * 获取标签详情
     */
    @GetMapping("/detail")
    public ApiRespVO getTagDetail(@NotNull(message = "标签 id 不能为 null") Integer tagId) {
        return ApiRespUtils.getApiRespOfOk(workContactTagService.getTagDetail(tagId));
    }

    /**
     * 移动标签
     */
    @PutMapping("/move")
    public ApiRespVO moveTags(@RequestBody JSONObject paramJson) {
        Integer groupId = paramJson.getInteger("groupId");
        if (groupId == null || groupId <= 0) {
            throw new ParamException("标签组 id 无效");
        }
        String tagIds = paramJson.getString("tagId");
        if (tagIds == null || tagIds.isEmpty()) {
            throw new ParamException("标签 id 不能为空");
        }
        workContactTagService.moveTags(tagIds, groupId);
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     * 更新标签
     */
    @PutMapping("/update")
    public ApiRespVO updateTag(@RequestBody JSONObject paramJson) {
        Integer tagId = paramJson.getInteger("tagId");
        if (tagId == null || tagId <= 0) {
            throw new ParamException("标签 id 无效");
        }
        Integer groupId = paramJson.getInteger("groupId");
        if (groupId == null || groupId <= 0) {
            throw new ParamException("标签组 id 无效");
        }
        String tagName = paramJson.getString("tagName");
        if (tagName == null || tagName.isEmpty()) {
            throw new ParamException("标签名不能为空");
        }
        int isUpdate = paramJson.getIntValue("isUpdate");
        if (isUpdate > 1) {
            return ApiRespUtils.getApiRespOfOk("");
        }
        workContactTagService.updateTag(tagId, groupId, tagName, isUpdate);
        return ApiRespUtils.getApiRespOfOk("");
    }
}
