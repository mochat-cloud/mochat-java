package com.mochat.mochat.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Api 默认包装类
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/21
 *
 * @see ApiRespUtils
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRespVO {
    private int code;
    private String msg;
    private Object data;
}
