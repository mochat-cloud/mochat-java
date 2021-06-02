package com.mochat.mochat.dao.entity.channel;

import com.baomidou.mybatisplus.annotation.*;
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
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedAt;
    @TableLogic
    private Date deletedAt;

    public static ChannelCodeGroupEntity getInstance(String name, Integer corpId) {
        ChannelCodeGroupEntity entity = new ChannelCodeGroupEntity();
        entity.setName(name);
        entity.setCorpId(corpId);
        return entity;
    }

}
