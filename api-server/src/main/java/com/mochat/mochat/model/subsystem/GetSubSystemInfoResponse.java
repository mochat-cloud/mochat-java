package com.mochat.mochat.model.subsystem;

import lombok.Data;

import java.util.List;

/**
 * @description: 获取子账户详情
 * @author: zhaojinjian
 * @create: 2020-11-23 18:38
 **/
@Data
public class GetSubSystemInfoResponse {
    private Integer userId;
    private String userName;
    private String phone;
    private Integer gender;
    private Integer roleId;
    private String roleName;
    private Integer status;
    private List<UserDeptVO> department;

    @Data
    public static class UserDeptVO {
        private Integer departmentId;
        private String departmentName;
    }

}
