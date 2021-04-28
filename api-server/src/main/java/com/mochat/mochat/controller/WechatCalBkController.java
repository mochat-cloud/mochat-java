package com.mochat.mochat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.annotion.LoginToken;
import com.mochat.mochat.common.util.MessageUtil;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.mapper.corp.CorpMapper;
import com.mochat.mochat.service.wxback.IWxCallbackChatService;
import com.mochat.mochat.service.wxback.IWxCallbackContactService;
import com.mochat.mochat.service.wxback.IWxCallbackEmployeeService;
import com.mochat.mochat.service.wxback.IWxCallbackTagService;
import com.mochat.mochat.weixin.mp.WXBizMsgCrypt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/9 14:46
 */
@Slf4j
@RestController
@RequestMapping("/weWork")
public class WechatCalBkController {

    /**
     * 通讯录相关回调
     */
    private static final String EVENT_EMPLOYEE = "change_contact";

    /**
     * 客户相关回调
     */
    private static final String EVENT_CONTACT = "change_external_contact";

    /**
     * 客户群相关回调
     */
    private static final String EVENT_CHAT = "change_external_chat";

    /**
     * 客户标签相关回调
     */
    private static final String EVENT_CONTACT_TAG = "change_external_tag";

    private Map<String, String> map = new HashMap<>(0);

    @Autowired
    private CorpMapper corpMapper;

    @Autowired
    private IWxCallbackContactService wxCallbackContactService;

    @Autowired
    private IWxCallbackChatService wxCallbackChatServiceImpl;

    @Autowired
    private IWxCallbackEmployeeService wxCallbackEmployeeService;

    @Autowired
    private IWxCallbackTagService wxCallbackTagService;

    /**
     * @description:get请求 验签
     * @return:
     * @author: Huayu
     * @time: 2020/11/9 16:52
     */
    @GetMapping(value = "/callback", produces = {"text/html"})
    @LoginToken
    public String verifyURL(String msg_signature,
                            String timestamp,
                            String nonce,
                            String echostr,
                            String cid) throws Exception {

        msg_signature = processArg(msg_signature);
        timestamp = processArg(timestamp);
        nonce = processArg(nonce);
        echostr = processArg(echostr);
        cid = processArg(cid);

        log.debug(">>>>>>> msg_signature: " + msg_signature);
        log.debug(">>>>>>> timestamp: " + timestamp);
        log.debug(">>>>>>> nonce: " + nonce);
        log.debug(">>>>>>> echostr: " + echostr);
        log.debug(">>>>>>> cid: " + cid);

        int corpId = Integer.parseInt(cid);
        String token = getToken(corpId);
        String wxCorpId = getWxCorpId(corpId);
        String encodingAesKey = getAesKey(corpId);

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, wxCorpId);
        // 验证微信回调参数是否有效
        String sEchoStr = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);

        //必须要返回解密之后的明文
        if (StringUtils.isBlank(sEchoStr)) {
            log.debug("URL验证失败");
        } else {
            log.debug("验证成功!");
        }

        log.debug(" >>>>>>>> sEchoStr: " + sEchoStr);

        return sEchoStr;
    }

    /**
     * 企业微信客户联系回调.
     *
     * @return success
     */
    @PostMapping(value = "/callback")
    @LoginToken
    public String callback(@RequestBody String requestBody, String msg_signature, String timestamp, String nonce, String cid) throws Exception {

        log.debug("<<<<<<<<< requestBody: " + requestBody);
        log.debug("<<<<<<<<< msg_signature: " + msg_signature);
        log.debug("<<<<<<<<< timestamp: " + timestamp);
        log.debug("<<<<<<<<< nonce: " + nonce);

        requestBody = processArg(requestBody);
        msg_signature = processArg(msg_signature);
        timestamp = processArg(timestamp);
        nonce = processArg(nonce);
        cid = processArg(cid);

        int corpId = Integer.parseInt(cid);
        String token = getToken(corpId);
        String wxCorpId = getWxCorpId(corpId);
        String encodingAesKey = getAesKey(corpId);

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, wxCorpId);

        String sMsg = wxcpt.DecryptMsg(msg_signature, timestamp, nonce, requestBody);

        log.debug(" >>>>>>>> 微信回调信息: " + sMsg);

        //将post数据转换为map
        Map<String, Object> dataMap = MessageUtil.parseXml(sMsg);
        dataMap.put("corpId", corpId);

        String dataJson = JSON.toJSONString(dataMap);
        log.debug(dataJson);

        return dispatch(dataJson);
    }

    private String processArg(String arg) {
        int index = arg.indexOf(",");
        if (index > 0) {
            return arg.substring(index + 1);
        }
        return arg;
    }

    @SneakyThrows
    private String dispatch(String dataJson) {
        JSONObject jsonObject = JSON.parseObject(dataJson);
        String event = jsonObject.getJSONObject("xml").getString("event");
        switch (event) {
            case EVENT_CONTACT:
                return wxCallbackContactService.dispatchEvent(dataJson);
            case EVENT_EMPLOYEE:
                return wxCallbackEmployeeService.dispatchEvent(dataJson);
            case EVENT_CHAT:
                return wxCallbackChatServiceImpl.updateChatCallBack(dataJson);
            case EVENT_CONTACT_TAG:
                return wxCallbackTagService.dispatchEvent(dataJson);
            default:
                return "";
        }
    }

    private String getWxCorpId(int corpId) {
        String key = "corp-" + corpId;
        String wxCorpId = map.get(key);
        if (wxCorpId == null) {
            wxCorpId = getCorpEntity(corpId).getWxCorpId();
        }
        return wxCorpId;
    }

    private String getToken(int corpId) {
        String key = "token-" + corpId;
        String token = map.get(key);
        if (token == null) {
            token = getCorpEntity(corpId).getToken();
        }
        return token;
    }

    private String getAesKey(int corpId) {
        String key = "aes-" + corpId;
        String aesKey = map.get(key);
        if (aesKey == null) {
            aesKey = getCorpEntity(corpId).getEncodingAesKey();
        }
        return aesKey;
    }

    /**
     * 获取企业信息
     *
     * @param corpId 企业 Id
     * @return 企业实体
     */
    private CorpEntity getCorpEntity(int corpId) {
        CorpEntity corpEntity = corpMapper.selectById(corpId);
        map.put("corp-" + corpId, corpEntity.getWxCorpId());
        map.put("token-" + corpId, corpEntity.getToken());
        map.put("aes-" + corpId, corpEntity.getEncodingAesKey());
        return corpEntity;
    }

}
