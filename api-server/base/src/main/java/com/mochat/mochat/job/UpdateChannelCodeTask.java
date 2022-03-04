package com.mochat.mochat.job;

import com.mochat.mochat.dao.entity.channel.ChannelCodeEntity;
import com.mochat.mochat.service.channel.IChannelCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/3 3:07 下午
 * @description 定时任务更新渠道码二维码绑定成员
 */
@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class UpdateChannelCodeTask {

    @Autowired
    private IChannelCodeService channelCodeService;

    private static int count = 60;

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/3 3:10 下午
     * @description 刷新间隔 1 分钟
     */
    @Async
    @Scheduled(fixedDelay = 60000)
    public void updateChannelCodeQR() {
        List<ChannelCodeEntity> codeEntityList = channelCodeService.lambdaQuery()
                .select(ChannelCodeEntity::getId)
                .list();
        for (ChannelCodeEntity entity : codeEntityList) {
            channelCodeService.updateChannelCodeQr(entity.getId());
        }
    }

}

