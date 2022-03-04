package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description  
 * @Author  idea
 * @Date 2020-12-10 
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mc_contact_field" )
public class ContactFieldEntity {


	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 字段标识 input-name
	 */
	private String name;

	/**
	 * 字段名称 input-label
	 */
	private String label;

	/**
	 * 字段类型 input-type 0text 1radio 2 checkbox 3select 4file 5date 6dateTime 7number 8rate
	 */
	private Integer type;

	/**
	 * 字段类型描述
	 */
	@TableField(exist = false)
	private String typeText;

	/**
	 * 字段可选值 input-options
	 */
	private String options;

	/**
	 * 排序
	 */
	@TableField("`order`")
	private Integer order;

	/**
	 * 状态 0不展示 1展示
	 */
	private Integer status;

	/**
	 * 是否为系统字段 0否1是
	 */
	private Integer isSys;

	@TableField(fill = FieldFill.INSERT)
	private Date createdAt;
	@TableField(fill = FieldFill.UPDATE)
	private Date updatedAt;

	private Date deletedAt;
}
