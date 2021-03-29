package com.mochat.mochat.dao.entity.sensitive;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @description:敏感词分组实体类
 * @author: Huayu
 * @time: 2021/1/28 14:17
 */
@TableName("mc_sensitive_word_group")
@Data
public class SensitiveWordGroupEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer corpId;//企业id
    private Integer userId;//用户id(mc_user.id)
    private Integer EmployeeId;//员工id （mc_work_employee.id)
    private String name;//分组名称
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
