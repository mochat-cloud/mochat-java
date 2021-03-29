package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.TenantEntity;
import com.mochat.mochat.dao.mapper.TenantMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description:租户业务
 * @author: Huayu
 * @time: 2020/11/28 10:42
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, TenantEntity> implements ITenantService{


    @Override
    public List<Map<String,Object>> getTenantByStatus() {
        QueryWrapper<TenantEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("url");
        QueryWrapper.eq("status","1");
        List<Map<String,Object>> corpList = this.baseMapper.selectMaps(QueryWrapper);
        return corpList;
    }
}
