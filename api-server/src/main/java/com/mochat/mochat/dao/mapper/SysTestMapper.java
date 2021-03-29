package com.mochat.mochat.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;;
import com.mochat.mochat.dao.entity.SysTestEntity;

public interface SysTestMapper extends BaseMapper<SysTestEntity> {
    public SysTestEntity getUserNumber();
}
