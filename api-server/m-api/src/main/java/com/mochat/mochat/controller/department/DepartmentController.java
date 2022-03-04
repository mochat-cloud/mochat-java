package com.mochat.mochat.controller.department;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.WorkDeptEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeDepartmentEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.dao.entity.permission.McRbacRoleEntity;
import com.mochat.mochat.dao.entity.permission.McRbacUserRoleEntity;
import com.mochat.mochat.model.dept.ReqDeptPageDTO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkDeptService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.permission.IRbacRoleService;
import com.mochat.mochat.service.permission.IRbacUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaojinjian
 * @ClassName DepartmentController.java
 * @Description 企业部门管理控制器
 * @createTime 2021/1/7 11:41
 */
@RestController
@Valid
public class DepartmentController {
    @Autowired
    private IWorkDeptService deptService;

    @Autowired
    private IWorkEmployeeService workEmployeeService;

    @Autowired
    private IWorkEmployeeDepartmentService workEmployeeDepartmentService;

    @Autowired
    private IWorkDeptService workDeptService;

    @Autowired
    private IRbacUserRoleService rbacUserRoleService;

    @Autowired
    private IRbacRoleService rbacRoleService;


    /**
     * @description 部门管理 - 部门列表
     * @author zhaojinjian
     * @createTime 2021/1/7 13:38
     */
    @GetMapping("/workDepartment/index")
    public ApiRespVO getDeptList(String searchKeyWords) {
        Integer corpId = AccountService.getCorpId();
        if (corpId == null) {
            throw new CommonException(100013, "未选择登录企业，不可操作");
        }
        return ApiRespUtils.ok(deptService.getWorkDepartments(searchKeyWords, corpId));
    }

    /**
     * @description 部门管理 - 部门下的成员列表
     * @author zhaojinjian
     * @createTime 2021/1/7 13:37
     */
    @GetMapping("/workEmployeeDepartment/memberIndex")
    public ApiRespVO getDeptMemberList(String departmentIds) {
        Integer corpId = AccountService.getCorpId();
        String[] deptIds = departmentIds.split(",");
        List<Integer> deptIdList = new ArrayList<>();
        for (int i = 0; i < deptIds.length; i++) {
            deptIdList.add(Integer.parseInt(deptIds[i]));
        }
        return ApiRespUtils.ok(deptService.getDeptMemberList(deptIdList, corpId));
    }

    /**
     * @description:部门管理 - 根据手机号匹配成员部门下拉列表
     * @author: Huayu
     * @time: 2021/2/7 15:56
     */
    @GetMapping("/workDepartment/selectByPhone")
    public ApiRespVO selectDeptListByPhone(
            @NotNull(message = "手机号不能为空") String phone,
            String type
    ) {
        if (type == null || type.equals("")) {
            type = "2";
        }
        //根据手机号查询成员通讯录
        String clStr = "id,corp_id";
        List<WorkEmployeeEntity> workEmployeeEntityList = workEmployeeService.getWorkEmployeesByMobile(phone, clStr);
        if (workEmployeeEntityList == null || workEmployeeEntityList.size() == 0) {
            return ApiRespUtils.ok("");
        }
        List<String> employeeIdList = new ArrayList<String>();
        for (WorkEmployeeEntity workEmployeeEntity :
                workEmployeeEntityList) {
            if (workEmployeeEntity.getCorpId().equals(AccountService.getCorpId())) {
                employeeIdList.add(workEmployeeEntity.getId().toString());
            }
        }
        if (employeeIdList == null || employeeIdList.size() == 0) {
            return ApiRespUtils.ok("");
        }
        //查询成员-部门关联表
        clStr = "id,department_id";
        List<WorkEmployeeDepartmentEntity> workEmployeeDepartmentEntityList = workEmployeeDepartmentService.getWorkEmployeeDepartmentsByEmployeeIds(employeeIdList, clStr);
        if (workEmployeeDepartmentEntityList == null || workEmployeeDepartmentEntityList.size() == 0) {
            return ApiRespUtils.ok("");
        }
        StringBuilder sb = new StringBuilder();
        String departmentId = null;
        //查询部门信息
        for (WorkEmployeeDepartmentEntity workEmployeeDepartmentEntity :
                workEmployeeDepartmentEntityList) {
            departmentId = workEmployeeDepartmentEntity.getDepartmentId().toString();
            sb = sb.append(departmentId).append(",");
        }
        String ids = sb.substring(0, sb.length() - 1);
        //查询部门信息
        clStr = "id,corp_id,name";
        List<WorkDeptEntity> workDeptEntityList = workDeptService.getWorkDepartmentsById(ids, clStr);
        if (workDeptEntityList == null || workDeptEntityList.size() == 0) {
            return ApiRespUtils.ok("");
        }
        List<Map<String, Object>> mapList = new ArrayList();
        Map<String, Object> mapData = null;
        for (WorkDeptEntity workDeptEntity :
                workDeptEntityList) {
            mapData = new HashMap<String, Object>();
            mapData.put("corpId", workDeptEntity.getCorpId());
            mapData.put("workDepartmentId", workDeptEntity.getId());
            mapData.put("workDepartmentName", workDeptEntity.getName());
            mapList.add(mapData);
        }
        return ApiRespUtils.ok(mapList);
    }


