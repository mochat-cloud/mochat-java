package com.mochat.mochat.service.emp;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.model.emp.EmpEmployeeBO;
import com.mochat.mochat.model.emp.EmpIndexDTO;
import com.mochat.mochat.model.emp.EmpSearchConditionBO;
import com.mochat.mochat.model.emp.WXEmployeeDTO;

import java.util.List;
import java.util.Map;

public interface IWorkEmployeeService extends IService<WorkEmployeeEntity> {

    /**
     * @author: yangpengwei
     * @time: 2021/5/20 4:45 下午
     * @description 通过员工企业微信 id 获取员工数据
     */
    WorkEmployeeEntity getByWxEmpId(String wxEmpId);

    void synWxEmployee(int corpId);

    Page<EmpEmployeeBO> index(EmpIndexDTO req, ReqPerEnum permission);

    EmpSearchConditionBO searchCondition();

    void insertEmployee(int corpId, WXEmployeeDTO dto);

    void updateEmpDeptIndex(int corpId, WXEmployeeDTO dto);

    void syncDepartment(int corpId);

    void syncEmployee(int corpId);

    void syncEmployeeStatistic(int corpId);

    /**
     * 查看通讯录表中是否存在当前手机号，然后绑定
     *
     * @param mobile
     * @param logUserId
     */
    void verifyEmployeeMobile(String mobile, Integer logUserId);

    WorkEmployeeEntity getWorkEmployeeInfoById(Integer empId);

    WorkEmployeeEntity getWorkEmployeeInfoByWxEmpId(String userId);

    List<WorkEmployeeEntity> getWorkEmployeeByLogUserId(Integer logUserId);

    List<WorkEmployeeEntity> getWorkEmployeeByUserId(String userId);

    String[] getEmployeeName(List<Integer> empIds);

    Map<Integer, String> getCorpEmployeeName(Integer corpId, List<Integer> empIds);

    JSONArray getCorpEmployeeName(Integer corpId, String searchKeyWords);

    Map<String, Integer> getCorpByUserId(Integer corpId);

    List<WorkEmployeeEntity> getWorkEmployeeByLogUserId(String userId, String corpId);

    List<WorkEmployeeEntity> getWorkEmployeeByCorpIdLogUserId(String corpId, String userId);

    List<WorkEmployeeEntity> getWorkEmployeesById(String ownerIdArr);

    List<WorkEmployeeEntity> getWorkEmployeesByCorpIdName(Integer corpId, String name, String clStr);

    List<WorkEmployeeEntity> getWorkEmployeesByCorpId(Integer corpId, String s);

    List<WorkEmployeeEntity> getWorkEmployeesByMobile(String phone, String clStr);

    List<WorkEmployeeEntity> getWorkEmployeeList(String page, String perPage, String clStr, String empIdArr);

    List<WorkEmployeeEntity> getWorkEmployeesByCorpIdsWxUserId(Integer corpId, List<String> participantIdArr, String s);

    /**
     * 根据员工 id 列表获取员工微信 id
     */
    List<String> getWxEmpIdListByEmpIdList(List<Integer> empIdList);

    WorkEmployeeEntity getWorkEmployeeInfoByLogId(Integer userId);

    WorkEmployeeEntity getWorkEmployeeByWxUserId(String userId, String s);

    List<WorkEmployeeEntity> countWorkEmployeesByCorpId(Integer corpId);

    /**
     * @author: yangpengwei
     * @time: 2021/5/21 10:52 上午
     * @description 根据登录用户 id 获取用户所属企业 id 列表
     */
    List<Integer> listCorpIdByLoginUserId(Integer loginUserId);
}
