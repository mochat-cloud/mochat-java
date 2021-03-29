package com.mochat.mochat.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.mapper.corp.CorpMapper;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/12/11 6:25 下午
 * @description 通讯录 - 定时任务
 */
@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class WorkEmployeeSynLogic {

    @Autowired
    private CorpMapper corpMapper;

    @Autowired
    private IWorkEmployeeService employeeService;

    /**
     * cron = 秒 分钟 小时 日 月 星期 年
     * 每天晚上 0 点定时同步员工客户统计数据
     */
    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void onAsyncEmpStatistic() {
        List<CorpEntity> corpEntities = corpMapper.selectList(
                new QueryWrapper<CorpEntity>()
                        .select("id")
        );
        for (CorpEntity entity : corpEntities) {
            employeeService.syncEmployeeStatistic(entity.getCorpId());
        }

    }

}
