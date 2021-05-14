package com.mochat.mochat.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.em.user.StatusEnum;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.entity.permission.McRbacRoleEntity;
import com.mochat.mochat.dao.entity.permission.McRbacUserRoleEntity;
import com.mochat.mochat.dao.mapper.SubSystemMapper;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkDeptService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.permission.IRbacRoleService;
import com.mochat.mochat.service.permission.IRbacUserRoleService;
import com.mochat.mochat.model.subsystem.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @description: 子账户业务实现类
 * @author: zhaojinjian
 * @create: 2020-11-17 17:41
 **/
@Service
public class SubsystemServiceImpl extends ServiceImpl<SubSystemMapper, UserEntity> implements ISubSystemService {

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    @Autowired
    private IWorkDeptService deptService;

    @Autowired
    private ICorpService corpService;

    @Autowired
    private IRbacRoleService roleService;

    @Autowired
    private IRbacUserRoleService userRoleService;

    @Autowired
    private IWorkEmployeeService workEmployeeServiceImpl;

    @Autowired
    private IWorkEmployeeDepartmentService workEmployeeDepartmentServiceImpl;

    @Autowired
    private IWorkDeptService workDeptServiceImpl;

    /**
     * @Description: 获取子账户列表（分页）
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/18
     */
    @Override
    public GetSubSystemPage getSubSystemPageList(APIGetSubSystemRequest parem, ReqPerEnum permission) {
        //region 组装where条件
        LambdaQueryChainWrapper<UserEntity> wrapper = lambdaQuery();
        setWrapperPermission(wrapper, permission);
        if (StringUtils.hasLength(parem.getPhone())) {
            wrapper.like(UserEntity::getPhone, parem.getPhone());
        }
        if (parem.getStatus() != null && parem.getStatus() >= 0 && parem.getStatus() <= 2) {
            wrapper.eq(UserEntity::getStatus, parem.getStatus());
        }
        //endregion

        //region 数据库获取数据
        //分页查询
        Page<UserEntity> page = new Page<>(parem.getPage(), parem.getPerPage());
        wrapper.page(page);
        List<UserEntity> list = page.getRecords();
        //endregion

        GetSubSystemPage result = new GetSubSystemPage();

        //region 按照状态统计用户数
        // 禁用状态
        long disableNum = list.stream().filter(n -> n.getStatus() == StatusEnum.DISABLE.getCode()).count();
        result.setDisableNum(disableNum);
        //正常状态
        long normalNum = list.stream().filter(n -> n.getStatus() == StatusEnum.NORMAL.getCode()).count();
        result.setNormalNum(normalNum);
        //未启用状态
        long notEnabledNum = list.size() - disableNum - normalNum;
        result.setNotEnabledNum(notEnabledNum);
        //endregion

        //region 给page参数赋值
        GetSubSystemPage.Page pages = result.new Page();
        pages.setPerPage(parem.getPerPage());
        pages.setTotal(page.getTotal());
        pages.setTotalPage(page.getPages());
        result.setPage(pages);
        //endregion
        WorkEmployeeDepartmentEntity wWorkEmployeeDepartmentEntity = null;
        //region 给list集合赋值
        List<GetSubSystemPage.UserList> userList = new ArrayList<>();
        Map<String, Object> map = null;
        for (UserEntity item : list) {
            List<Map<String, Object>> mapList = new ArrayList();
            GetSubSystemPage.UserList userItem = result.new UserList();
            userItem.setCreatedAt(DateUtils.formatS1(item.getCreatedAt().getTime()));
            userItem.setPhone(item.getPhone());
            WorkEmployeeEntity workEmployeeEntity = workEmployeeServiceImpl.getWorkEmployeeInfoByLogId(item.getId());
            String sbStr = null;
            if (workEmployeeEntity != null) {
                List<WorkEmployeeDepartmentEntity> workEmployeeDepartmentEntityList = workEmployeeDepartmentServiceImpl.getDeptIdByEmpId(workEmployeeEntity.getId());
                if (workEmployeeDepartmentEntityList != null && workEmployeeDepartmentEntityList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (WorkEmployeeDepartmentEntity workEmployeeDepartmentEntity :
                            workEmployeeDepartmentEntityList) {
                        String str = workEmployeeDepartmentEntity.getDepartmentId().toString();
                        sb.append(str).append(",");
                    }
                    sbStr = sb.substring(0, sb.length() - 1);
                }

            }
            if (sbStr != null && sbStr.length() > 0) {
                List<WorkDeptEntity> workDeptEntityList = workDeptServiceImpl.getNameById(sbStr, "id,name");
                if (workDeptEntityList != null && workDeptEntityList.size() > 0) {
                    for (WorkDeptEntity workDeptEntity :
                            workDeptEntityList) {
                        map = new HashMap();
                        map.put("departmentId", workDeptEntity.getId());
                        map.put("departmentName", workDeptEntity.getName());
                        mapList.add(map);
                    }
                    userItem.setDepartment(mapList);
                } else {
                    userItem.setDepartment(null);
                }
            }
            userItem.setPosition(item.getPosition());
            userItem.setStatus(item.getStatus());
            userItem.setUserName(item.getName());
            userItem.setUserId(item.getId());
            userItem.setStatusText(StatusEnum.getTypeByCode(item.getStatus()));
            userList.add(userItem);
        }
        result.setList(userList);
        //endregion

        return result;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private void setWrapperPermission(LambdaQueryChainWrapper<UserEntity> wrapper, ReqPerEnum permission) {
        if (permission == ReqPerEnum.ALL) {
            return;
        }

        LambdaQueryChainWrapper<WorkEmployeeEntity> tempWrapper = employeeService.lambdaQuery()
                .select(WorkEmployeeEntity::getLogUserId);
        if (permission == ReqPerEnum.DEPARTMENT) {
            // 查询员工所属的部门 id 列表
            List<Integer> idList = employeeDepartmentService.getDeptAndChildDeptEmpIdList();
            tempWrapper.in(WorkEmployeeEntity::getId, idList);
        }

        if (permission == ReqPerEnum.EMPLOYEE) {
            int empId = AccountService.getEmpId();
            tempWrapper.eq(WorkEmployeeEntity::getId, empId);
        }

        // 子账户 id 列表
        List<Integer> idList = tempWrapper
                .list()
                .stream()
                .map(WorkEmployeeEntity::getLogUserId)
                .collect(Collectors.toList());

        wrapper.in(UserEntity::getId, idList);
    }

    /**
     * @Description: 创建子账户
     * @Param:
     * @return: true|false
     * @Author: zhaojinjian
     * @Date: 2020/11/18
     */
    @Override
    public boolean saveSubsystem(AddSubSystemRequest parem) {
        // region 验证手机号唯一
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", parem.getPhone());
        int cnt = this.baseMapper.selectCount(wrapper);
        if (cnt > 0) {
            throw new ParamException("手机号已存在");
        }
        // endregion

        // region 验证角色是否存在
        McRbacRoleEntity roleEntity = roleService.getById(parem.getRoleId());
        if (Objects.isNull(roleEntity)) {
            throw new ParamException("角色不存在");
        }
        // endregion

        String password = "1234563S6ybWbSy&LFAlp";
        String pwd = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.toCharArray());

        // region 子账户实体赋值 并保存
        UserEntity entity = new UserEntity();
        entity.setName(parem.getUserName());
        entity.setGender(parem.getGender());
        entity.setPhone(parem.getPhone());
        entity.setPassword(pwd);
        entity.setStatus(parem.getStatus());
        entity.setTenantId(AccountService.getTenantId());
        boolean result = entity.insert();
        // endregion

        // region 关联子账户与员工
        if (result) {
            List<WorkEmployeeEntity> employeeEntityList = employeeService.lambdaQuery()
                    .select(WorkEmployeeEntity::getId, WorkEmployeeEntity::getLogUserId)
                    .eq(WorkEmployeeEntity::getMobile, parem.getPhone())
                    .list();
            employeeEntityList.forEach(e -> {
                e.setLogUserId(entity.getId());
            });
            employeeService.saveOrUpdateBatch(employeeEntityList);
        }
        // endregion

        // region 关联子账户与角色
        if (result) {
            McRbacUserRoleEntity userRoleEntity = new McRbacUserRoleEntity();
            userRoleEntity.setUserId(entity.getId());
            userRoleEntity.setRoleId(parem.getRoleId());
            return userRoleEntity.insert();
        }
        // endregion

        return false;
    }


