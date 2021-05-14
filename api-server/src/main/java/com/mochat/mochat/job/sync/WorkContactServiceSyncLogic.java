package com.mochat.mochat.job.sync;

import com.mochat.mochat.service.impl.IWorkContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @author: yangpengwei
 * @time: 2021/5/8 9:49 上午
 * @description 客户异步服务
 */
@Component
@EnableAsync
public class WorkContactServiceSyncLogic {

    @Autowired
    private IWorkContactService contactService;

    /**
     * @author: yangpengwei
     * @time: 2021/5/10 10:53 上午
     * @description 同步客户
     */
    @Async
    public void onSync(int corpId) {
        contactService.synContactByCorpId(corpId);
    }

}
