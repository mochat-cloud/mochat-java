package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * @Description  
 * @Author  idea
 * @Date 2020-12-10 
 */

@Data
@TableName("mc_contact_process" )
public class ContactProcessEntity {


	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * corp表id
	 */
	private Integer corpId;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 排序
	 */
	@TableField("`order`")
	private Integer order;

	@TableField(fill = FieldFill.INSERT)
	private Date createdAt;
	@TableField(fill = FieldFill.UPDATE)
	private Date updatedAt;

	@TableLogic
	private Date deletedAt;
}
