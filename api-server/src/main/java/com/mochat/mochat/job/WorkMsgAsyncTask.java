package com.mochat.mochat.job;

import com.mochat.mochat.service.wm.IWorkMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 10:07 上午
 * @description 异步定时任务, 同步微信会话存档数据
 */
@Component
@EnableScheduling
@EnableAsync
public class WorkMsgAsyncTask {

    private static final String OS_LINUX = "linux";

    @Autowired
    private IWorkMsgService workMsgService;

    @Async
    @Scheduled(initialDelay = 1000, fixedDelay = 1 * 60 * 1000)
    public void onAsyncMsg() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (OS_LINUX.equals(osName)) {
            workMsgService.onAsyncMsg();
        }
    }
}
