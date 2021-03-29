package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @description:(通讯录)部门管理
 * @author: Huayu
 * @time: 2020/11/28 17:32
 */
@Data
@TableName("mc_work_department")
public class WorkDeptEntity {
    
    /**
     * 部门 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 微信部门自增ID
     */
    private Integer wxDepartmentId;
    
    /**
     * 企业表 id
     */
    private Integer corpId;
    
    /**
     * 部门名称
     */
    private String name;
    
    /**
     * 父部门 id
     */
    private Integer parentId;
    
    /**
     * 微信父部门 id
     */
    private Integer wxParentid;
    
    /**
     * 部门排序
     */
    @TableField("`order`")
    private Integer order;
    
    /**
     * 部门等级
     */
    private Integer level;
    
    /**
     * 部门父 id 路径
     */
    private String path;
    
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
	private Date deletedAt;
}
