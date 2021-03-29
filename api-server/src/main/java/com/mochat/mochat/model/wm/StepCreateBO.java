package com.mochat.mochat.model.wm;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 2:19 下午
 * @description 会话内容存档配置 - 微信后台配置-步骤页面
 */
@Data
public class StepCreateBO {

    /**
     * 会话配置ID
     */
    private Integer id;

    /**
     * (会话内容)申请进度 0未申请 1填写企业信息 2添加客服提交资料 3配置后台 4完成
     */
    private Integer chatApplyStatus;

    /**
     * 企业名称
     */
    private String corpName = "";

    /**
     * 微信企业ID
     */
    private String wxCorpId = "";

    /**
     * 客服联系方式(图片)
     */
    private String serviceContactUrl = "";

    /**
     * 白名单IP[可信IP地址]
     */
    private List<String> chatWhitelistIp;

    /**
     * 白名单IP[可信IP地址]
     */
    private String chatWhitelistIpJson;

    /**
     * 公钥
     */
    private String rsaPublicKey = "";

    /**
     * 私钥
     */
    private String rsaPrivateKey = "";

    public List<String> getChatWhitelistIp() {
        chatWhitelistIp = JSON.parseArray(getChatWhitelistIpJson(), String.class);
        return chatWhitelistIp;
    }

    private void setChatWhitelistIp(List<String> chatWhitelistIp) {
    }
    private String getChatWhitelistIpJson() {
        if (null == chatWhitelistIpJson || chatWhitelistIpJson.isEmpty()) {
            return "[]";
        }
        return chatWhitelistIpJson;
    }
}
