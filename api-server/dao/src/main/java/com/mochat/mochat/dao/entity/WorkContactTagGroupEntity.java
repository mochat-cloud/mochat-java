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
@TableName("mc_work_contact_tag_group" )
public class WorkContactTagGroupEntity {


	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 微信企业标签分组ID
	 */
	private String wxGroupId;

	/**
	 * 企业表ID （mc_corp.id）
	 */
	private Integer corpId;

	/**
	 * 客户标签分组名称
	 */
	private String groupName;

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
