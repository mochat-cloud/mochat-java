package com.mochat.mochat.dao.entity.permission;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 9:52 上午
 * @description 菜单表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mc_rbac_menu")
@EqualsAndHashCode(callSuper=false)
public class McRbacMenuEntity extends Model<McRbacMenuEntity> {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer parentId;
  private String name;
  private Integer level;
  private String path;
  private String icon;
  private Integer status;
  private Integer linkType;
  private Integer isPageMenu;
  private String linkUrl;
  private Integer dataPermission;
  private Integer operateId;
  private String operateName;
  private Integer sort;
  @TableField(fill = FieldFill.INSERT)
  private Date createdAt;
  @TableField(fill = FieldFill.UPDATE)
  private Date updatedAt;
  @TableLogic
  private Date deletedAt;

}
