package com.mochat.mochat.model;

import java.io.Serializable;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/10 17:29
 */
public class ServiceSuccessCode implements Serializable {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
