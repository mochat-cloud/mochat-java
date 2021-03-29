package com.mochat.mochat.model.contactfieldpivot;

import lombok.Data;

import java.util.List;

@Data
public class ContactFieldPivotVO {
    private Integer contactFieldId;
    private Integer contactFieldPivotId;
    private String name;
    private Object value;
    private String pictureFlag;
    private Integer type;
    private String typeText;
    private List<String> options;
}
