package com.mochat.mochat.dao.entity.sensitive;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @description:敏感词库实体类
 * @author: Huayu
 * @time: 2021/1/27 10:43
 */
@TableName("mc_sensitive_word")
@Data
public class SensitiveWordEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer corpId;//企业id
    private String name;//敏感词名称
    private Integer groupId;//智能风控分组id
    private Integer status; //1-开启，2-关闭）
    private Integer employeeNum;//员工触发次数
    private Integer contactNum;//客户触发次数
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}
