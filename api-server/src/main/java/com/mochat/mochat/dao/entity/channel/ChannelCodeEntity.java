package com.mochat.mochat.dao.entity.channel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
  private Date createdAt;
  private Date updatedAt;
  @TableLogic
  private Date deletedAt;

}
