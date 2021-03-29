package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description  
 * @Author  zhaojinjian
 * @Date 2020-11-20 
 */

@Data
@ToString
@TableName("mc_work_employee" )
public class WorkEmployeeEntity {

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 * wx.userId
	 */
	private String wxUserId;

	/**
	 * 所属企业corpid（mc_corp.id）
	 */
	@NotNull(message = "企业ID不能为空")
	private Integer corpId;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 职位信息
	 */
	private String position;

	/**
	 * 性别。0表示未定义，1表示男性，2表示女性
	 */
	private Integer gender;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 头像url
	 */
	private String avatar;

	/**
	 * 头像缩略图
	 */
	private String thumbAvatar;

	/**
	 * 座机
	 */
	private String telephone;

	/**
	 * 别名
	 */
	private String alias;

	/**
	 * 扩展属性
	 */
	private String extattr;

	/**
	 * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业
	 */
	private Integer status;

	/**
	 * 员工二维码
	 */
	private String qrCode;

	/**
	 * 员工对外属性
	 */
	private String externalProfile;

	/**
	 * 员工对外职位
	 */
	private String externalPosition;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 全局唯一id
	 */
	private String openUserId;

	/**
	 * 微信端主部门ID
	 */
	private Integer wxMainDepartmentId;

	/**
	 * 主部门id(mc_work_department.id)
	 */
	private Integer mainDepartmentId;

	/**
	 * 子账户ID(mc_user.id)
	 */
	private Integer logUserId;

	/**
	 * 是否配置外部联系人权限（1.是 2.否）
	 */
	private Integer contactAuth;
	private Date createdAt;
	private Date updatedAt;
	@TableLogic
	private Date deletedAt;

	public WorkEmployeeEntity(){

	}

	public WorkEmployeeEntity(int id){
		this.id = id;
	}
}
