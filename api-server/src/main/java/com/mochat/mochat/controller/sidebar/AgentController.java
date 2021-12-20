package com.mochat.mochat.controller.sidebar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ServiceException;
import com.mochat.mochat.common.annotion.SkipVerityToken;
import com.mochat.mochat.common.util.JwtUtils;
import com.mochat.mochat.common.util.RSAUtils;
import com.mochat.mochat.common.util.RedisUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.UserEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.properties.ChatToolProperties;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.ISubSystemService;
import com.mochat.mochat.service.sidebar.IWorkAgentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2021/1/27 2:16 下午
 * @description 企业微信侧边栏应用域名校验
 */
@Slf4j
@RestController
public class AgentController {

    @Autowired
    private ChatToolProperties chatToolProperties;

    @Autowired
    private IWorkAgentService workAgentService;

    @Autowired
    private ICorpService corpService;

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private ISubSystemService subSystemService;

    /**
     * @author: yangpengwei
     * @time: 2021/5/19 3:55 下午
     * @description 企业微信应用域名校验文件验证
     */
    @GetMapping("/{filename:.+}")
    @ResponseBody
    @SkipVerityToken
    public ResponseEntity<String> getContentByVerifyFileName(@PathVariable String filename) {
        String key = filename.replaceAll("WW_verify_", "");
        key = key.replaceAll(".txt", "");
        return ResponseEntity.ok().body(key);
    }

    /**
     * @param agentId      应用 ID
     * @param isJsRedirect 是否跳转回本页面 0, 否; 1, 是
     * @param act          跳转回本页面时带的自定义参数，如客户标识，素材库标识
     */
    @GetMapping("/agent/oauth")
    @SkipVerityToken
    public ApiRespVO oauth(
            @NotNull(message = "应用 ID 不能为空") Integer agentId,
            @RequestParam(defaultValue = "0") String isJsRedirect,
            @RequestParam(defaultValue = "") String act
    ) {
        // 获取企业微信 id
        Integer corpId = workAgentService.getCorpIdById(agentId);
        if (null == corpId || corpId < 1) {
            throw new CommonException("应用 id 未关联企业");
        }

        String wxCorpId = corpService.getWxCorpIdById(corpId);
        if (null == wxCorpId) {
            throw new ServiceException("企业微信 id 获取失败, 未查到对应企业");
        }

        String redirectUrl = chatToolProperties.getApiUrl() + "/agent/oauth/callback"
                + "?agentId=" + agentId
                + "&isJsRedirect=" + isJsRedirect
                + "&act=" + act;

        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + wxCorpId +
                "&redirect_uri=" + redirectUrl +
                "&response_type=code" +
                "&scope=snsapi_base" +
                "&state=" +
                "#wechat_redirect";

        HashMap<String, String> map = new HashMap<>(1);
        map.put("url", url);
        return ApiRespUtils.ok(map);
    }

