package com.mochat.mochat.common.model;

import lombok.Data;

/**
 * @description:分页对象
 * @author: Huayu
 * @time: 2020/11/23 16:42
 */
@Data
public class PageModel {
    private Integer perPage;

    private Integer total;

    private Integer totalPage;

    public PageModel(Integer perPage, Integer total, Integer totalPage) {
        this.perPage = perPage;
        this.total = total;
        this.totalPage = totalPage;
    }
}
