package com.mochat.mochat.dao.entity.channel;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.util.Date;

/**
 * @author: yangpengwei
 * @time: 2021/2/22 4:51 下午
 * @description 渠道码表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("mc_channel_code")
public class ChannelCodeEntity extends Model<ChannelCodeEntity> {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer corpId;
  private Integer groupId;
  private String name;
  private String qrcodeUrl;
  private String wxConfigId;
  private Integer autoAddFriend;
  private String tags;
  private Integer type;
  private String drainageEmployee;
  private String welcomeMessage;
  @TableField(fill = FieldFill.INSERT)
  private Date createdAt;
  @TableField(fill = FieldFill.UPDATE)
  private Date updatedAt;
  @TableLogic
  private Date deletedAt;

}
