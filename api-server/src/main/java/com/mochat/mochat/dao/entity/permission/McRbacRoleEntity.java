package com.mochat.mochat.dao.entity.permission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 9:52 上午
 * @description 角色表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mc_rbac_role")
@EqualsAndHashCode(callSuper=false)
public class McRbacRoleEntity extends Model<McRbacRoleEntity> {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer tenantId;
  private String name;
  private String remarks;
  private Integer status;
  private Integer operateId;
  private String operateName;
  private String dataPermission;
  private Date createdAt;
  private Date updatedAt;
  @TableLogic
  private Date deletedAt;

}
