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
@TableName("mc_contact_employee_track" )
public class ContactEmployeeTrackEntity {

	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 员工 ID(mc_work_employee.id)
	 */
	private Integer employeeId;

	/**
	 * 外部联系人 ID work_contact.id
	 */
	private Integer contactId;

	/**
	 * 事件
	 */
	private Integer event;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 企业表ID corp.id
	 */
	private Integer corpId;

	@TableField(fill = FieldFill.INSERT)
	private Date createdAt;
	@TableField(fill = FieldFill.UPDATE)
	private Date updatedAt;
	@TableLogic
	private Date deletedAt;
}
