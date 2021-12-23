package com.mochat.mochat.model.emp;

import lombok.Data;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/4 5:19 下午
 * @description 成员列表搜索条件数据
 */
@Data
public class EmpSearchConditionBO {
    private List<EmpEnumBO> status;
    private List<EmpEnumBO> contactAuth;
    private String syncTime;

    @Data
    public static class EmpEnumBO {
        private Integer id;
        private String name;
    }

}
