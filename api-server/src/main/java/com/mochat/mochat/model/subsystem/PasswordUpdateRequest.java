package com.mochat.mochat.model.subsystem;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.config.ex.ParamException;
import lombok.Data;

/**
 * @description: 更新员工账户登录密码
 * @author: zhaojinjian
 * @create: 2020-11-18 14:52
 **/
@Data
public class PasswordUpdateRequest {
    private String oldPassword;
    private String newPassword;
    private String againNewPassword;
    public void  verifyParam()
    {
        if(!this.newPassword.equals(this.againNewPassword)){
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(),"密码不一致");
        }
    }


}
