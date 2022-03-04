package com.mochat.mochat.model;

/**
 * @description:
 * @author: Andy
 * @time: 2020/11/25 10:05
 */
public class PageTest {

    private String page;

    private String perPage;

    private String total;

    private String totalPage;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPerPage() {
        return perPage;
    }

    public void setPerPage(String perPage) {
        this.perPage = perPage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public PageTest(String perPage, String total, String totalPage) {
        this.perPage = perPage;
        this.total = total;
        this.totalPage = totalPage;
    }
}
