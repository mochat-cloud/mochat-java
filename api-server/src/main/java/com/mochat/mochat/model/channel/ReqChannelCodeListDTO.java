package com.mochat.mochat.model.channel;

import lombok.Data;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/1 10:05 上午
 * @description 渠道码列表接口 DTO
 */
@Data
public class ReqChannelCodeListDTO {
    private String name;
    private Integer type;
    private Integer groupId;
}
