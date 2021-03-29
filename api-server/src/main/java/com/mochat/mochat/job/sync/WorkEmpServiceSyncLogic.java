package com.mochat.mochat.job.sync;

import com.mochat.mochat.service.emp.IWorkEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @author: yangpengwei
 * @time: 2020/12/22 2:16 下午
 * @description 企业成员服务 - 同步企业微信通讯录
 */
@Component
@EnableAsync
public class WorkEmpServiceSyncLogic {

    @Autowired
    private IWorkEmployeeService employeeService;

    /**
     * 同步微信企业通讯录
     */
    @Async
    public void onSyncWxEmp(int corpId) {
        employeeService.synWxEmployee(corpId);
    }

    @Async
    public void syncDepartment(int corpId) {
        employeeService.syncDepartment(corpId);
    }

    @Async
    public void onSyncEmployee(int corpId) {
        employeeService.syncEmployee(corpId);
    }
}
