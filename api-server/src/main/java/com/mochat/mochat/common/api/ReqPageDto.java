package com.mochat.mochat.common.api;

import lombok.Data;

/**
 * 默认分页参数
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/21
 */
@Data
public class ReqPageDto {
    private Integer page = 1;
    private Integer perPage = 20;
}
