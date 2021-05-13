package com.mochat.mochat.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;;
import com.mochat.mochat.dao.entity.WorkContactEmployeeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkContactEmployeeMapper extends BaseMapper<WorkContactEmployeeEntity> {

    List<WorkContactEmployeeEntity>  getContactEmployee(@Param("employeeId") String employeeId,@Param("corpId") Integer corpId,@Param("page") Integer page, @Param("perPage")Integer perPage);

    List<WorkContactEmployeeEntity>  getContactEmployeeInfo(@Param("contactId") Integer contactId,@Param("employeeId") Integer employeeId,@Param("corpId") Integer corpId,@Param("status") Integer status,@Param("id") Integer id);

}
