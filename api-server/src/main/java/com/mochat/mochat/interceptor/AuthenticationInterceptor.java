package com.mochat.mochat.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.annotion.LoginToken;
import com.mochat.mochat.common.annotion.SkipPermission;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.util.JwtUtils;
import com.mochat.mochat.config.ex.AuthException;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.UserEntity;
import com.mochat.mochat.dao.entity.permission.McRbacMenuEntity;
import com.mochat.mochat.dao.entity.permission.McRbacRoleEntity;
import com.mochat.mochat.dao.entity.permission.McRbacRoleMenuEntity;
import com.mochat.mochat.dao.entity.permission.McRbacUserRoleEntity;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.ISubSystemService;
import com.mochat.mochat.service.permission.IRbacMenuService;
import com.mochat.mochat.service.permission.IRbacRoleMenuService;
import com.mochat.mochat.service.permission.IRbacRoleService;
import com.mochat.mochat.service.permission.IRbacUserRoleService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:配置拦截器对请求进行验证
 * @author: Huayu
 * @time: 2020/11/20 9:42
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private ISubSystemService subSystemService;

    @Autowired
    private IRbacUserRoleService userRoleService;

    @Autowired
    private IRbacRoleMenuService roleMenuService;

    @Autowired
    private IRbacRoleService roleService;

    @Autowired
    private IRbacMenuService menuService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 检查是否有LoginToken注释，有则跳过认证
        if (method.isAnnotationPresent(LoginToken.class)) {
            LoginToken loginToken = method.getAnnotation(LoginToken.class);
            if (loginToken.required()) {
                return true;
            }
        }

        // 从 http 请求头中取出 token
        String token = request.getHeader("Authorization");
        // 如果token为空的情况下，返回非法token，禁止访问
        if (!StringUtils.hasLength(token)) {
            throw new AuthException(RespErrCodeEnum.AUTH_UNAUTHORIZED.getCode(), RespErrCodeEnum.AUTH_UNAUTHORIZED.getMsg());
        } else {
            try {
                if (JwtUtils.isVerify(token)) {
                    throw new AuthException(RespErrCodeEnum.AUTH_TOKEN_INVALID.getCode(), RespErrCodeEnum.AUTH_TOKEN_INVALID.getMsg());
                } else {
                    if (AccountService.isLogin()) {
                        throw new AuthException(RespErrCodeEnum.TOKEN_INVALID.getCode(), RespErrCodeEnum.TOKEN_INVALID.getMsg());
                    }
                }
            } catch (ExpiredJwtException e) {
                throw new AuthException(RespErrCodeEnum.AUTH_SESSION_EXPIRED.getCode(), RespErrCodeEnum.AUTH_SESSION_EXPIRED.getMsg());
            }
        }

        // 检查是否有 SkipPermission 注释，有则跳过权限验证
        if (method.isAnnotationPresent(SkipPermission.class)) {
            SkipPermission loginToken = method.getAnnotation(SkipPermission.class);
            if (loginToken.required()) {
                request.setAttribute("permission", ReqPerEnum.ALL);
                return true;
            }
        }

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod().toLowerCase();
        boolean hasPer = permissionLogic(request, requestUri + "#" + requestMethod);
        if (!hasPer) {
            throw new CommonException("该用户无此菜单权限");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //response.setContentType("application/json;charset=utf-8");
    }

    /**
     * @author: yangpengwei
     * @time: 2021/5/21 11:21 上午
     * @description 验证用户是否拥有当前接口访问权限
     */
    private boolean permissionLogic(HttpServletRequest request, String urlAndMethod) {
        int userId = AccountService.getUserId();
        UserEntity userEntity = subSystemService.getById(userId);

        // 超级管理员拥有所有访问权限
        if (1 == userEntity.getIsSuperAdmin()) {
            request.setAttribute("permission", ReqPerEnum.ALL);
            return true;
        }

        // 查询用户对应角色, 根据角色判断权限
        List<McRbacUserRoleEntity> userRoleEntityList = userRoleService.lambdaQuery()
                .select(McRbacUserRoleEntity::getRoleId)
                .eq(McRbacUserRoleEntity::getUserId, userId)
                .list();
        if (userRoleEntityList.isEmpty()) {
            return false;
        }

        int corpId = AccountService.getCorpId();
        List<Map<String, Integer>> mapList = new ArrayList<>();
        for (McRbacUserRoleEntity userRoleEntity : userRoleEntityList) {
            Map<String, Integer> map = new HashMap<>(2);

            McRbacRoleEntity roleEntity = roleService.getById(userRoleEntity.getRoleId());
            // 角色对应企业的数据权限
            String dataJsonStr = roleEntity.getDataPermission();
            JSONArray jsonArray = JSON.parseArray(dataJsonStr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // 角色本企业的数据权限
                if (corpId == jsonObject.getIntValue("corpId")) {
                    // 角色是否启用数据权限
                    map.put("dp", jsonObject.getIntValue("permissionType"));
                    break;
                }
            }

            // 角色与菜单关联数据
            List<McRbacRoleMenuEntity> roleMenuEntityList = roleMenuService.lambdaQuery()
                    .select(McRbacRoleMenuEntity::getMenuId)
                    .eq(McRbacRoleMenuEntity::getRoleId, userRoleEntity.getRoleId())
                    .orderByAsc(McRbacRoleMenuEntity::getMenuId)
                    .list();

            // 角色对应菜单 id 列表
            List<Integer> menuIdList = roleMenuEntityList.stream()
                    .map(McRbacRoleMenuEntity::getMenuId)
                    .collect(Collectors.toList());
            if (!menuIdList.isEmpty()) {
                List<McRbacMenuEntity> menuEntityList = menuService.lambdaQuery()
                        .in(McRbacMenuEntity::getId, menuIdList)
                        .eq(McRbacMenuEntity::getLinkUrl, urlAndMethod)
                        .list();
                if (menuEntityList.size() > 0) {
                    // 菜单是否启用菜单权限
                    map.put("mp", menuEntityList.get(0).getDataPermission());
                }
            }
            mapList.add(map);
        }

        int departmentPermission;
        int menuPermission;
        for (Map<String, Integer> map : mapList) {
            // 数据权限 【1-启用 2不启用（查看企业下数据）】
            menuPermission = map.getOrDefault("mp", 0);
            if (menuPermission == 0) {
                // 没有菜单权限
                return false;
            }

            // 菜单不启用数据权限, 可查看全部数据
            if (menuPermission == 2) {
                request.setAttribute("permission", ReqPerEnum.ALL);
            } else {
                // 菜单启用数据权限, 判断用户数据查询范围
                // 1-是(所选择企业)本用户部门 2-否 （本用户)
                departmentPermission = map.getOrDefault("dp", 0);
                if (departmentPermission == 1) {
                    // 所属部门
                    request.setAttribute("permission", ReqPerEnum.DEPARTMENT);
                } else {
                    // 个人
                    request.setAttribute("permission", ReqPerEnum.EMPLOYEE);
                }
            }
        }

        return true;
    }

}