    /**
     * @Description: 更新员工账户登录密码
     * @Param:
     * @return: true|;false
     * @Author: zhaojinjian
     * @Date: 2020/11/18
     */
    @Override
    public boolean updatePassword(PasswordUpdateRequest parem, int id) {
        UserEntity entity = this.baseMapper.selectById(id);
        if (entity != null) {
            //String password1="1234563S6ybWbSy&LFAlp";
            String oldPwd = parem.getOldPassword() + "3S6ybWbSy&LFAlp";
            BCrypt.Result res = BCrypt.verifyer().verify(oldPwd.toCharArray(), entity.getPassword());
            //判断当前登陆人密码是否正确
            if (res.verified) {
                String password = parem.getNewPassword() + "3S6ybWbSy&LFAlp";
                String pwd = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.toCharArray());
                entity.setPassword(pwd);
                entity.setUpdatedAt(new Timestamp(new Date().getTime()));
                return this.baseMapper.updateById(entity) == 1;
            } else {
                //旧密码输入错误
                throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), "旧密码输入错误");
            }
        } else {
            //用户不存在
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), "用户不存在");
        }
    }

    /**
     * @Description:子账户信息更新
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @Override
    public boolean update(UpdateSubSystemRequest parem) {
        // region 验证账户 id
        if (parem.getUserId() <= 0) {
            throw new ParamException("账户不存在");
        }
        // endregion

        // region 验证角色是否存在
        McRbacRoleEntity roleEntity = roleService.getById(parem.getRoleId());
        if (Objects.isNull(roleEntity)) {
            throw new ParamException("角色不存在");
        }
        // endregion

        UserEntity userEntity = getById(parem.getUserId());

        // region 判断当前手机号是否重复
        if (!userEntity.getPhone().equals(parem.getPhone())) {
            QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", parem.getPhone());
            int cnt = this.baseMapper.selectCount(wrapper);
            if (cnt > 0) {
                throw new ParamException("手机号已存在");
            }
        }
        // endregion

        userEntity.setName(parem.getUserName());
        userEntity.setGender(parem.getGender());
        userEntity.setPhone(parem.getPhone());
        userEntity.setStatus(parem.getStatus());
        this.baseMapper.updateById(userEntity);

        // region 关联子账户与角色
        McRbacUserRoleEntity userRoleEntity = userRoleService.lambdaQuery()
                .select(McRbacUserRoleEntity::getId)
                .eq(McRbacUserRoleEntity::getUserId, userEntity.getId())
                .one();
        if (Objects.isNull(userRoleEntity)) {
            userRoleEntity = new McRbacUserRoleEntity();
            userRoleEntity.setUserId(userEntity.getId());
        }
        userRoleEntity.setRoleId(parem.getRoleId());
        userRoleEntity.insertOrUpdate();
        // endregion

        //region 查看通讯录表中是否存在当前手机号，然后绑定
        employeeService.verifyEmployeeMobile(parem.getPhone(), parem.getUserId());
        //endregion
        return true;
    }

    /**
     * @Description:获取登录用户信息详情
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @Override
    public LoginShowRresponse getLoginShowInfo(int userId, int empId) {
        if (userId > 0) {
            UserEntity userEntity = this.baseMapper.selectById(userId);
            WorkEmployeeEntity employeeEntity = employeeService.getById(empId);
            CorpEntity corpEntity = null;
            Integer corpId = AccountService.getCorpId();
            if (corpId == null) {
                if (!employeeEntity.getCorpId().equals("") && employeeEntity.getCorpId() != null) {
                    corpEntity = corpService.getCorpInfoById(employeeEntity.getCorpId());
                }
            } else {
                corpEntity = corpService.getCorpInfoById(corpId);
            }
            LoginShowRresponse loginInfo = new LoginShowRresponse();
            loginInfo.setUserId(userEntity.getId());
            loginInfo.setUserPhone(userEntity.getPhone());
            loginInfo.setUserName(userEntity.getName());
            loginInfo.setUserGender(userEntity.getGender());
            loginInfo.setUserDepartment(userEntity.getDepartment());
            loginInfo.setUserPosition(userEntity.getPosition());
            if (userEntity.getLoginTime() != null) {
                loginInfo.setUserLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userEntity.getLoginTime()));
            }
            loginInfo.setUserStatus(userEntity.getStatus());
            if (employeeEntity != null) {
                loginInfo.setEmployeeId(employeeEntity.getId());
                loginInfo.setEmployeeName(employeeEntity.getName());
                loginInfo.setEmployeeMobile(employeeEntity.getMobile());
                loginInfo.setEmployeePosition(employeeEntity.getPosition());
                loginInfo.setEmployeeGender(employeeEntity.getGender());
                loginInfo.setEmployeeEmail(employeeEntity.getEmail());
                loginInfo.setEmployeeAvatar(AliyunOssUtils.getUrl(employeeEntity.getAvatar()));
                loginInfo.setEmployeeThumbAvatar(AliyunOssUtils.getUrl(employeeEntity.getThumbAvatar()));
                loginInfo.setEmployeeTelephone(employeeEntity.getTelephone());
                loginInfo.setEmployeeAlias(employeeEntity.getAlias());
                loginInfo.setEmployeeStatus(employeeEntity.getStatus());
                loginInfo.setEmployeeQrCode(AliyunOssUtils.getUrl(employeeEntity.getQrCode()));
                loginInfo.setEmployeeExternalPosition(employeeEntity.getExternalPosition());
                loginInfo.setEmployeeAddress(employeeEntity.getAddress());
                loginInfo.setCorpId(employeeEntity.getCorpId());
                loginInfo.setCorpName(corpEntity.getCorpName());
            }
            return loginInfo;
        }
        return null;
    }

    /**
     * @Description: 多账户状态禁用或启用
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @Override
    public boolean setStatus(String[] userIds, int status) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.in("id", Arrays.asList(userIds));
        UserEntity entity = new UserEntity();
        entity.setStatus(status);
        return update(entity, wrapper);
    }


    /**
     * @Description: 获取子账户详情
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @Override
    public GetSubSystemInfoResponse getSubSystemInfo(int userId) {
        UserEntity userEntity = this.baseMapper.selectById(userId);
        if (Objects.isNull(userEntity)) {
            throw new ParamException("用户不存在");
        }

        GetSubSystemInfoResponse info = new GetSubSystemInfoResponse();
        info.setUserId(userEntity.getId());
        info.setUserName(userEntity.getName());
        info.setPhone(userEntity.getPhone());
        info.setGender(userEntity.getGender());
        info.setStatus(userEntity.getStatus());

        // 查询角色
        List<McRbacUserRoleEntity> userRoleEntityList = userRoleService.lambdaQuery()
                .eq(McRbacUserRoleEntity::getUserId, userId)
                .list();
        if (!userRoleEntityList.isEmpty()) {
            McRbacRoleEntity roleEntity = roleService.getById(userRoleEntityList.get(0).getRoleId());
            if (Objects.nonNull(roleEntity)) {
                info.setRoleId(roleEntity.getId());
                info.setRoleName(roleEntity.getName());
            }
        }

        // TODO 查询部门
        List<WorkEmployeeEntity> employeeEntityList = employeeService.lambdaQuery()
                .select(WorkEmployeeEntity::getId)
                .eq(WorkEmployeeEntity::getLogUserId, userId)
                .eq(WorkEmployeeEntity::getCorpId, AccountService.getCorpId())
                .list();
        if (!employeeEntityList.isEmpty()) {
            List<Integer> deptIdList = employeeDepartmentService.lambdaQuery()
                    .select(WorkEmployeeDepartmentEntity::getDepartmentId)
                    .eq(WorkEmployeeDepartmentEntity::getEmployeeId, employeeEntityList.get(0).getId())
                    .list()
                    .stream()
                    .map(WorkEmployeeDepartmentEntity::getDepartmentId)
                    .collect(Collectors.toList());
            if (!deptIdList.isEmpty()) {
                List<WorkDeptEntity> deptEntityList = deptService.listByIds(deptIdList);
                List<GetSubSystemInfoResponse.UserDeptVO> deptVOList = new ArrayList<>();
                for (WorkDeptEntity deptEntity : deptEntityList) {
                    GetSubSystemInfoResponse.UserDeptVO vo = new GetSubSystemInfoResponse.UserDeptVO();
                    vo.setDepartmentId(deptEntity.getId());
                    vo.setDepartmentName(deptEntity.getName());
                    deptVOList.add(vo);
                }
                info.setDepartment(deptVOList);
            }
        }

        return info;
    }

    @Override
    public List<UserEntity> getUserByPhone(String phoneNumber, String password) {
        List<UserEntity> userEntityList = this.baseMapper.getUserByPhone(phoneNumber, password);
        //QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        //Page<UserEntity> indexDataPage = new Page<>(page, pageSize);
        return userEntityList;
    }

    @Override
    public UserEntity login(String phone, String password) {
        QueryWrapper<UserEntity> userWrapper = new QueryWrapper<>();
        userWrapper.eq("phone", phone);
        userWrapper.eq("status", 1);
        UserEntity userEntity = this.baseMapper.selectOne(userWrapper);
        if (userEntity != null) {
            password = password + "3S6ybWbSy&LFAlp";
            BCrypt.Result res = BCrypt.verifyer().verify(password.toCharArray(), userEntity.getPassword());
            if (res.verified) {
                QueryWrapper<UserEntity> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("", userEntity.getId());
                userEntity.setLoginTime(new Date());
                this.updateById(userEntity);
                return userEntity;
            }
        }
        return null;
    }
}
