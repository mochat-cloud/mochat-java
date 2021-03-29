package com.mochat.mochat.model.permission;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 10:13 上午
 * @description 角色管理 - 角色添加
 */
@Data
public class RoleEmpShowVO {

    /**
     * 员工 id
     */
    private Integer employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 员工手机号
     */
    private String phone;

    /**
     * 员工邮箱
     */
    private String email;

    /**
     * 员工部门
     */
    private String department;

}
