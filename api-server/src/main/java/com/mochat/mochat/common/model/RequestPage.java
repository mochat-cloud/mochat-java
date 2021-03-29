package com.mochat.mochat.common.model;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2020/11/26 6:21 下午
 * @description 接口请求通用 Page
 */
@Data
public class RequestPage {
    private int page = 1;
    private int perPage = 20;

    public RequestPage(int page, int perPage) {
        this.page = page;
        this.perPage = perPage;
    }
    public RequestPage() {
    }
}
