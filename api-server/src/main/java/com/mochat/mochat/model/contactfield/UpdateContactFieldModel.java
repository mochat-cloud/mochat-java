package com.mochat.mochat.model.contactfield;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.config.ex.ParamException;

/**
 * @author zhaojinjian
 * @ClassName UpdateContactFieldModel.java
 * @Description TODO
 * @createTime 2020/12/16 11:42
 */
public class UpdateContactFieldModel extends AddContactFieldModel {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void verifyParam()
    {
        super.verifyParam();
        if(this.id==null)
        {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
    }
}
