package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.TenantEntity;
import com.mochat.mochat.dao.mapper.TenantMapper;
import org.springframework.stereotype.Service;

/**
 * @description:租户业务
 * @author: Huayu
 * @time: 2020/11/28 10:42
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, TenantEntity> implements ITenantService{

}
