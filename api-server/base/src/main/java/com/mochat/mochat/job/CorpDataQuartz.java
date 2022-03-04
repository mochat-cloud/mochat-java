package com.mochat.mochat.job;

import com.mochat.mochat.service.ICorpDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description:更新首页数据
 * @author: Huayua
 */
@Component
@EnableAsync
@EnableScheduling
public class CorpDataQuartz {

    @Autowired
    private ICorpDataService corpDataServiceImpl;

    @Async
    @Scheduled(initialDelay = 1000, fixedDelay = 3*60*1000)
    public void updateCorpData(){
        //查询所有企业
        corpDataServiceImpl.updateCorpDate();
    }
}
