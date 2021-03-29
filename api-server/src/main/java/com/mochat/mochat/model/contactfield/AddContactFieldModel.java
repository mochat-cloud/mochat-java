package com.mochat.mochat.model.contactfield;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.config.ex.ParamException;

/**
 * @author zhaojinjian
 * @ClassName AddContactFieldModel.java
 * @Description 高级属性 - 添加
 * @createTime 2020/12/16 11:28
 */
public class AddContactFieldModel {
    private String label;
    private Integer type;
    private String options;
    private Integer order;
    private Integer status;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = (options != null && !options.isEmpty()) ? options : "[]";
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order == null ? 0 : order;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status == null ? 1 : status;
    }

    public void verifyParam() {
        if (this.label.isEmpty()) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
        if (this.type == null) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
    }
}
