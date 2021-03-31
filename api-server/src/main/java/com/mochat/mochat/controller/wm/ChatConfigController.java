package com.mochat.mochat.controller.wm;

import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.wm.ReqCorpStoreDTO;
import com.mochat.mochat.model.wm.ReqStepUpdateDTO;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.wm.IChatConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2020/12/2 11:05 上午
 * @description 会话内容存档配置
 */
@RestController()
@RequestMapping(path = "/workMessageConfig")
@Validated
public class ChatConfigController {

    @Autowired
    private IChatConfigService chatConfigService;

    @Autowired
    private ICorpService corpService;

    /**
     * @description: 列表
     * @author: Huayu
     * @time: 2020/11/23 19:26
     *
     * @param corpName 企业名称
     *
     * @info 因二期权限管理需求, 本人只能查看本公司的信息, 所属其他公司信息查看需要切换公司
     */
    @GetMapping(value = "corpIndex")
    public ApiRespVO getCorpList(String corpName, RequestPage requestPage, @RequestAttribute ReqPerEnum permission) {
        return ApiRespUtils.getApiRespByPage(corpService.getCorpPageList(corpName, requestPage, permission));
    }

    /**
     * 会话内容存档配置 - 企业信息查看
     */
    @GetMapping("/corpShow")
    public ApiRespVO corpShow(@NotNull(message = "corpId 不能为 null") Integer corpId) {
        return ApiRespUtils.getApiRespOfOk(chatConfigService.getCorpShowInfo(corpId));
    }

    /**
     * 会话内容存档配置 - 企业信息添加
     */
    @PostMapping("/corpStore")
    public ApiRespVO corpStore(@Validated @RequestBody ReqCorpStoreDTO req) {
        int id = chatConfigService.setCorpStore(req);
        Map<String, Integer> map = new HashMap<>(1);
        map.put("id", id);
        return ApiRespUtils.getApiRespOfOk(map);
    }

    /**
     * 会话内容存档配置 - 微信后台配置-步骤页面
     */
    @GetMapping("/stepCreate")
    public ApiRespVO stepCreate() {
        return ApiRespUtils.getApiRespOfOk(chatConfigService.getStepCreateInfo());
    }

    /**
     * 会话内容存档配置 - 微信后台配置-步骤动作
     */
    @PutMapping("/stepUpdate")
    public ApiRespVO stepUpdate(@Validated @RequestBody ReqStepUpdateDTO req) {
        chatConfigService.putStepUpdate(req);
        return ApiRespUtils.getApiRespOfOk("");
    }

}
