package com.mochat.mochat.service.permission;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.permission.McRbacUserRoleEntity;
import com.mochat.mochat.dao.mapper.permission.RbacUserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RbacUserRoleServiceImpl extends ServiceImpl<RbacUserRoleMapper, McRbacUserRoleEntity> implements IRbacUserRoleService {
}
