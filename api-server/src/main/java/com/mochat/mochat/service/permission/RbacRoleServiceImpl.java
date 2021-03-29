package com.mochat.mochat.service.permission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.UserEntity;
import com.mochat.mochat.dao.entity.WorkDeptEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.permission.McRbacMenuEntity;
import com.mochat.mochat.dao.entity.permission.McRbacRoleEntity;
import com.mochat.mochat.dao.entity.permission.McRbacRoleMenuEntity;
import com.mochat.mochat.dao.entity.permission.McRbacUserRoleEntity;
import com.mochat.mochat.dao.mapper.permission.RbacRoleMapper;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkDeptService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.ISubSystemService;
import com.mochat.mochat.model.permission.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RbacRoleServiceImpl extends ServiceImpl<RbacRoleMapper, McRbacRoleEntity> implements IRbacRoleService {

    @Autowired
    private ISubSystemService subSystemService;

    @Autowired
    private IRbacMenuService menuService;

    @Autowired
    private IRbacUserRoleService userRoleService;

    @Autowired
    private IRbacRoleMenuService roleMenuService;

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private IWorkDeptService deptService;

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 6:07 下午
     * @description 角色下拉列表
     */
    @Override
    public List<RoleItemVO> roleList() {
        List<RoleItemVO> voList = new ArrayList<>();

        int tenantId = AccountService.getTenantId();

        List<McRbacRoleEntity> roleEntityList = lambdaQuery()
                .eq(McRbacRoleEntity::getTenantId, tenantId)
                .list();
        for (McRbacRoleEntity entity : roleEntityList) {
            RoleItemVO vo = new RoleItemVO();
            vo.setRoleId(entity.getId());
            vo.setName(entity.getName());
            voList.add(vo);
        }
        return voList;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 6:24 下午
     * @description 角色修改
     */
    @Override
    public void updateRole(RoleUpdateDTO req) {
        McRbacRoleEntity entity = getById(req.getRoleId());
        if (Objects.isNull(entity)) {
            throw new ParamException("角色不存在");
        }

        entity.setName(req.getName());
        entity.setRemarks(req.getRemarks());

        int corpId = AccountService.getCorpId();

        String dataPermission = entity.getDataPermission();
        if (Objects.nonNull(dataPermission)) {
            JSONArray jsonArray = JSON.parseArray(dataPermission);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getIntValue("corpId") == corpId) {
                    jsonObject.put("permissionType", req.getDataPermission() == 1 ? 1 : 2);
                    break;
                }
            }
            entity.setDataPermission(jsonArray.toJSONString());
        }
        entity.updateById();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 3:28 下午
     * @description 创建角色
     */
    @Override
    public void storeRole(RoleStoreDTO req) {
        McRbacRoleEntity roleEntity = new McRbacRoleEntity();
        roleEntity.setName(req.getName());
        roleEntity.setRemarks(req.getRemarks());
        roleEntity.setTenantId(AccountService.getTenantId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("corpId", AccountService.getCorpId());
        jsonObject.put("permissionType", req.getDataPermission());
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);

        roleEntity.setDataPermission(jsonArray.toJSONString());

        updateRoleOperate(roleEntity);

        roleEntity.insert();

        // 拷贝角色相关菜单权限
        Integer roleId = req.getRoleId();
        if (Objects.nonNull(roleId)) {
            List<McRbacRoleMenuEntity> roleMenuEntityList = roleMenuService.lambdaQuery()
                    .eq(McRbacRoleMenuEntity::getRoleId, req.getRoleId())
                    .list();
            for (McRbacRoleMenuEntity roleMenuEntity : roleMenuEntityList) {
                roleMenuEntity.setId(null);
                roleMenuEntity.setRoleId(roleEntity.getId());
                roleMenuEntity.insert();
            }
        }
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 3:29 下午
     * @description 修改角色启用状态
     */
    @Override
    public void updateRoleStatus(Integer roleId, Integer status) {
        McRbacRoleEntity entity = getById(roleId);
        if (Objects.isNull(entity)) {
            throw new ParamException("角色不存在");
        }

        entity.setStatus(status);
        entity.updateById();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 3:33 下午
     * @description 角色详情
     */
    @Override
    public RoleShowVO showRole(Integer roleId) {
        McRbacRoleEntity entity = getById(roleId);
        if (Objects.isNull(entity)) {
            throw new ParamException("角色不存在");
        }

        RoleShowVO vo = new RoleShowVO();
        vo.setRoleId(entity.getId());
        vo.setName(entity.getName());
        vo.setRemarks(entity.getRemarks());

        int corpId = AccountService.getCorpId();
        String dataPermission = entity.getDataPermission();
        if (Objects.nonNull(dataPermission)) {
            JSONArray jsonArray = JSON.parseArray(dataPermission);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getIntValue("corpId") == corpId) {
                    vo.setDataPermission((Integer) jsonObject.getOrDefault("permissionType", 1));
                    break;
                }
            }
        }
        return vo;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/11 10:47 上午
     * @description 更新角色操作人信息
     */
    private void updateRoleOperate(McRbacRoleEntity entity) {
        int userId = AccountService.getUserId();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity = userEntity.selectById();
        if (Objects.isNull(entity)) {
            throw new CommonException(RespErrCodeEnum.AUTH_FAILED);
        }
        entity.setTenantId(entity.getTenantId());
        entity.setOperateId(userEntity.getId());
        entity.setOperateName(userEntity.getName());
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 9:40 上午
     * @description 角色列表
     */
    @Override
    public Page<RolePageItemVO> roleListPage(String name, RequestPage page) {
        int userId = AccountService.getUserId();
        UserEntity userEntity = subSystemService.getById(userId);

        Page<McRbacRoleEntity> roleEntityPage = ApiRespUtils.initPage(page);
        LambdaQueryChainWrapper<McRbacRoleEntity> wrapper = lambdaQuery();
        wrapper.eq(McRbacRoleEntity::getTenantId, userEntity.getTenantId());
        if (Objects.nonNull(name) && !name.isEmpty()) {
            wrapper.like(McRbacRoleEntity::getName, name);
        }
        wrapper.page(roleEntityPage);

        List<RolePageItemVO> voList = new ArrayList<>();
        for (McRbacRoleEntity entity : roleEntityPage.getRecords()) {
            RolePageItemVO vo = new RolePageItemVO();
            entityToPageItemVo(entity, vo);
            voList.add(vo);
        }

        return ApiRespUtils.transPage(roleEntityPage, voList);
    }

    private void entityToPageItemVo(McRbacRoleEntity entity, RolePageItemVO vo) {
        int corpId = AccountService.getCorpId();

        vo.setRoleId(entity.getId());
        vo.setName(entity.getName());
        vo.setRemarks(entity.getRemarks());
        vo.setUpdatedAt(DateUtils.formatS1(entity.getUpdatedAt().getTime()));
        vo.setStatus(entity.getStatus());

        int sum = 0;

        List<McRbacUserRoleEntity> userRoleEntityList = userRoleService.lambdaQuery()
                .select(McRbacUserRoleEntity::getUserId)
                .eq(McRbacUserRoleEntity::getRoleId, entity.getId())
                .list();
        for (McRbacUserRoleEntity userRoleEntity : userRoleEntityList) {
            int count = employeeService.lambdaQuery()
                    .eq(WorkEmployeeEntity::getLogUserId, userRoleEntity.getUserId())
                    .eq(WorkEmployeeEntity::getCorpId, corpId)
                    .count();
            sum += count;
        }
        vo.setEmployeeNum(sum);
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 10:56 上午
     * @description 角色权限保存
     */
    @Override
    public void updateRoleMenu(Integer roleId, List<Integer> menuIds) {
        int count = lambdaQuery().eq(McRbacRoleEntity::getId, roleId).count();
        if (count <= 0) {
            throw new ParamException("角色不存在");
        }

        int menuCount = menuService.lambdaQuery()
                .in(McRbacMenuEntity::getId, menuIds)
                .count();
        if (menuIds.size() != menuCount) {
            throw new ParamException("菜单 id 数组有误");
        }

        List<Integer> menuIdList = roleMenuService.lambdaQuery()
                .select(McRbacRoleMenuEntity::getId)
                .eq(McRbacRoleMenuEntity::getRoleId, roleId)
                .list()
                .stream()
                .map(McRbacRoleMenuEntity::getId)
                .collect(Collectors.toList());
        roleMenuService.removeByIds(menuIdList);

        List<McRbacRoleMenuEntity> roleMenuEntityList = new ArrayList<>();
        for (Integer menuId : menuIds) {
            McRbacRoleMenuEntity entity = new McRbacRoleMenuEntity();
            entity.setRoleId(roleId);
            entity.setMenuId(menuId);
            roleMenuEntityList.add(entity);
        }
        roleMenuService.saveBatch(roleMenuEntityList);
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/12 5:08 下午
     * @description 角色查看人员
     */
    @Override
    public Page<RoleEmpShowVO> showRoleEmp(Integer roleId, RequestPage page) {
        Page<WorkEmployeeEntity> employeeEntityPage = ApiRespUtils.initPage(page);

        List<Integer> userIdList = userRoleService.lambdaQuery()
                .select(McRbacUserRoleEntity::getUserId)
                .eq(McRbacUserRoleEntity::getRoleId, roleId)
                .list()
                .stream()
                .map(McRbacUserRoleEntity::getUserId)
                .collect(Collectors.toList());

         employeeService.lambdaQuery()
                .eq(WorkEmployeeEntity::getCorpId, AccountService.getCorpId())
                .in(WorkEmployeeEntity::getLogUserId, userIdList)
                .page(employeeEntityPage);

        List<WorkEmployeeEntity> employeeEntityList = employeeEntityPage.getRecords();
        List<RoleEmpShowVO> voList = new ArrayList<>();
        for (WorkEmployeeEntity employeeEntity : employeeEntityList) {
            RoleEmpShowVO vo = new RoleEmpShowVO();
            if (Objects.isNull(employeeEntity)) {
                log.error("角色 id:" + roleId + " 对应员工 id 有误");
                continue;
            }
            vo.setEmployeeId(employeeEntity.getId());
            vo.setEmployeeName(employeeEntity.getName());
            vo.setPhone(employeeEntity.getMobile());
            vo.setEmail(employeeEntity.getEmail());
            WorkDeptEntity deptEntity = deptService.lambdaQuery()
                    .select(WorkDeptEntity::getName)
                    .eq(WorkDeptEntity::getId, employeeEntity.getMainDepartmentId())
                    .one();
            if (Objects.nonNull(deptEntity)) {
                vo.setDepartment(deptEntity.getName());
            } else {
                log.error("员工:" + employeeEntity.toString() + " 对应主部门 id 有误");
            }
            voList.add(vo);
        }
        return ApiRespUtils.transPage(employeeEntityPage, voList);
    }
}
