package com.mochat.mochat.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * @description:企业实体类
 * @author: Huayu
 * @time: 2020/11/23 19:10
 */
@TableName("mc_corp")
@Getter
@Setter
public class CorpEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @TableField("id")
    private Integer corpId;
    @TableField("name")
    @NotNull(message = "企业名称不能为空")
    private String corpName;//企业名称
    @TableField("wx_corpid")
    @NotNull(message = "企业微信ID不能为空")
    private String wxCorpId;//企业微信ID
    private String socialCode;//企业代码(企业统一社会信用代码)
    @NotNull(message = "企业通讯录secret不能为空")
    private String employeeSecret;//企业通讯录secret
    private String eventCallback;//事件回调地址
    @NotNull(message = "外部联系人secret不能为空")
    private String contactSecret;//企业外部联系人secret
    private String token;//回调token
    private String encodingAesKey;//回调消息加密串
    @JSONField(name = "time", format = "yyyy/MM/dd HH:mm:ss")
    private Date createdAt;
    @JSONField(name = "time", format = "yyyy/MM/dd HH:mm:ss")
    private Date updatedAt;
    @JSONField(name = "time", format = "yyyy/MM/dd HH:mm:ss")
    @TableLogic
    private Date deletedAt;

}
