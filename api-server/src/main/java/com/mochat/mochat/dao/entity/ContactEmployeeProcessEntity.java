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
@TableName("mc_contact_employee_process" )
public class ContactEmployeeProcessEntity {


	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 企业表ID（corp.id）
	 */
	private Integer corpId;

	/**
	 * 员工外部联系人中间表id(mc_work_contact_employee.id)
	 */
	private Integer contactEmployeeId;

	/**
	 * 员工ID（mc_work_employee.id）
	 */
	private Integer employeeId;

	/**
	 * 外部联系人ID（mc_work_contact.id）
	 */
	private Integer contactId;

	/**
	 * 跟进流程ID
	 */
	private Integer contactProcessId;

	/**
	 * 跟进流程名称
	 */
	private String contactProcessName;

	/**
	 * 跟进流程描述
	 */
	private String contactProcessDescription;

	private Date createdAt;

	private Date updatedAt;

	@TableLogic
	private Date deletedAt;
}
