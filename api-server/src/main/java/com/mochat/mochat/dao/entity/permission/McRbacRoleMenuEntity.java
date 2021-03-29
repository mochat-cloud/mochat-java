package com.mochat.mochat.dao.entity.permission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @author: yangpengwei
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
  private Date createdAt;
  private Date updatedAt;

}
