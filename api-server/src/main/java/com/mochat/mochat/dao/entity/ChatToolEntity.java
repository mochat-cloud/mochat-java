package com.mochat.mochat.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mc_chat_tool")
@EqualsAndHashCode(callSuper=false)
public class ChatToolEntity extends Model<ChatToolEntity> {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private String pageName;
  private String pageFlag;
  private Integer status;

  private Date createdAt;
  private Date updatedAt;

  @TableLogic
  private Date deletedAt;

}
