package com.mochat.mochat.dao.entity.medium;

import com.baomidou.mybatisplus.annotation.*;
import com.mochat.mochat.common.validation.ToolInterface;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @description:素材库
 * @author: Huayu
 * @time: 2020/12/4 17:05
 */
@TableName("mc_medium")
@Data
public class MediumEnyity {
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "素材ID不能为空")
    private Integer id;
    private String mediaId;//素材媒体标识[有效期3天]
    private Integer lastUploadTime;//L上一次微信素材上传的时间戳
    @NotNull(groups = {ToolInterface.mediumStore.class},message = "类型不能为空")
    private Integer type;//类型 1文本、2图片、3音频、4视频、5小程序、6文件素材
    @NotNull(groups = {ToolInterface.mediumStore.class},message = "内容不能为空")
    private String  content;//具体内容:
    private Integer corpId;//企业表ID
    @NotNull(groups = {ToolInterface.mediumStore.class},message = "素材分组ID不能为空")
    private Integer mediumGroupId;//素材分组ID
    private Integer userId;//上传者ID
    private String userName;//上传者名称
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
