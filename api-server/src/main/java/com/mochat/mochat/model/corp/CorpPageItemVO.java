package com.mochat.mochat.model.corp;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2021/3/22 3:44 下午
 * @description 企业微信授权列表/会话内容存档配置列表
 */
@Data
public class CorpPageItemVO {
    private Integer corpId;
    private String corpName;
    private String wxCorpId;
    private String createdAt;
    private Integer chatApplyStatus;
    private Integer chatStatus;
    private String messageCreatedAt;
}
