package com.mochat.mochat.model.subsystem;

/**
 * @description: 获取子账户管理列表的查询条件
 * @author: zhaojinjian
 * @create: 2020-11-17 11:44
 **/
public class APIGetSubSystemRequest {

    /**
     * 账户手机号
     **/
    private String phone;

    /**
     * 账户状态(0-未启用1-正常2-禁用)
     * <p>
     * 允许值: 0, 1, 2
     **/
    private Integer status;

    /**
     * 页码
     * <p>
     * 默认值: 1
     **/
    private Integer page;

    /**
     * 每页条数
     * <p>
     * 默认值: 10
     **/
    private Integer perPage;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPerPage() {
        return perPage == null ? 10 : perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }
}
