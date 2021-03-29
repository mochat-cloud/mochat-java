package com.mochat.mochat.model.subsystem;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 更新子账号账户信息
 * @author: zhaojinjian
 * @create: 2020-11-23 14:55
 **/
@Data
public class UpdateSubSystemRequest {
    /**
     * 子账户 id
     */
    @NotNull(message = "userId 不能为空")
    private Integer userId;

    /**
     * 账户名
     */
    @NotBlank(message = "userName 不能为空")
    private String userName;

    /**
     * 账户手机号, 用于关联企业成员
     */
    @NotBlank(message = "phone 不能为空")
    private String phone;

    /**
     * 性别(1-男2-女)
     *
     * 允许值: 1, 2
     */
    @Range(min = 1, max = 2, message = "gender 超出范围")
    private Integer gender;

    /**
     * 角色 id
     */
    @NotNull(message = "roleId 不能为空")
    private Integer roleId;

    /**
     * 状态(0-未启用1-正常2-禁用)
     *
     * 允许值: 0, 1, 2
     */
    @Range(min = 0, max = 2, message = "status 超出范围")
    private int status;
}
