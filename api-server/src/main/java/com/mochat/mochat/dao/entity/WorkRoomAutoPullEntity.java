package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Description  
 * @Author  idea
 * @Date 2020-12-11 
 */

@Data
@TableName("mc_work_room_auto_pull" )
@EqualsAndHashCode(callSuper = false)
public class WorkRoomAutoPullEntity extends Model<WorkRoomAutoPullEntity> {

	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 企业表ID(mc_corp.id)
	 */
	private Integer corpId;

	/**
	 * 二维码名称
	 */
	private String qrcodeName;

	/**
	 * 二维码地址
	 */
	private String qrcodeUrl;

	/**
	 * 二维码凭证
	 */
	private String wxConfigId;

	/**
	 * 添加验证 （1:需验证 2:直接通过）
	 */
	private Integer isVerified;

	/**
	 * 入群引导语
	 */
	private String leadingWords;

	/**
	 * 群标签 [{`tag_id`: `1`,`type`: 1,`tag_name`:`标签`,group_id:`1` ,group_name`:分组名称}]
	 */
	private String tags;

	/**
	 * 使用成员[{`id`: `1`,name`:`成员`}]
	 */
	private String employees;

	/**
	 * 群[{`id`: `1`,`type`: 1,`name`:`成员`,room_max:'群上限'}]
	 */
	private String rooms;

	private Date createdAt;

	private Date updatedAt;

	@TableLogic
	private Date deletedAt;
}
