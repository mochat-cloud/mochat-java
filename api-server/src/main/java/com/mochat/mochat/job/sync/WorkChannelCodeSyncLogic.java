package com.mochat.mochat.job.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.util.FileUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.emp.DownUploadQueueUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.channel.ChannelCodeEntity;
import com.mochat.mochat.service.channel.IChannelCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2021/3/2 10:49 上午
 * @description 渠道码异步任务
 */
@Component
@EnableAsync
public class WorkChannelCodeSyncLogic {

    @Autowired
    private IChannelCodeService channelCodeService;

    /**
     * 创建微信 配置客户联系「联系我」方式 二维码
     */
    public void onCreateWxAddContactWayQrcode(ChannelCodeEntity entity, Map<String, List<?>> map) {
        int corpId = entity.getCorpId();
        boolean skipVerify = 1 == entity.getAutoAddFriend();
        int type = entity.getType();
        // status 组成规则: channelCode- + entity.getId
        String state = "channelCode-" + entity.getId();

        // 微信获取二维码
        String resultJson = WxApiUtils.requestCreateContactWay(
                corpId,
                type,
                skipVerify,
                state,
                map.get("eIds"),
                map.get("dId")
        );

        JSONObject jsonObject = JSON.parseObject(resultJson);
        String qrcodeUrl = jsonObject.getString("qr_code");
        String wxConfigId = jsonObject.getString("config_id");

        // 上传图片
        String qrcodeFileName = FileUtils.getFileNameOfContactWayQrCode();
        DownUploadQueueUtils.uploadFileByUrl(qrcodeFileName, qrcodeUrl);

        entity.setQrcodeUrl(qrcodeFileName);
        entity.setWxConfigId(wxConfigId);

        channelCodeService.updateById(entity);
    }

    /**
     * 更新微信 配置客户联系「联系我」方式 二维码
     */
    @Async
    public void onUpdateWxAddContactWayQrcode(int codeId, Map<String, List<?>> map) {
        ChannelCodeEntity entity = channelCodeService.getById(codeId);
        if (entity == null) {
            throw new CommonException("渠道码创建失败");
        }

        int corpId = entity.getCorpId();
        String wxConfigId = entity.getWxConfigId();

        WxApiUtils.requestUpdateContactWay(corpId, wxConfigId, map.get("eIds"), map.get("dId"));
    }

}
