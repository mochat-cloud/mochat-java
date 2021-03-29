package com.mochat.mochat.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 子账户管理
 * @author: zhaojinjian
 * @create: 2020-11-17 17:36
 **/
public interface SubSystemMapper extends BaseMapper<UserEntity> {
    List<UserEntity> getSubSystemList();

    List<UserEntity> getUserByPhone(@Param("phone") String phone,@Param("password") String password);
}
