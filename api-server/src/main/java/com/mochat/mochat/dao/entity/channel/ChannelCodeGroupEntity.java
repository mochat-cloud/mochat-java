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
 * @description 渠道码分组表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("mc_channel_code_group")
public class ChannelCodeGroupEntity extends Model<ChannelCodeGroupEntity> {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer corpId;
  private String name;
  private Date createdAt;
  private Date updatedAt;
  @TableLogic
  private Date deletedAt;

  public ChannelCodeGroupEntity(String name, Integer corpId) {
    this.name = name;
    this.corpId = corpId;
  }

}
