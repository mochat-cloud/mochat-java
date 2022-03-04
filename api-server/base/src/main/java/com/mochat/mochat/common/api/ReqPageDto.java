package com.mochat.mochat.common.api;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2020/11/26 6:21 下午
 * @description 接口请求通用 Page
 */
@Data
public class ReqPageDto {
    private Integer page = 1;
    private Integer perPage = 20;
}
