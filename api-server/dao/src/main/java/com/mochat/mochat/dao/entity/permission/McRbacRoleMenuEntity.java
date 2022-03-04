package com.mochat.mochat.dao.entity.permission;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/11 9:51 上午
 * @description 角色与菜单关联表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mc_rbac_role_menu")
@EqualsAndHashCode(callSuper=false)
public class McRbacRoleMenuEntity extends Model<McRbacRoleMenuEntity> {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer roleId;
  private Integer menuId;
  @TableField(fill = FieldFill.INSERT)
  private Date createdAt;
  @TableField(fill = FieldFill.UPDATE)
  private Date updatedAt;

}
