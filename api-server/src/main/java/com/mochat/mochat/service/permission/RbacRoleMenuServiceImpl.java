package com.mochat.mochat.service.permission;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.permission.McRbacRoleMenuEntity;
import com.mochat.mochat.dao.mapper.permission.RbacRoleMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RbacRoleMenuServiceImpl extends ServiceImpl<RbacRoleMenuMapper, McRbacRoleMenuEntity> implements IRbacRoleMenuService {
}
