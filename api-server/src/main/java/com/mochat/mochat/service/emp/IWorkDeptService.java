package com.mochat.mochat.service.emp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.WorkDeptEntity;

import java.util.List;

public interface IWorkDeptService extends IService<WorkDeptEntity> {

    List<WorkDeptEntity> getWorkDepartmentsByCorpId(String corpId);

    boolean insertDeptments(List<WorkDeptEntity> workDeptEntityList,int corpId);

    Integer updDeptments(List<WorkDeptEntity> workDeptEntityList);

    JSONObject getWorkDepartments(String searchKeyWords, Integer corpId);

    JSONArray getDeptMemberList(List<Integer> deptIds, Integer corpId);

    List<WorkDeptEntity> getWorkDepartmentsById(String ids, String clStr);

    List<WorkDeptEntity> getWorkDepartmentsByCorpIdName(Integer corpId, String name, String clStr);

    List<WorkDeptEntity> getWorkDepartmentsByCorpIdPath(Integer corpId, String path, String id);

    List<WorkDeptEntity> getWorkDepartmentsBySearch(String substring, String clStr);

    List<WorkDeptEntity> getWorkDepartmentsByPath(int level, String path);

    /**
     * 根据部门 id 列表获取部门企业 id 列表
     */
    List<Integer> getWxDeptIdListByDeptIdList(List<Integer> deptIdList);

    List<WorkDeptEntity> getNameById(String departmentIds, String name);
}
