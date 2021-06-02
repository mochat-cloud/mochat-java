package com.mochat.mochat.dao.entity.permission;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 9:51 上午
 * @description 用户与角色关联表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mc_rbac_user_role")
@EqualsAndHashCode(callSuper=false)
public class McRbacUserRoleEntity extends Model<McRbacUserRoleEntity> {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer userId;
  private Integer roleId;
  @TableField(fill = FieldFill.INSERT)
  private Date createdAt;
  @TableField(fill = FieldFill.UPDATE)
  private Date updatedAt;
  @TableLogic
  private Date deletedAt;

}
