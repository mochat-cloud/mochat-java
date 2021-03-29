package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Description  同步时间表
 * @Author  idea
 * @Date 2020-11-26 
 */
@Data
@TableName("mc_work_update_time")
public class WorkUpdateTimeEntity {

	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 企业表ID（mc_crop.id）
	 */
	private Integer corpId;

	/**
	 * 类型（1.通讯录，2.客户，3.标签，4.部门 5.会放内容存档）
	 */
	private Integer type;

	/**
	 * 最后一次同步时间
	 */
	private Date lastUpdateTime;

	/**
	 * 错误信息
	 */
	private String errorMsg;

	private Date createdAt;

	private Date updatedAt;
}
