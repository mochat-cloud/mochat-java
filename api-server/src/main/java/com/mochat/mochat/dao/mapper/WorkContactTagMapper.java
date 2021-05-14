package com.mochat.mochat.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.WorkContactTagEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkContactTagMapper extends BaseMapper<WorkContactTagEntity> {
    List<WorkContactTagEntity> getWorkContactTag(@Param("empId") Integer empId);
}
