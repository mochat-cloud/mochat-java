package com.mochat.mochat.dao.entity.wm;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mc_work_message_1")
public class WorkMsgEntity {

    /**
     * 企业 id
     */
    @TableId(type = IdType.AUTO)
    private Integer corpId;


    /**
     * id
     */
    private Integer id;

    /**
     * seq 查询用
     */
    private Integer seq;

    /**
     * msgid
     */
    private String msgId;

    /**
     * 消息动作，0.send(发送消息) 1.recall(撤回消息) 2.switch(切换企业日志)
     * 注: 切换企业日志已被过滤, 不存储
     */
    private Integer action;

    /**
     * 发送者微信 id
     */
    private String from;

    /**
     * 接收者微信 id 数组
     */
    private String tolist;

    /**
     * 接收方类型 0通讯录 1外部联系人 2群
     */
    private Integer tolistType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 内容
     */
    private String content;

    /**
     * 时间
     */
    private String msgTime;

    /**
     * 微信群 id
     */
    private String wxRoomId;

    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;

    public Integer getCorpId() {
        if (null == corpId) {
            return 0;
        }
        return corpId;
    }

    public Integer setCorpId(Integer corpId) {
        return this.corpId = corpId;
    }

    public Integer getSeq() {
        if (null == seq) {
            return 0;
        }
        return seq;
    }

    public Integer setSeq(Integer seq) {
        return this.seq = seq;
    }

    public String getMsgId() {
        if (null == msgId) {
            return "";
        }
        return msgId;
    }

    public String setMsgId(String msgId) {
        return this.msgId = msgId;
    }

    public Integer getAction() {
        if (null == action) {
            return 0;
        }
        return action;
    }

    public Integer setAction(Integer action) {
        return this.action = action;
    }

    public String getFrom() {
        if (null == from) {
            return "";
        }
        return from;
    }

    public String setFrom(String from) {
        return this.from = from;
    }

    public String getTolist() {
        if (null == tolist) {
            return "";
        }
        return tolist;
    }

    public String setTolist(String tolist) {
        return this.tolist = tolist;
    }

    public Integer getTolistType() {
        return tolistType;
    }

    public void setTolistType(Integer tolistType) {
        this.tolistType = tolistType;
    }

    public Integer getMsgType() {
        if (null == msgType) {
            return 0;
        }
        return msgType;
    }

    public Integer setMsgType(Integer msgType) {
        return this.msgType = msgType;
    }

    public String getContent() {
        if (null == content) {
            return "";
        }
        return content;
    }

    public String setContent(String content) {
        return this.content = content;
    }

    public String getMsgTime() {
        if (null == msgTime) {
            return "";
        }
        return msgTime;
    }

    public String setMsgTime(String msgTime) {
        return this.msgTime = msgTime;
    }

    public String getWxRoomId() {
        if (null == wxRoomId) {
            return "";
        }
        return wxRoomId;
    }

    public String setWxRoomId(String wxRoomId) {
        return this.wxRoomId = wxRoomId;
    }

    @Override
    public String toString() {
        return "WorkMsgEntity{" +
                "corpId=" + corpId +
                ", seq=" + seq +
                ", msgId='" + msgId + '\'' +
                ", action=" + action +
                ", from='" + from + '\'' +
                ", tolist='" + tolist + '\'' +
                ", msgType=" + msgType +
                ", content='" + content + '\'' +
                ", msgTime='" + msgTime + '\'' +
                ", wxRoomId='" + wxRoomId + '\'' +
                '}';
    }
}