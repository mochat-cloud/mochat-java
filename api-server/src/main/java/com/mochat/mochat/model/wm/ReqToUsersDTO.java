package com.mochat.mochat.model.wm;

import com.mochat.mochat.common.model.RequestPage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author: yangpengwei
 * @time: 2020/12/1 10:50 上午
 * @description ToUsers 接口请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReqToUsersDTO extends RequestPage {

    @NotNull(message = "员工 id 不能为 null")
    private Integer workEmployeeId;

    /**
     * 可选参数
     */
    @NotNull(message = "类型不能为 null")
    @Range(min = 0, max = 2, message = "类型无效")
    private Integer toUsertype;

    private String name;

}
