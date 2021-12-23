package com.mochat.mochat.service.permission;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.UserEntity;
import com.mochat.mochat.dao.entity.permission.McRbacMenuEntity;
import com.mochat.mochat.dao.entity.permission.McRbacRoleMenuEntity;
import com.mochat.mochat.dao.entity.permission.McRbacUserRoleEntity;
import com.mochat.mochat.dao.mapper.permission.RbacMenuMapper;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.model.permission.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RbacMenuServiceImpl extends ServiceImpl<RbacMenuMapper, McRbacMenuEntity> implements IRbacMenuService {

    @Autowired
    private IRbacUserRoleService userRoleService;

    @Autowired
    private IRbacRoleMenuService roleMenuService;

    /**
     * @param req
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:19 上午
     * @description 菜单管理 - 修改菜单
     */
    @Override
    public void updateMenu(MenuUpdateDTO req) {
        McRbacMenuEntity entity = getById(req.getMenuId());
        if (Objects.isNull(entity)) {
            throw new ParamException("菜单 id 无效");
        }

        updateMenuOperate(entity);

        entity.setName(req.getName());
        entity.setIcon(req.getIcon());
        entity.setIsPageMenu(req.getIsPageMenu());
        entity.setLinkUrl(req.getLinkUrl());
        entity.setLinkType(req.getLinkType());
        entity.setDataPermission(req.getDataPermission());
        entity.updateById();
    }

    @Override
    public void storeMenu(MenuStoreDTO req) {
        StringBuilder sb = new StringBuilder();
        sb.append(req.getFirstMenuId() == null ? "" : "-#" + req.getFirstMenuId() + "#");
        sb.append(req.getSecondMenuId() == null ? "" : "-#" + req.getFirstMenuId() + "#");
        sb.append(req.getThirdMenuId() == null ? "" : "-#" + req.getFirstMenuId() + "#");
        sb.append(req.getFourthMenuId() == null ? "" : "-#" + req.getFirstMenuId() + "#");
        String path = sb.substring(1);

        McRbacMenuEntity entity = new McRbacMenuEntity();
        updateMenuOperate(entity);
        entity.setParentId(req.getParentMenuId());
        entity.setName(req.getName());
        entity.setLevel(req.getLevel());
        entity.setPath(path);
        entity.setIcon(req.getIcon());
        entity.setLinkType(req.getLinkType());
        entity.setIsPageMenu(req.getIsPageMenu());
        entity.setLinkUrl(req.getLinkUrl());
        entity.setDataPermission(req.getDataPermission());
        entity.insert();

        entity.setPath(entity.getPath() + "-#" + entity.getId() + "#");
        entity.updateById();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/11 10:47 上午
     * @description 更新菜单操作人信息
     */
    private void updateMenuOperate(McRbacMenuEntity entity) {
        int userId = AccountService.getUserId();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity = userEntity.selectById();
        if (Objects.isNull(entity)) {
            throw new CommonException(RespErrCodeEnum.AUTH_FAILED);
        }
        entity.setOperateId(userEntity.getId());
        entity.setOperateName(userEntity.getName());
    }

    @Override
    public Page<MenuPageItemVO> menuListPage(String name, ReqPageDto page) {
        Page<McRbacMenuEntity> menuEntityPage = ApiRespUtils.initPage(page);
        LambdaQueryChainWrapper<McRbacMenuEntity> wrapper = lambdaQuery();
        wrapper.select(McRbacMenuEntity::getId);
        wrapper.eq(McRbacMenuEntity::getLevel, 1);
        if (Objects.nonNull(name) && !name.isEmpty()) {
            wrapper.like(McRbacMenuEntity::getName, name);
        }
        wrapper.page(menuEntityPage);

        wrapper = lambdaQuery();
        wrapper.orderByAsc(McRbacMenuEntity::getLevel);
        List<Integer> menuIdList = menuEntityPage.getRecords().stream()
                .map(McRbacMenuEntity::getId)
                .collect(Collectors.toList());
        int size = menuIdList.size();
        for (int i = 0; i < size; i++) {
            wrapper.like(McRbacMenuEntity::getPath, menuIdList.get(i));
            if (i != size - 1) {
                wrapper.or();
            }
        }

        List<MenuPageItemVO> voList = entityListToPageItemVoList(wrapper.list());

        return ApiRespUtils.transPage(menuEntityPage, voList);
    }

    private List<MenuPageItemVO> entityListToPageItemVoList(List<McRbacMenuEntity> entityList) {
        Map<Integer, MenuPageItemVO> mapParent = new HashMap<>();
        Map<Integer, MenuPageItemVO> mapChild = new HashMap<>();
        int level = 1;
        List<MenuPageItemVO> voList = new ArrayList<>();
        for (McRbacMenuEntity entity : entityList) {
            MenuPageItemVO vo = new MenuPageItemVO();
            entityToPageItemVo(entity, vo);

            if (entity.getLevel() == 1) {
                voList.add(vo);
                mapParent.put(entity.getId(), vo);
            }

            if (entity.getLevel() - 1 > level) {
                mapParent.clear();
                mapParent.putAll(mapChild);
                mapChild.clear();
                level ++;
            }

            if (entity.getLevel() > level) {
                mapChild.put(entity.getId(), vo);
            }

            if (entity.getLevel() > 1) {
                MenuPageItemVO voParent = mapParent.get(entity.getParentId());
                if (Objects.nonNull(voParent)) {
                    voParent.getChildren().add(vo);
                }
            }
        }
        return voList;
    }

    private void entityToPageItemVo(McRbacMenuEntity entity, MenuPageItemVO vo) {
        vo.setMenuPath(entity.getPath().replaceAll("#", ""));
        vo.setMenuId(entity.getId());
        vo.setParentId(entity.getParentId());
        vo.setName(entity.getName());
        vo.setLevel(entity.getLevel());
        vo.setLevelName(getLevelName(entity.getLevel()));
        vo.setIcon(entity.getIcon());
        vo.setStatus(entity.getStatus());
        vo.setOperateName(entity.getOperateName());
        vo.setUpdatedAt(DateUtils.formatS1(entity.getUpdatedAt().getTime()));
        vo.setChildren(new ArrayList<>());
    }

    private String getLevelName(int level) {
        switch (level) {
            case 1:
                return "一级菜单";
            case 2:
                return "二级菜单";
            case 3:
                return "三级菜单";
            case 4:
                return "四级菜单";
            case 5:
                return "五级菜单";
            default:
                return "";
        }
    }

    @Override
    public MenuShowVO showMenu(Integer menuId) {
        McRbacMenuEntity entity = getById(menuId);
        if (Objects.isNull(entity)) {
            throw new ParamException("菜单不存在");
        }

        MenuShowVO vo = new MenuShowVO();
        vo.setMenuId(entity.getId());

        String[] menuIds = entity.getPath().split("-");
        if (menuIds.length >= MenuConst.LEVEL_FIRST) {
            vo.setFirstMenuId(pathToId(menuIds[0]));
        }
        if (menuIds.length >= MenuConst.LEVEL_SECOND) {
            vo.setSecondMenuId(pathToId(menuIds[1]));
        }
        if (menuIds.length >= MenuConst.LEVEL_THIRD) {
            vo.setThirdMenuId(pathToId(menuIds[2]));
        }
        if (menuIds.length >= MenuConst.LEVEL_FOURTH) {
            vo.setFourthMenuId(pathToId(menuIds[3]));
        }

        vo.setLevel(entity.getLevel());
        vo.setName(entity.getName());
        vo.setIcon(entity.getIcon());
        vo.setIsPageMenu(entity.getIsPageMenu());
        vo.setLinkUrl(entity.getLinkUrl());
        vo.setLinkType(entity.getLinkType());
        vo.setDataPermission(entity.getDataPermission());
        vo.setStatus(entity.getStatus() == 1 ? "启用" : "禁用");

        return vo;
    }

    private String pathToId(String path) {
        return path.replaceAll("#", "");
    }

    @Override
    public void updateMenuStatus(Integer menuId, Integer status) {
        McRbacMenuEntity entity = getById(menuId);
        if (Objects.isNull(entity)) {
            throw new ParamException("菜单 id 无效");
        }

        entity.setStatus(status);
        entity.updateById();
    }

    @Override
    public List<String> menuIconList() {
        List<McRbacMenuEntity> entityList = list();
        List<String> list = entityList.stream()
                .filter(entity -> !entity.getIcon().isEmpty())
                .map(McRbacMenuEntity::getIcon)
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public List<MenuItemVO> menuList() {
        List<McRbacMenuEntity> menuEntityList = lambdaQuery()
                .orderByAsc(McRbacMenuEntity::getLevel)
                .eq(McRbacMenuEntity::getStatus, 1)
                .list();
        return entityListToItemVoList(menuEntityList);
    }

    private List<MenuItemVO> entityListToItemVoList(List<McRbacMenuEntity> entityList) {
        Map<Integer, MenuItemVO> mapParent = new HashMap<>();
        Map<Integer, MenuItemVO> mapChild = new HashMap<>();
        int level = 1;
        List<MenuItemVO> voList = new ArrayList<>();
        for (McRbacMenuEntity entity : entityList) {
            MenuItemVO vo = new MenuItemVO();
            entityToItemVo(entity, vo);

            if (entity.getLevel() == 1) {
                voList.add(vo);
                mapParent.put(entity.getId(), vo);
            }

            if (entity.getLevel() - 1 > level) {
                mapParent.clear();
                mapParent.putAll(mapChild);
                mapChild.clear();
                level ++;
            }

            if (entity.getLevel() > level) {
                mapChild.put(entity.getId(), vo);
            }

            if (entity.getLevel() > 1) {
                MenuItemVO voParent = mapParent.get(entity.getParentId());
                if (Objects.nonNull(voParent)) {
                    voParent.getChildren().add(vo);
                }
            }
        }
        return voList;
    }

    private void entityToItemVo(McRbacMenuEntity entity, MenuItemVO vo) {
        vo.setMenuId(entity.getId());
        vo.setName(entity.getName());
        vo.setLevel(entity.getLevel());
        vo.setDataPermission(entity.getDataPermission());
        vo.setParentId(entity.getParentId());
        vo.setChildren(new ArrayList<>());
    }

    @Override
    public List<UserMenuItemVO> menuListByUserId() {
        int userId = AccountService.getUserId();
        UserEntity userEntity = new UserEntity();
        userEntity = userEntity.selectById(userId);

        List<McRbacMenuEntity> menuEntityList;
        if (1 == userEntity.getIsSuperAdmin()) {
            menuEntityList = lambdaQuery()
                    .orderByAsc(McRbacMenuEntity::getLevel)
                    .eq(McRbacMenuEntity::getStatus, 1)
                    .list();
        } else {
            List<McRbacUserRoleEntity> userRoleEntityList = userRoleService.lambdaQuery()
                    .select(McRbacUserRoleEntity::getRoleId)
                    .eq(McRbacUserRoleEntity::getUserId, userId)
                    .list();

            List<Integer> roleIdList = userRoleEntityList.stream()
                    .map(McRbacUserRoleEntity::getRoleId)
                    .collect(Collectors.toList());
            List<McRbacRoleMenuEntity> roleMenuEntityList = roleMenuService.lambdaQuery()
                    .select(McRbacRoleMenuEntity::getMenuId)
                    .in(McRbacRoleMenuEntity::getRoleId, roleIdList)
                    .groupBy(McRbacRoleMenuEntity::getMenuId)
                    .list();

            List<Integer> menuIdList = roleMenuEntityList.stream()
                    .map(McRbacRoleMenuEntity::getMenuId)
                    .collect(Collectors.toList());
            menuEntityList = lambdaQuery()
                    .in(McRbacMenuEntity::getId, menuIdList)
                    .orderByAsc(McRbacMenuEntity::getLevel)
                    .eq(McRbacMenuEntity::getStatus, 1)
                    .list();
        }

        return entityListToUserItemVoList(menuEntityList);
    }

    private List<UserMenuItemVO> entityListToUserItemVoList(List<McRbacMenuEntity> entityList) {
        Map<Integer, UserMenuItemVO> mapParent = new HashMap<>();
        Map<Integer, UserMenuItemVO> mapChild = new HashMap<>();
        int level = 1;
        List<UserMenuItemVO> voList = new ArrayList<>();
        for (McRbacMenuEntity entity : entityList) {
            UserMenuItemVO vo = new UserMenuItemVO();
            entityToUserItemVo(entity, vo);

            if (entity.getLevel() == 1) {
                voList.add(vo);
                mapParent.put(entity.getId(), vo);
            }

            if (entity.getLevel() - 1 > level) {
                mapParent.clear();
                mapParent.putAll(mapChild);
                mapChild.clear();
                level ++;
            }

            if (entity.getLevel() > level) {
                mapChild.put(entity.getId(), vo);
            }

            if (entity.getLevel() > 1) {
                UserMenuItemVO voParent = mapParent.get(entity.getParentId());
                if (Objects.nonNull(voParent)) {
                    voParent.getChildren().add(vo);
                }
            }
        }
        return voList;
    }

    private void entityToUserItemVo(McRbacMenuEntity entity, UserMenuItemVO vo) {
        vo.setMenuId(entity.getId());
        vo.setName(entity.getName());
        vo.setLevel(entity.getLevel());
        vo.setDataPermission(entity.getDataPermission());
        vo.setIcon(entity.getIcon());
        vo.setLinkUrl(entity.getLinkUrl());
        vo.setLinkType(entity.getLinkType());
        vo.setParentId(entity.getParentId());
        vo.setChildren(new ArrayList<>());
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/12 4:24 下午
     * @description 角色权限查看
     */
    @Override
    public List<RoleMenuShowVO> showRoleMenu(Integer roleId) {
        List<RoleMenuShowVO> voList = new ArrayList<>();

        List<McRbacRoleMenuEntity> roleMenuEntityList = roleMenuService.lambdaQuery()
                .select(McRbacRoleMenuEntity::getMenuId)
                .eq(McRbacRoleMenuEntity::getRoleId, roleId)
                .list();

        List<McRbacMenuEntity> menuEntityList = lambdaQuery()
                .orderByAsc(McRbacMenuEntity::getLevel)
                .eq(McRbacMenuEntity::getStatus, 1)
                .list();
        voList.addAll(entityListToRoleItemVoList(menuEntityList));

        List<Integer> menuIdList = roleMenuEntityList.stream()
                .map(McRbacRoleMenuEntity::getMenuId)
                .collect(Collectors.toList());

        for (RoleMenuShowVO vo : voList) {
            setRoleMenuCheck(vo, menuIdList);
        }

        return voList;
    }

    private List<RoleMenuShowVO> entityListToRoleItemVoList(List<McRbacMenuEntity> entityList) {
        Map<Integer, RoleMenuShowVO> mapParent = new HashMap<>();
        Map<Integer, RoleMenuShowVO> mapChild = new HashMap<>();
        int level = 1;
        List<RoleMenuShowVO> voList = new ArrayList<>();
        for (McRbacMenuEntity entity : entityList) {
            RoleMenuShowVO vo = new RoleMenuShowVO();
            entityToRoleItemVo(entity, vo);

            if (entity.getLevel() == 1) {
                voList.add(vo);
                mapParent.put(entity.getId(), vo);
            }

            if (entity.getLevel() - 1 > level) {
                mapParent.clear();
                mapParent.putAll(mapChild);
                mapChild.clear();
                level ++;
            }

            if (entity.getLevel() > level) {
                mapChild.put(entity.getId(), vo);
            }

            if (entity.getLevel() > 1) {
                RoleMenuShowVO voParent = mapParent.get(entity.getParentId());
                if (Objects.nonNull(voParent)) {
                    voParent.getChildren().add(vo);
                }
            }
        }
        return voList;
    }

    private void entityToRoleItemVo(McRbacMenuEntity entity, RoleMenuShowVO vo) {
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setLevel(entity.getLevel());
        vo.setIsPageMenu(entity.getIsPageMenu());
        vo.setChildren(new ArrayList<>());
    }

    private void setRoleMenuCheck(RoleMenuShowVO vo, List<Integer> menuIdList) {
        List<RoleMenuShowVO> list = vo.getChildren();
        if (list.size() > 0) {
            for (RoleMenuShowVO showVO : list) {
                setRoleMenuCheck(showVO,menuIdList);
            }
            int size = list.size();
            int count = getCheckCount(list, menuIdList);
            if (count == 0) {
                vo.setChecked(1);
            } else {
                vo.setChecked(size == count ? 2 : 3);
            }
        } else {
            if (menuIdList.contains(vo.getId())) {
                vo.setChecked(2);
            } else {
                vo.setChecked(1);
            }
        }
    }

    private int getCheckCount(List<RoleMenuShowVO> list, List<Integer> menuIdList) {
        int count = 0;
        for (RoleMenuShowVO vo : list) {
            if (menuIdList.contains(vo.getId())) {
                count++;
                menuIdList.remove(vo.getId());
            }
        }
        return count;
    }

}
