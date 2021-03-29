package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Description  客户标签
 * @Author  idea
 * @Date 2020-11-26 
 */

@Data
@TableName("mc_work_contact_tag")
public class WorkContactTagEntity {
	/**
	 * 企业标签ID
	 */
	@TableId(type = IdType.AUTO)
	private int id;

	/**
	 * 微信企业标签ID
	 */
	private String wxContactTagId;

	/**
	 * 企业表ID （mc_corp.id）
	 */
	private int corpId;

	/**
	 * 标签名称
	 */
	private String name;

	/**
	 * 排序
	 */
	@TableField("`order`")
	private int order;

	/**
	 * 客户标签分组ID（mc_work_contract_tag_group.id）
	 */
	private int contactTagGroupId;

	private Date createdAt;

	private Date updatedAt;

	@TableLogic
	private Date deletedAt;

	@TableField(exist = false)
	private WorkContactTagPivotEntity workContactTagPivot;
}
