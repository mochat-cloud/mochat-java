package com.mochat.mochat.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: zhaojinjian
 * @time: 2020/12/2 5:18 下午
 * @description 员工 mapper
 */
public interface WorkEmployeeMapper extends BaseMapper<WorkEmployeeEntity> {

    List<WorkEmployeeEntity> getWorkEmployeeByUserId(@Param("logUserId") String logUserId);
}
