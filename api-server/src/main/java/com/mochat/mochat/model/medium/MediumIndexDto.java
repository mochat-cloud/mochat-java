package com.mochat.mochat.model.medium;

import com.mochat.mochat.common.api.ReqPageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MediumIndexDto extends ReqPageDto {
    private String mediumGroupId;
    private String searchStr;
    private Integer type = 0;
}
