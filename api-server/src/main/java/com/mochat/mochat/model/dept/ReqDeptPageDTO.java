package com.mochat.mochat.model.dept;

import com.mochat.mochat.common.model.RequestPage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReqDeptPageDTO extends RequestPage {
    private String name;
    private String parentName;
}
