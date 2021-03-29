package com.mochat.mochat.model;

import com.mochat.mochat.common.em.RespErrCodeEnum;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author: yangpengwei
 * @time: 2020/12/2 11:42 上午
 * @description 接口 response 统一包装类
 */
public class ApiRespVO implements Serializable {

    private Integer code;
    private String msg;
    private Object data;

    public ApiRespVO() {}

    public ApiRespVO(RespErrCodeEnum respErrCodeEnum) {
        this.code = respErrCodeEnum.getCode();
        this.msg = respErrCodeEnum.getMsg();
        this.data = "";
    }

    public ApiRespVO(RespErrCodeEnum respErrCodeEnum, Object data) {
        this.code = respErrCodeEnum.getStatus();
        this.msg = respErrCodeEnum.getMsg();
        this.data = data;
    }

    public ApiRespVO(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        if (code == null) {
            return 200;
        }
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        if (msg == null) {
            return "";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        if (data == null) {
            return "";
        }
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApiRespVO apiRespVO = (ApiRespVO) o;

        if (!code.equals(apiRespVO.code)) {
            return false;
        }
        if (!Objects.equals(msg, apiRespVO.msg)) {
            return false;
        }
        return Objects.equals(data, apiRespVO.data);
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (msg != null ? msg.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ApiRespVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
