package com.mochat.mochat.model.workcontacttag;

import lombok.Data;

/**
 * @author zhaojinjian
 * @ClassName GetEmployeeTagModel.java
 * @Description TODO
 * @createTime 2020/12/3 14:46
 */
@Data
public class GetEmployeeTagModel {
    private Integer tagId;
    private String tagName;
    private Integer contactId;
    private Integer empId;
}
