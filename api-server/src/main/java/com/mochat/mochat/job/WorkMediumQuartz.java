package com.mochat.mochat.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.em.medium.TypeEnum;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.dao.entity.medium.MediumEntity;
import com.mochat.mochat.service.impl.medium.IMediumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/11 6:25 下午
 * @description 媒体库 - 定时任务
 */
@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class WorkMediumQuartz {

    private final int DURATION = 60 * 60 * 60;

    @Autowired
    private IMediumService mediumService;

    /**
     * cron = 秒 分钟 小时 日 月 星期 年
     * 每个小时更新媒体库数据关联企业微信媒体库 id
     */
    @Async
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void onUpdateWxMediumId() {
        List<MediumEntity> mediumEntityList = mediumService.list();
        for (MediumEntity entity : mediumEntityList) {
            if (TypeEnum.TEXT.getCode().equals(entity.getType())) {
                continue;
            }

            int lastUploadTime = entity.getLastUploadTime();
            boolean isFirst = lastUploadTime < 1;
            boolean upload = lastUploadTime + DURATION >= System.currentTimeMillis() / 1000;
            if (isFirst || upload) {
                onAsyncUpload(entity);
            }
        }
    }

    @Async
    public void onAsyncUpload(MediumEntity entity) {
        if (entity == null) {
            return;
        }

        Integer type = entity.getType();
        if (type == null) {
            return;
        }

        JSONObject jsonObject = JSON.parseObject(entity.getContent());
        String key = null;
        String typeStr = "image";
        if (TypeEnum.PICTURE.getCode().equals(type)) {
            key = jsonObject.getString("imagePath");
        }
        if (TypeEnum.PICTURE_TEXT.getCode().equals(type)) {
            key = jsonObject.getString("imagePath");
        }
        if (TypeEnum.VOICE.getCode().equals(type)) {
            key = jsonObject.getString("voicePath");
            typeStr = "voice";
        }
        if (TypeEnum.VIDEO.getCode().equals(type)) {
            key = jsonObject.getString("videoPath");
            typeStr = "video";
        }
        if (TypeEnum.MINI_PROGRAM.getCode().equals(type)) {
            key = jsonObject.getString("imagePath");
        }
        if (TypeEnum.FILE.getCode().equals(type)) {
            key = jsonObject.getString("filePath");
            typeStr = "file";
        }

        if (key == null || key.isEmpty()) {
            return;
        }

        File file = AliyunOssUtils.getFile(key);

        String wxMediaId = WxApiUtils.uploadFileToTemp(entity.getCorpId(), typeStr, file);

        entity.setMediaId(wxMediaId);
        entity.setLastUploadTime((int) (System.currentTimeMillis()/1000));
        mediumService.updateById(entity);
    }

}
