package com.mochat.mochat.model.wm;

import com.mochat.mochat.common.api.ReqPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/1 10:50 上午
 * @description ToUsers 接口请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReqTestDTO extends ReqPageDto {

    @NotNull(message = "xxx")
    private Integer id;

    @NotNull(message = "xxx")
    private String name;

}
