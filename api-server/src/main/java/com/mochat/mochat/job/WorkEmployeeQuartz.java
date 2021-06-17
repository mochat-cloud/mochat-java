package com.mochat.mochat.job;

import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: yangpengwei
 * @time: 2020/12/11 6:25 下午
 * @description 通讯录 - 定时任务
 */
@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class WorkEmployeeQuartz {

    @Autowired
    private ICorpService corpService;

    @Autowired
    private IWorkEmployeeService employeeService;

    /**
     * cron = 秒 分钟 小时 日 月 星期 年
     * 每天晚上 0 点定时同步员工客户统计数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void onUpdateEmpStatistic() {
        List<Integer> corpEntities = corpService.lambdaQuery()
                .select(CorpEntity::getCorpId)
                .list()
                .stream()
                .map(CorpEntity::getCorpId)
                .collect(Collectors.toList());
        for (Integer corpId : corpEntities) {
            onAsyncCorpEmpStatistic(corpId);
        }
    }

    @Async
    public void onAsyncCorpEmpStatistic(Integer corpId) {
        employeeService.syncEmployeeStatistic(corpId);
    }

}
