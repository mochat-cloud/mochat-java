package com.mochat.mochat.model.subsystem;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 获取子账户分页数据
 * @author: zhaojinjian
 * @create: 2020-11-25 14:33
 **/
@Data
public class GetSubSystemPage {
    private long notEnabledNum;
    private long normalNum;
    private long disableNum;
    private Page page;
    private List<UserList> list;

    @Data
    public class Page {
        private int perPage;
        private long total;
        private long totalPage;
    }

    @Data
    public class UserList {
        private Integer userId;
        private String userName;
        private List<Map<String,Object>> department;
        private String position;
        private String phone;
        private int status;
        private String statusText;
        private String createdAt;

    }
}