    /**
     * @param agentId      应用 ID
     * @param isJsRedirect 是否跳转回本页面 0, 否; 1, 是
     * @param act          跳转回本页面时带的自定义参数，如客户标识，素材库标识
     * @param code         企业微信回调时返回值
     */
    @GetMapping("/agent/oauth/callback")
    @SkipVerityToken
    public void oauth(
            @NotNull(message = "应用 ID 不能为空") Integer agentId,
            @RequestParam(defaultValue = "0") String isJsRedirect,
            @RequestParam(defaultValue = "") String act,
            @RequestParam(defaultValue = "") String code,
            HttpServletResponse response
    ) throws IOException {

        // 获取企业微信 id
        Integer corpId = workAgentService.getCorpIdById(agentId);
        if (null == corpId || corpId < 1) {
            throw new CommonException("应用 id 未关联企业");
        }

        // 根据 code 查询员工信息
        String wxUserId = WxApiUtils.requestWxUserIdApi(corpId, agentId, code);
        WorkEmployeeEntity workEmployeeEntity = employeeService.getByWxEmpId(wxUserId);

        // 根据员工信息登录
        UserEntity userEntityList = subSystemService.getById(workEmployeeEntity.getLogUserId());
        if (userEntityList == null) {
            throw new CommonException(100013, "登录失败,用户不存在");
        }
        String token = JwtUtils.createToken(36000000, userEntityList);
        RedisUtils.set("mc:user.token" + token, "1");
        AccountService.updateCorpIdAndEmployeeId(workEmployeeEntity.getLogUserId(), workEmployeeEntity.getCorpId(), workEmployeeEntity.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("corpId", corpId);
        jsonObject.put("token", token);
        jsonObject.put("expire", 36000000);
        jsonObject.put("agentId", agentId);
        jsonObject.put("isJsRedirect", isJsRedirect);
        jsonObject.put("act", act);
        ApiRespVO apiRespVO = new ApiRespVO(200, "登录成功", jsonObject);
        String dataJson = JSON.toJSONString(apiRespVO);
        String data = RSAUtils.base64EncodeToString(dataJson.getBytes());
        // 微信回调, 需要重定向到前端页面
        String redirectUrl = chatToolProperties.getWebUrl() + "/codeAuth?callValues=" + data;
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/wxJsSdk/config")
    @SkipVerityToken
    public ApiRespVO wxJSConfig(
            @NotNull(message = "企业 ID 无效") Integer corpId,
            @RequestParam(defaultValue = "") String uriPath,
            @RequestParam(defaultValue = "0") Integer agentId
    ) {
        String ticket = "";
        if (agentId > 0) {
            // 应用 ticket
            ticket = WxApiUtils.getJsapiTicketOfApp(corpId, agentId);
        } else {
            // 企业 ticket
            ticket = WxApiUtils.getJsapiTicketOfCorp(corpId);
        }

        String noncestr = "" + System.currentTimeMillis();
        String timestamp = "" + System.currentTimeMillis();

        String str1 = "jsapi_ticket=" + ticket +
                "&noncestr=" + noncestr +
                "&timestamp=" + timestamp +
                "&url=" + chatToolProperties.getWebUrl() + uriPath;
        String sign = DigestUtils.sha1Hex(str1);

        String wxCorpId = corpService.getById(corpId).getWxCorpId();

        if (agentId < 1) {
            agentId = workAgentService.getFirstAgentId(corpId);
        }
        String wxAgentId = workAgentService.getWxAgentById(agentId);

        HashMap<String, String> map = new HashMap<>(6);
        map.put("appId", wxCorpId);
        map.put("corpid", wxCorpId);
        map.put("agentid", wxAgentId);
        map.put("nonceStr", noncestr);
        map.put("timestamp", timestamp);
        map.put("signature", sign);
        return ApiRespUtils.ok(map);
    }

    @PostMapping("/agent/store")
    public ApiRespVO storeAgent(@RequestBody JSONObject req) {
        String wxAgentId = req.getString("wxAgentId");
        if (!StringUtils.hasLength(wxAgentId)) {
            throw new ParamException("wxAgentId 不能为空");
        }

        String wxSecret = req.getString("wxSecret");
        if (!StringUtils.hasLength(wxSecret)) {
            throw new ParamException("wxSecret 不能为空");
        }

        int type = req.getIntValue("type");

        workAgentService.storeAgent(wxAgentId, wxSecret, type);

        return ApiRespUtils.ok();
    }

    @GetMapping("/chatTool/config")
    public ApiRespVO getChatToolConfig() {
        List<Map<String, Object>> agents = workAgentService.getChatTools();
        if (agents.isEmpty()) {
            return ApiRespUtils.ok();
        }

        List<String> whiteDomains = new ArrayList<>();
        whiteDomains.add(chatToolProperties.getApiUrl());
        whiteDomains.add(chatToolProperties.getWebUrl());

        Map<String, Object> result = new HashMap<>(2);
        result.put("agents", agents);
        result.put("whiteDomains", whiteDomains);
        return ApiRespUtils.ok(result);
    }
}
