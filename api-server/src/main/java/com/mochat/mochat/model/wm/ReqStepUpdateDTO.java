package com.mochat.mochat.model.wm;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/1 10:50 上午
 * @description stepUpdate 接口请求参数
 */
@Data
public class ReqStepUpdateDTO {

    /**
     * (会话内容)申请进度 0未申请 1填写企业信息 2添加客服提交资料 3配置后台 4完成
     */
    @NotNull
    private Integer chatApplyStatus;

    /**
     * 白名单IP[可信IP地址]
     */
    @NotNull
    private List<String> chatWhitelistIp;

    /**
     * rsa密钥
     */
    @NotNull
    private String chatRsaKey;

    /**
     * 会话内容存档secret
     */
    @NotNull
    private String chatSecret;

    /**
     * 存档状态 0不存储 1存储
     */
    @NotNull
    private Integer chatStatus;
    
}
