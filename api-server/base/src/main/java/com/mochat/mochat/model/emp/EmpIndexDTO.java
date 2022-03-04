package com.mochat.mochat.model.emp;

import com.mochat.mochat.common.api.ReqPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/4 3:19 下午
 * @description 成员 - 成员列表 接口 DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EmpIndexDTO extends ReqPageDto {

    /**
     * 成员状态（可选）（1=已激活，2=已禁用，4=未激活，5=退出企业）
     */
    private Integer status;
    
    /**
     * 成员名称（可选）
     */
    private String name;

    /**
     * 外部联系人权限（可选）
     */
    private String contactAuth;
}
