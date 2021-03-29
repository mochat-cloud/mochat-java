package com.mochat.mochat.service.emp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.WorkEmployeeStatisticEntity;
import com.mochat.mochat.dao.mapper.WorkEmployeeStatisticMapper;
import org.springframework.stereotype.Service;

@Service
public class WorkEmployeeStatisticServiceImpl extends ServiceImpl<WorkEmployeeStatisticMapper, WorkEmployeeStatisticEntity> implements IWorkEmployeeStatisticService {
}
