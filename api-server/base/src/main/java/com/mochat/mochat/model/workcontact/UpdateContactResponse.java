package com.mochat.mochat.model.workcontact;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.config.ex.ParamException;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhaojinjian
 * @ClassName UpdateContactResponse.java
 * @Description TODO
 * @createTime 2020/12/6 11:10
 */
@Data
public class UpdateContactResponse {
    @NotNull(message = "客户ID")
    private Integer contactId;
    private String remark;
    private List<Integer> tag;
    private String description;
    private String businessNo;

    public void verifyParam(){
        if(this.contactId==null)
        {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
    }
}
