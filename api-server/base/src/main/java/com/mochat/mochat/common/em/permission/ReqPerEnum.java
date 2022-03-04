package com.mochat.mochat.common.em.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/15 10:50 上午
 * @description 权限管理枚举 - 用于接口请求判断权限
 */
@Getter
@AllArgsConstructor
public enum ReqPerEnum {

    /**
     * 全部权限
     */
    ALL(0),

    /**
     * 部门权限
     */
    DEPARTMENT(1),

    /**
     * 员工权限
     */
    EMPLOYEE(2);

    private Integer value;

    public Integer getValue() {
        return value;
    }

}