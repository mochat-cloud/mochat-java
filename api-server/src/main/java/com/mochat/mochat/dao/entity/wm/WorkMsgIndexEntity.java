package com.mochat.mochat.dao.entity.wm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mc_work_message_index")
public class WorkMsgIndexEntity {

    /**
     * 企业 id
     */
    @TableId(type = IdType.AUTO)
    private Integer corpId;

    /**
     * 接收方 id
     */
    private Integer toId;

    /**
     * 接收方类型
     */
    private Integer toType;

    /**
     * 发送方 id
     */
    private Integer fromId;

    /**
     * flag
     */
    private String flag;

    private Date createdAt;
    private Date updatedAt;

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public Integer getToType() {
        return toType;
    }

    public void setToType(Integer toType) {
        this.toType = toType;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}