    /**
     * @description: 组织管理 - 查看人员列表
     * @author: Huayu
     * @time: 2021/2/9 14:14
     */
    @GetMapping("/workDepartment/showEmployee")
    public ApiRespVO showEmployee(String departmentId, ReqPageDto page) {
        Page<WorkEmployeeEntity> empPage = workEmployeeDepartmentService.handleShowEmpData(departmentId, page);
        List<WorkEmployeeEntity> empList = empPage.getRecords();

        if (empList.isEmpty()) {
            return ApiRespUtils.okPage(ApiRespUtils.initPage(page));
        }

        Map<Integer, Integer> empIdUserIdMap = empList.stream().collect(Collectors.toMap(WorkEmployeeEntity::getId, WorkEmployeeEntity::getLogUserId));
        Collection<Integer> UserIdList = empIdUserIdMap.values();

        LambdaQueryChainWrapper<McRbacUserRoleEntity> mcRbacUserRoleWrapper = rbacUserRoleService.lambdaQuery();
        mcRbacUserRoleWrapper.select(McRbacUserRoleEntity::getRoleId, McRbacUserRoleEntity::getUserId);
        mcRbacUserRoleWrapper.in(McRbacUserRoleEntity::getUserId, UserIdList);
        Map<Integer,Integer> userIdRoleIdMap = mcRbacUserRoleWrapper.list().stream()
                .collect(Collectors.toMap(McRbacUserRoleEntity::getUserId, McRbacUserRoleEntity::getRoleId));

        Collection<Integer> roleIdList = userIdRoleIdMap.values();
        LambdaQueryChainWrapper<McRbacRoleEntity> roleWrapper = rbacRoleService.lambdaQuery();
        roleWrapper.select(McRbacRoleEntity::getId, McRbacRoleEntity::getName);
        roleWrapper.in(McRbacRoleEntity::getId, roleIdList);
        Map<Integer,String> roleIdRoleNameMap = roleWrapper.list().stream()
                .collect(Collectors.toMap(McRbacRoleEntity::getId, McRbacRoleEntity::getName));

        List<Map<String, Object>> voList = new ArrayList<>(empList.size());
        Map<String, Object> vo;
        for (WorkEmployeeEntity entity : empList) {
            Integer empId = entity.getId();
            Integer userId = empIdUserIdMap.get(empId);
            Integer roleId = userIdRoleIdMap.get(userId);
            String name = roleIdRoleNameMap.get(roleId);

            vo = new HashMap<>();
            vo.put("employeeId", entity.getId());
            vo.put("employeeName", entity.getName());
            vo.put("phone", entity.getMobile());
            vo.put("roleName", name);
            voList.add(vo);
        }

        Page<Map<String, Object>> voPage = ApiRespUtils.transPage(empPage, voList);
        return ApiRespUtils.okPage(voPage);
    }

    @GetMapping("/workDepartment/pageIndex")
    public ApiRespVO pageIndex(ReqDeptPageDTO req) {
        return ApiRespUtils.okPage(workEmployeeDepartmentService.handlePageIndexData(req));
    }

}
