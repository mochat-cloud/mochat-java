package com.mochat.mochat.model.channel;

import lombok.Data;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2021/3/1 10:05 上午
 * @description 渠道码列表 VO
 */
@Data
public class RespChannelCodeListVO {
    private Integer channelCodeId;
    private String qrcodeUrl;
    private String name;
    private String type;
    private String groupName;
    private Integer contactNum;
    private List<String> tags;
    private String autoAddFriend;
}
