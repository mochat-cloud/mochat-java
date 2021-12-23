package com.mochat.mochat.service.emp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.dao.entity.WorkEmployeeDepartmentEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.model.dept.DeptPageItemVO;
import com.mochat.mochat.model.dept.ReqDeptPageDTO;

import java.util.List;
import java.util.Map;

/**
 * @author zhaojinjian
 * @ClassName IWorkEmployeeDepartmentService.java
 * @Description TODO
 * @createTime 2021/1/7 13:10
 */
public interface IWorkEmployeeDepartmentService extends IService<WorkEmployeeDepartmentEntity> {

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 10:52 上午
     * @description 根据部门 id 列表获取与部门关联的员工与部门数据
     */
    List<WorkEmployeeDepartmentEntity> getDeptEmployeeList(List<Integer> deptIds);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 10:55 上午
     * @description 获取员工所在部门与子部门所有的成员 id 列表
     */
    List<Integer> getDeptAndChildDeptEmpIdList();

    List<WorkEmployeeDepartmentEntity> getWorkEmployeeDepartmentsByEmployeeIds(List<String> employeeIdList, String clStr);

    Page<WorkEmployeeEntity> handleShowEmpData(String departmentId, ReqPageDto page);

    Page<DeptPageItemVO> handlePageIndexData(ReqDeptPageDTO req);
    List<WorkEmployeeDepartmentEntity>  getDeptIdByEmpId(Integer id);
}
