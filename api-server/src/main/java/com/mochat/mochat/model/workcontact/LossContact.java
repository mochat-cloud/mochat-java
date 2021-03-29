package com.mochat.mochat.model.workcontact;

import java.util.List;
import java.util.Map;

/**
 * @author zhaojinjian
 * @ClassName LessContact.java
 * @Description TODO
 * @createTime 2020/12/26 16:43
 */
public class LossContact {
    //每页显示数
    private Integer perPage;
    //总条数
    private Long total;
    //总页数
    private Long totalPage;
    //流失客户的id
    private Map<String,Integer> empIdAndContactId;

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Map<String, Integer> getEmpIdAndContactId() {
        return empIdAndContactId;
    }

    public void setEmpIdAndContactId(Map<String, Integer> empIdAndContactId) {
        this.empIdAndContactId = empIdAndContactId;
    }
}
