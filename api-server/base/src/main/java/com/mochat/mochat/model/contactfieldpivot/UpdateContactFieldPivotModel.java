package com.mochat.mochat.model.contactfieldpivot;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.config.ex.ParamException;
import lombok.Data;

/**
 * @author zhaojinjian
 * @ClassName UpdateContactFieldPivotModel.java
 * @Description TODO
 * @createTime 2020/12/24 18:11
 */
@Data
public class UpdateContactFieldPivotModel {
    private Integer contactId;
    private String userPortrait;

    @Data
    public static class UserPortrait
    {
        private Integer contactFieldId;
        private Integer contactFieldPivotId;
        private Integer type;
        private String value;
    }
    public void verifyParam()
    {
        if(this.contactId==null||this.getUserPortrait()==null){
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
    }
}
