package com.mochat.mochat.model.dept;

import com.mochat.mochat.common.api.ReqPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReqDeptPageDTO extends ReqPageDto {
    private String name;
    private String parentName;
}
