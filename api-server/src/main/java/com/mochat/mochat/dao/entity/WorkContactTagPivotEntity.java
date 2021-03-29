package com.mochat.mochat.dao.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Description  客户标签-客户关联表
 * @Author  idea
 * @Date 2020-11-26 
 */

@Data
@TableName("mc_work_contact_tag_pivot")
public class WorkContactTagPivotEntity {

	@TableId(type= IdType.AUTO)
	private int id;

	/**
	 * 客户表ID（work_contact.id）
	 */
	private int contactId;

	/**
	 * 员工表id（work_employee.id）
	 */
	private int employeeId;

	/**
	 * 客户标签表ID（work_contact_tag.id）
	 */
	private
	Integer contactTagId;

	/**
	 * 该成员添加此外部联系人所打标签类型, 1-企业设置, 2-用户自定义
	 */
	private Integer type;

	private Date createdAt;

	private Date updatedAt;

	@TableLogic
	private Date deletedAt;
}
