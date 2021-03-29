package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Description 用户画像
 * @Author  idea
 * @Date 2020-11-26 
 */

@Data
@TableName("mc_contact_field_pivot")
public class ContactFieldPivotEntity {
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 客户表ID（work_contact.id）
	 */

	private Integer contactId;

	/**
	 * 高级属性表ID(contact_field.id）
	 */

	private Integer contactFieldId;

	/**
	 * 高级属性值
	 */

	private String value;


	private Date createdAt;


	private Date updatedAt;


	@TableLogic
	private Date deletedAt;
}
