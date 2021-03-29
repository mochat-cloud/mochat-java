package com.mochat.mochat.service.impl;

import com.mochat.mochat.dao.entity.SysTestEntity;
import com.mochat.mochat.dao.mapper.SysTestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Andy
 * @time: 2020/11/13 10:30
 */
@Service
public class SysTestServiceImpl implements ISysTestService {

    @Autowired
    private SysTestMapper sysTestMapper;

    @Override
    public SysTestEntity getUserNumber() {
        return sysTestMapper.getUserNumber();
    }
}
