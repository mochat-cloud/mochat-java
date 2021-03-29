package com.mochat.mochat.job.sync;

import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.IWorkMsgConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:企业微信-敏感词监控
 * @author: Huayu
 * @time: 2020/12/12 11:24
 */
@Component
@EnableAsync
public class SensitiveWordsMonitorSynLogic {

    @Autowired
    private ICorpService corpServiceImpl;

    @Autowired
    private IWorkMsgConfigService msgConfigService;

    //@Async
    //@Scheduled(cron = "0 0/5 * * * ?")
    public void autoSensitiveWords(){
        //查出所有会话消息
        String clStr = "id";
        List<CorpEntity> corpEntityList = corpServiceImpl.getCorpIds(clStr);
        List<Map<String,Object>> listMap = new ArrayList();
        Map<String,Object> map = null;
        StringBuilder sb = new StringBuilder();
        for (CorpEntity corpEntity:
        corpEntityList) {
            sb.append(corpEntity.getCorpId());
            sb.append(",");
        }
        String corpIds = sb.substring(0,sb.length()-1);
        msgConfigService.getWorkMsgByCorpId(corpIds,"corp_id,msg_id");
    }

}
