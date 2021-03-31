package com.mochat.mochat.controller.channel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.channel.ChannelCodeGroupEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.channel.IChannelCodeGroupService;
import com.mochat.mochat.service.channel.IChannelCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author: yangpengwei
 * @time: 2021/2/22 2:47 下午
 * @description 渠道码 - 分组
 */
@RestController
@RequestMapping("/channelCodeGroup")
public class ChannelCodeGroupController {

    @Autowired
    private IChannelCodeGroupService channelCodeGroupService;

    @Autowired
    private IChannelCodeService channelCodeService;

    /**
     * @author: yangpengwei
     * @time: 2021/2/22 3:53 下午
     * @description 新建渠道码分组
     */
    @PostMapping("/store")
    public ApiRespVO storeCodeGroup(@RequestBody JSONObject reqJson) {
        if (Objects.isNull(reqJson) || Objects.isNull(reqJson.get("name"))) {
            throw new ParamException();
        }

        JSONArray jsonArray = reqJson.getJSONArray("name");
        List<String> nameList = jsonArray.toJavaList(String.class);

        channelCodeGroupService.saveByNames(nameList);
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     * @author: yangpengwei
     * @time: 2021/2/22 3:53 下午
     * @description 编辑渠道码分组
     */
    @PutMapping("/update")
    public ApiRespVO updateCodeGroup(@RequestBody JSONObject reqJson) {
        int groupId = reqJson.getIntValue("groupId");
        if (groupId <= 0) {
            throw new ParamException("groupId 不能为空");
        }

        String name = reqJson.getString("name");
        if (!StringUtils.hasLength(name)) {
            throw new ParamException("name 不能为空");
        }

        channelCodeGroupService.updateNameByGroupId(groupId, name);
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     * @author: yangpengwei
     * @time: 2021/2/22 3:53 下午
     * @description 编辑渠道码分组
     */
    @GetMapping("/index")
    public ApiRespVO showCodeGroupList() {
        List<ChannelCodeGroupEntity> codeGroupEntityList = channelCodeGroupService.getListByCorpId(AccountService.getCorpId());
        List<Map<String, Object>> list = new ArrayList<>();
        for (ChannelCodeGroupEntity e : codeGroupEntityList) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("groupId", e.getId());
            map.put("name", e.getName());
            list.add(map);
        }
        return ApiRespUtils.getApiRespOfOk(list);
    }

    /**
     * @author: yangpengwei
     * @time: 2021/2/22 3:53 下午
     * @description 编辑渠道码分组
     */
    @GetMapping("/detail")
    public ApiRespVO showCodeGroupDetail(@NotNull(message = "渠道码分组 id 不能为空") Integer groupId) {
        ChannelCodeGroupEntity entity = channelCodeGroupService.getById(groupId);
        if (Objects.isNull(entity)) {
            throw new ParamException("渠道码分组 id 不存在");
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("groupId", entity.getId());
        map.put("name", entity.getName());
        return ApiRespUtils.getApiRespOfOk(map);
    }

    /**
     * @author: yangpengwei
     * @time: 2021/2/23 9:39 上午
     * @description
     */
    @PutMapping("/move")
    public ApiRespVO showCodeGroupMove(@RequestBody JSONObject req) {
        int channelCodeId = req.getIntValue("channelCodeId");
        if (channelCodeId <= 0) {
            throw new ParamException("channelCodeId 不能为空");
        }

        int groupId = req.getIntValue("groupId");
        if (groupId <= 0) {
            throw new ParamException("groupId 不能为空");
        }

        channelCodeService.updateGroupId(channelCodeId,groupId);
        return ApiRespUtils.getApiRespOfOk();
    }

}
