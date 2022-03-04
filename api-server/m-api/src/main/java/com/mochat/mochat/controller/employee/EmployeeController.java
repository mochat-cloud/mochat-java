package com.mochat.mochat.controller.employee;

import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.job.sync.WorkEmpServiceSyncLogic;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.model.emp.EmpIndexDTO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/4 2:39 下午
 * @description 企业成员
 */
@RestController
@RequestMapping(path = "/workEmployee")
public class EmployeeController {

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private WorkEmpServiceSyncLogic workEmpServiceSyncLogic;

    /**
     * 成员 - 同步企业成员
     */
    @PutMapping("/synEmployee")
    public ApiRespVO synEmployee() {
        int corpId = AccountService.getCorpId();
        workEmpServiceSyncLogic.onSyncWxEmp(corpId);
        return ApiRespUtils.ok("");
    }

    /**
     * 成员 - 成员列表
     */
    @GetMapping("/index")
    public ApiRespVO index(EmpIndexDTO req, @RequestAttribute ReqPerEnum permission) {
        return ApiRespUtils.okPage(employeeService.index(req, permission));
    }

    /**
     * 成员 - 成员列表搜索条件数据
     */
    @GetMapping("/searchCondition")
    public ApiRespVO searchCondition() {
        return ApiRespUtils.ok(employeeService.searchCondition());
    }
}
