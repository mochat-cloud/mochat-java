package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/13 15:24
 */
@TableName("test")
public class SysTestEntity {

    public SysTestEntity() {
    }

    @TableId("id")
    private String id;
    private String c;
    private String d;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }
}
