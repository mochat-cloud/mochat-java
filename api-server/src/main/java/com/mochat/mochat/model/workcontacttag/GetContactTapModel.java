package com.mochat.mochat.model.workcontacttag;

import lombok.Data;

/**
 * @author zhaojinjian
 * @ClassName GetContactTapModel.java
 * @Description TODO
 * @createTime 2020/12/3 17:01
 */
@Data
public class GetContactTapModel {
    private Integer contactId;
    private Integer tagId;
    private String tagName;
}
