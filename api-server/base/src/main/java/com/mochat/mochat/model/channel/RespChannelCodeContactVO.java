package com.mochat.mochat.model.channel;

import lombok.Data;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/1 10:05 上午
 * @description 渠道码客户列表接口响应 VO
 */
@Data
public class RespChannelCodeContactVO {

    private Integer contactId;
    private String name;
    private List<String> employees;
    private String createTime;
}
