package com.mochat.mochat.service.emp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.RespEmployeeErrCodeEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.em.workemployee.ContactAuthEnum;
import com.mochat.mochat.common.em.workemployee.EmployeeStatusEnum;
import com.mochat.mochat.common.em.workemployee.GenderEnum;
import com.mochat.mochat.common.em.workupdatetime.TypeEnum;
import com.mochat.mochat.common.util.FileUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.util.emp.DownUploadQueueUtils;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.mapper.WorkEmployeeMapper;
import com.mochat.mochat.model.emp.*;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.IWorkUpdateTimeService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.ISubSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 通讯录服务
 * @author: zhaojinjian
 * @create: 2020-11-23 16:02
 **/
@Service
public class WorkEmployeeServiceImpl extends ServiceImpl<WorkEmployeeMapper, WorkEmployeeEntity> implements IWorkEmployeeService {

    @Autowired
    private IWorkDeptService deptService;

    @Autowired
    private IWorkEmployeeStatisticService employeeStatisticService;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    @Autowired
    private IWorkUpdateTimeService workUpdateTimeService;

    @Autowired
    private ISubSystemService subSystemService;

    /**
     * @description 企业服务
     * @author zhaojinjian
     * @createTime 2020/12/12 16:56
     */
    @Autowired
    private ICorpService corpService;

    private static Map<Integer, List<String>> followUserMap = new HashMap<>();

    @Override
    public WorkEmployeeEntity getByWxEmpId(String wxEmpId) {
        return lambdaQuery().eq(WorkEmployeeEntity::getWxUserId, wxEmpId).one();
    }

    /**
     * 同步部门和成员
     *
     * @param corpId
     */
    @Override
    public void synWxEmployee(int corpId) {
        syncDepartment(corpId);
        syncEmployee(corpId);
        workUpdateTimeService.updateSynTime(corpId, TypeEnum.EMPLOYEE);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void syncDepartment(int corpId) {
        String result = WxApiUtils.requestDepartmentListApi(corpId);
        log.debug(" >>>>>>> 微信部门列表: " + result);
        if (null == result) {
            log.error(">>>>>>> 微信部门列表获取失败: null");
            return;
        }

        List<WXDepartmentDTO> wxEmployeeDTOList = JSONArray.parseArray(result, WXDepartmentDTO.class);
        if (null == wxEmployeeDTOList || wxEmployeeDTOList.isEmpty()) {
            return;
        }

        Map<Integer, WXDepartmentDTO> map = new HashMap<>(0);
        for (WXDepartmentDTO e : wxEmployeeDTOList) {
            map.put(e.getId(), e);
        }

        List<WorkDeptEntity> workDeptEntityList = deptService.lambdaQuery()
                .eq(WorkDeptEntity::getCorpId, corpId)
                .list();

        for (WorkDeptEntity wde : workDeptEntityList) {
            map.remove(wde.getWxDepartmentId());
        }
        workDeptEntityList.clear();

        map.forEach((integer, wxDepartmentDTO) -> {
            WorkDeptEntity entity = new WorkDeptEntity();
            entity.setWxDepartmentId(wxDepartmentDTO.getId());
            entity.setCorpId(corpId);
            entity.setName(wxDepartmentDTO.getName());
            entity.setParentId(getMainDepartmentId(corpId, wxDepartmentDTO.getParentid()));
            entity.setWxParentid(wxDepartmentDTO.getParentid());
            entity.setOrder(wxDepartmentDTO.getOrder());

            workDeptEntityList.add(entity);
        });
        map.clear();

        if (!workDeptEntityList.isEmpty()) {
            deptService.insertDeptments(workDeptEntityList, corpId);
        }
    }

    @Override
    public void syncEmployee(int corpId) {
        // 获取部门信息
        List<WorkDeptEntity> deptEntityList = deptService.lambdaQuery()
                .select(WorkDeptEntity::getWxDepartmentId)
                .eq(WorkDeptEntity::getCorpId, corpId)
                .list();

        // 遍历部门信息, 拉取微信成员信息
        for (WorkDeptEntity dept : deptEntityList) {
            syncEmpByWxDepartId(corpId, dept.getWxDepartmentId());
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void syncEmployeeStatistic(int corpId) {
        // 获取成员信息
        List<WorkEmployeeEntity> employeeEntityList = lambdaQuery()
                .select(WorkEmployeeEntity::getId, WorkEmployeeEntity::getWxUserId)
                .eq(WorkEmployeeEntity::getCorpId, corpId)
                .list();

        for (WorkEmployeeEntity emp : employeeEntityList) {
            String result = WxApiUtils.getUserBehaviorData(corpId, emp.getWxUserId());
            log.debug(" >>>>>>>>> 微信员工客户统计数据: " + result);
            if (null == result) {
                log.error(" >>>>>>>>> 微信员工客户统计数据同步失败: null");
                continue;
            }

            List<WXEmpStatisticDTO> statisticDTOList = JSONArray.parseArray(result, WXEmpStatisticDTO.class);
            if (null == statisticDTOList || statisticDTOList.isEmpty()) {
                log.error("获取微信员工统计数据");
                return;
            }
            WXEmpStatisticDTO dto = statisticDTOList.get(0);
            WorkEmployeeStatisticEntity entity = new WorkEmployeeStatisticEntity();
            entity.setCorpId(emp.getCorpId());
            entity.setEmployeeId(emp.getId());
            entity.setNewApplyCnt(dto.getNewApplyCnt());
            entity.setNewContactCnt(dto.getNewContactCnt());
            entity.setChatCnt(dto.getChatCnt());
            entity.setMessageCnt(dto.getMessageCnt());
            entity.setReplyPercentage((int) (dto.getReplyPercentage() * 100));
            entity.setAvgReplyTime(dto.getAvgReplyTime());
            entity.setNegativeFeedbackCnt(dto.getNegativeFeedbackCnt());
            entity.setSynTime(new Timestamp(dto.getStatTime()));

            employeeStatisticService.saveOrUpdate(entity);
        }
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void syncEmpByWxDepartId(int corpId, int wxDepartmentId) {
        String result = WxApiUtils.requestUserListApi(corpId, wxDepartmentId);
        log.debug(" >>>>>> 微信部门下成员详情列表: " + result);
        if (null == result) {
            log.error(" >>>>>> 微信部门下成员详情列表获取失败: null");
            return;
        }

        List<WXEmployeeDTO> wxEmployeeDTOList = JSONArray.parseArray(result, WXEmployeeDTO.class);
        if (null == wxEmployeeDTOList || wxEmployeeDTOList.isEmpty()) {
            return;
        }

        Map<String, WXEmployeeDTO> map = new HashMap<>(0);
        for (WXEmployeeDTO e : wxEmployeeDTOList) {
            map.put(e.getUserid(), e);
        }

        List<WorkEmployeeEntity> employeeEntityList = lambdaQuery()
                .select(WorkEmployeeEntity::getWxUserId)
                .eq(WorkEmployeeEntity::getCorpId, corpId)
                .list();

        for (WorkEmployeeEntity e : employeeEntityList) {
            map.remove(e.getWxUserId());
        }

        List<WorkEmployeeEntity> newEmployeeEntityList = new ArrayList<>();
        map.forEach((key, wxe) -> {
            WorkEmployeeEntity employee = transWxEmpDtoToEmpEntity(corpId, wxe);
            newEmployeeEntityList.add(employee);
        });

        if (!newEmployeeEntityList.isEmpty()) {
            saveBatch(newEmployeeEntityList);
            // 更新成员与部门表索引
            for (WXEmployeeDTO dto : map.values()) {
                updateEmpDeptIndex(corpId, dto);
            }
        }
    }

    private List<String> getFollowUserList(int corpId) {
        List<String> list = followUserMap.get(corpId);
        if (list == null) {
            String followUserStr = WxApiUtils.requestFollowUserListApi(corpId);
            List<String> tempList = JSONArray.parseArray(followUserStr, String.class);
            if (tempList != null && !tempList.isEmpty()) {
                list = tempList;
                followUserMap.put(corpId, list);
            }
        }
        return list;
    }

    private int getMainDepartmentId(int corpId, int wxDepartmentId) {
        if (wxDepartmentId == 0) {
            return 0;
        }
        try {
            WorkDeptEntity workDeptEntity = new WorkDeptEntity();
            workDeptEntity.setCorpId(corpId);
            workDeptEntity.setWxDepartmentId(wxDepartmentId);
            return deptService.lambdaQuery()
                    .eq(WorkDeptEntity::getCorpId, corpId)
                    .eq(WorkDeptEntity::getWxDepartmentId, wxDepartmentId)
                    .one()
                    .getId();
        } catch (Exception e) {
            // 部门 ID 不存在
            log.error(">>>>>>>>> 获取部门 id 出错, 部门 wxDepartmentId: " + wxDepartmentId + "不存在");
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void insertEmployee(int corpId, WXEmployeeDTO dto) {
        WorkEmployeeEntity employee = transWxEmpDtoToEmpEntity(corpId, dto);
        save(employee);
        updateEmpDeptIndex(corpId, dto);
    }

    private WorkEmployeeEntity transWxEmpDtoToEmpEntity(int corpId, WXEmployeeDTO dto) {
        WorkEmployeeEntity employee = new WorkEmployeeEntity();
        employee.setWxUserId(dto.getUserid());
        employee.setCorpId(corpId);
        employee.setName(dto.getName());
        employee.setMobile(dto.getMobile());
        employee.setPosition(dto.getPosition());
        employee.setGender(dto.getGender());
        employee.setEmail(dto.getEmail());
        employee.setAvatar(dto.getAvatar());
        employee.setThumbAvatar(dto.getThumbAvatar());
        employee.setTelephone(dto.getTelephone());
        employee.setAlias(dto.getAlias());
        employee.setExtattr(dto.getExtattr());
        employee.setStatus(dto.getStatus());
        employee.setExternalProfile(dto.getExternalProfile());
        employee.setExternalPosition(dto.getExternalPosition());
        employee.setAddress(dto.getAddress());
        employee.setOpenUserId(dto.getOpenUserid());
        employee.setWxMainDepartmentId(dto.getMainDepartment());
        employee.setMainDepartmentId(getMainDepartmentId(corpId, dto.getMainDepartment()));

        // 是否配置外部联系人权限（1.是 2.否）
        employee.setContactAuth(hasContactAuth(corpId, dto.getUserid()) ? 1 : 2);

        // 关联子账户
        List<UserEntity> userEntityList = subSystemService.lambdaQuery()
                .select(UserEntity::getId)
                .eq(UserEntity::getPhone, dto.getMobile())
                .list();
        if (!userEntityList.isEmpty()) {
            employee.setLogUserId(userEntityList.get(0).getId());
        }

        String avatarUrl = dto.getAvatar();
        String thumbAvatarUrl = dto.getThumbAvatar();
        String qrCodeUrl = dto.getQrCode();

        String avatarKey = FileUtils.getFileNameOfEmpAvatar();
        String thumbAvatarKey = FileUtils.getFileNameOfEmpThumbAvatar();
        String qrCodeKey = FileUtils.getFileNameOfEmpQrCode();

        employee.setAvatar(avatarKey);
        employee.setThumbAvatar(thumbAvatarKey);
        employee.setQrCode(qrCodeKey);

        DownUploadQueueUtils.uploadFileByUrl(avatarKey, avatarUrl);
        DownUploadQueueUtils.uploadFileByUrl(thumbAvatarKey, thumbAvatarUrl);
        DownUploadQueueUtils.uploadFileByUrl(qrCodeKey, qrCodeUrl);

        return employee;
    }

    private boolean hasContactAuth(int corpId, String wxUserId) {
        return getFollowUserList(corpId).contains(wxUserId);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateEmpDeptIndex(int corpId, WXEmployeeDTO dto) {
        // 根据员工微信 id, 获取员工 id
        int empId = getEmpIdByWxUserId(corpId, dto.getUserid());

        List<WorkEmployeeDepartmentEntity> workEmployeeDepartmentEntityList = employeeDepartmentService.list(
                new QueryWrapper<WorkEmployeeDepartmentEntity>()
                        .eq("employee_id", empId)
        );

        // 循环员工微信部门 id, 获取对应部门 id
        List<Integer> depts = dto.getDepartment();
        List<Integer> orders = dto.getOrder();
        List<Integer> isLeaderInDepts = dto.getIsLeaderInDept();

        WorkEmployeeDepartmentEntity entity;

        List<Integer> hasList = new ArrayList<>();
        List<WorkEmployeeDepartmentEntity> updateList = new ArrayList<>();

        if (!workEmployeeDepartmentEntityList.isEmpty()) {
            for (int i = 0; i < depts.size(); i++) {
                int deptId = depts.get(i);
                int order = orders.get(i);
                int isLeaderInDept = isLeaderInDepts.get(i);

                // 查询对应部门 id
                int dId = getMainDepartmentId(corpId, deptId);

                for (int j = 0; j < workEmployeeDepartmentEntityList.size(); j++) {
                    entity = workEmployeeDepartmentEntityList.get(j);
                    if (dId == entity.getDepartmentId()) {
                        hasList.add(i);
                        entity.setIsLeaderInDept(isLeaderInDept);
                        entity.setOrder(order);
                        updateList.add(entity);
                        workEmployeeDepartmentEntityList.remove(entity);
                        break;
                    }
                }
            }
        }

        for (int hasIndex : hasList) {
            depts.remove(hasIndex);
            orders.remove(hasIndex);
            isLeaderInDepts.remove(hasIndex);
        }

        List<WorkEmployeeDepartmentEntity> newList = new ArrayList<>();

        for (int i = 0; i < depts.size(); i++) {
            int deptId = depts.get(i);
            int order = orders.get(i);
            int isLeaderInDept = isLeaderInDepts.get(i);

            // 查询对应部门 id
            int dId = getMainDepartmentId(corpId, deptId);

            entity = new WorkEmployeeDepartmentEntity();
            entity.setEmployeeId(empId);
            entity.setDepartmentId(dId);
            entity.setIsLeaderInDept(isLeaderInDept);
            entity.setOrder(order);

            newList.add(entity);
        }

        List<Integer> removeIds = new ArrayList<>();
        for (WorkEmployeeDepartmentEntity employeeDepartmentEntity : workEmployeeDepartmentEntityList) {
            removeIds.add(employeeDepartmentEntity.getId());
        }

        if (!removeIds.isEmpty()) {
            employeeDepartmentService.getBaseMapper().deleteBatchIds(removeIds);
        }
        if (!updateList.isEmpty()) {
            employeeDepartmentService.updateBatchById(updateList);
        }
        if (!newList.isEmpty()) {
            employeeDepartmentService.saveBatch(newList);
        }
    }

    private int getEmpIdByWxUserId(int corpId, String wxUserId) {
        // 根据员工微信 id, 获取员工 id
        WorkEmployeeEntity employeeEntity = lambdaQuery()
                .eq(WorkEmployeeEntity::getCorpId, corpId)
                .eq(WorkEmployeeEntity::getWxUserId, wxUserId)
                .one();
        if (employeeEntity == null) {
            throw new CommonException(RespEmployeeErrCodeEnum.EMPLOYEE_NO_EXISTS);
        } else {
            return employeeEntity.getId();
        }
    }

    @Override
    public Page<EmpEmployeeBO> index(EmpIndexDTO req, ReqPerEnum permission) {
        int corpId = AccountService.getCorpId();

        Integer status = req.getStatus();
        String name = req.getName();
        String contactAuth = req.getContactAuth();

        LambdaQueryChainWrapper<WorkEmployeeEntity> wrapper = lambdaQuery();
        // 设置数据权限
        setWrapperPermission(wrapper, permission);
        wrapper.select(
                WorkEmployeeEntity::getId,
                WorkEmployeeEntity::getName,
                WorkEmployeeEntity::getThumbAvatar,
                WorkEmployeeEntity::getStatus,
                WorkEmployeeEntity::getContactAuth,
                WorkEmployeeEntity::getGender
        );
        wrapper.eq(WorkEmployeeEntity::getCorpId, corpId);
        if (null != status && status > 0 && status <= 5) {
            wrapper.eq(WorkEmployeeEntity::getStatus, status);
        }
        if (null != name && !name.isEmpty()) {
            wrapper.like(WorkEmployeeEntity::getName, name);
        }
        if (null != contactAuth) {
            wrapper.eq(WorkEmployeeEntity::getContactAuth, contactAuth);
        }

        Page<WorkEmployeeEntity> pageEntity = ApiRespUtils.initPage(req);
        wrapper.page(pageEntity);

        List<WorkEmployeeEntity> workEmployeeEntities = pageEntity.getRecords();
        List<Integer> eIds = new ArrayList<>();
        for (WorkEmployeeEntity e : workEmployeeEntities) {
            eIds.add(e.getId());
        }

        Map<Integer, WorkEmployeeStatisticEntity> map = new HashMap<>();
        if (!eIds.isEmpty()) {
            List<WorkEmployeeStatisticEntity> workEmployeeStatisticEntities = employeeStatisticService.list(
                    new QueryWrapper<WorkEmployeeStatisticEntity>()
                            .select("new_apply_cnt", "new_contact_cnt", "chat_cnt", "message_cnt", "reply_percentage",
                                    "avg_reply_time", "negative_feedback_cnt")
                            .eq("corp_id", corpId)
                            .in("employee_id", eIds)
            );
            for (WorkEmployeeStatisticEntity employeeStatisticEntity : workEmployeeStatisticEntities) {
                map.put(employeeStatisticEntity.getEmployeeId(), employeeStatisticEntity);
            }
        }

        List<EmpEmployeeBO> employeeBOList = new ArrayList<>();
        EmpEmployeeBO employeeBO;
        for (WorkEmployeeEntity employeeEntity : workEmployeeEntities) {
            employeeBO = new EmpEmployeeBO();
            employeeBO.setId(employeeEntity.getId());
            employeeBO.setName(employeeEntity.getName());
            String thumbAvatar = AliyunOssUtils.getUrl(employeeEntity.getThumbAvatar());
            employeeBO.setThumbAvatar(thumbAvatar);
            employeeBO.setStatus(employeeEntity.getStatus());
            employeeBO.setContactAuth(employeeEntity.getContactAuth());
            employeeBO.setGender(GenderEnum.getMsgByCode(employeeEntity.getGender()));
            employeeBO.setContactAuthName(ContactAuthEnum.getMsgByCode(employeeEntity.getContactAuth()));
            employeeBO.setApplyNums(employeeEntity.getId());
            employeeBO.setStatusName(EmployeeStatusEnum.getMsgByCode(employeeEntity.getStatus()));

            WorkEmployeeStatisticEntity employeeStatisticEntity = map.get(employeeEntity.getId());
            if (employeeStatisticEntity != null) {
                employeeBO.setAddNums(employeeStatisticEntity.getNewContactCnt());
                employeeBO.setMessageNums(employeeStatisticEntity.getMessageCnt());
                employeeBO.setSendMessageNums(employeeStatisticEntity.getMessageCnt());
                employeeBO.setReplyMessageRatio(employeeStatisticEntity.getReplyPercentage());
                employeeBO.setAverageReply(employeeStatisticEntity.getAvgReplyTime());
                employeeBO.setInvalidContact(employeeStatisticEntity.getNegativeFeedbackCnt());
            }

            employeeBOList.add(employeeBO);
        }

        return ApiRespUtils.transPage(pageEntity, employeeBOList);
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private void setWrapperPermission(LambdaQueryChainWrapper<WorkEmployeeEntity> wrapper, ReqPerEnum permission) {
        if (permission == ReqPerEnum.ALL) {
            return;
        }

        if (permission == ReqPerEnum.DEPARTMENT) {
            // 查询员工所属的部门 id 列表
            List<Integer> idList = employeeDepartmentService.getDeptAndChildDeptEmpIdList();

            // 将部门和子部门的员工 id 列表加入搜索 wrapper
            if (idList.size() > 0) {
                wrapper.in(WorkEmployeeEntity::getId, idList);
            }
        }

        if (permission == ReqPerEnum.EMPLOYEE) {
            int empId = AccountService.getEmpId();
            wrapper.eq(WorkEmployeeEntity::getId, empId);
        }
    }

    @Override
    public EmpSearchConditionBO searchCondition() {
        EmpSearchConditionBO searchConditionBO = new EmpSearchConditionBO();

        String syncTime = workUpdateTimeService.getLastUpdateTime(TypeEnum.EMPLOYEE);
        searchConditionBO.setSyncTime(syncTime);

        EmployeeStatusEnum[] employeeStatusEnums = EmployeeStatusEnum.values();
        List<EmpSearchConditionBO.EmpEnumBO> empEnumBOList = new ArrayList<>();
        for (EmployeeStatusEnum e : employeeStatusEnums) {
            EmpSearchConditionBO.EmpEnumBO empEnumBO = new EmpSearchConditionBO.EmpEnumBO();
            empEnumBO.setId(e.getCode());
            empEnumBO.setName(e.getMsg());
            empEnumBOList.add(empEnumBO);
        }
        searchConditionBO.setStatus(empEnumBOList);

        ContactAuthEnum[] contactAuthEnums = ContactAuthEnum.values();
        List<EmpSearchConditionBO.EmpEnumBO> empEnumBOList1 = new ArrayList<>();
        for (ContactAuthEnum e : contactAuthEnums) {
            EmpSearchConditionBO.EmpEnumBO empEnumBO = new EmpSearchConditionBO.EmpEnumBO();
            empEnumBO.setId(e.getCode());
            empEnumBO.setName(e.getMsg());
            empEnumBOList1.add(empEnumBO);
        }
        searchConditionBO.setContactAuth(empEnumBOList1);

        return searchConditionBO;
    }

    /**
     * 查看通讯录表中是否存在当前手机号，然后绑定
     *
     * @param mobile
     * @param logUserId
     */
    @Override
    public void verifyEmployeeMobile(String mobile, Integer logUserId) {
        //region 查看通讯录表中是否存在当前手机号，然后绑定
        QueryWrapper<WorkEmployeeEntity> workEmployeeWrapper = new QueryWrapper<>();
        workEmployeeWrapper.eq("mobile", mobile);
        workEmployeeWrapper.isNull("deleted_at");
        List<WorkEmployeeEntity> myWorkEmployee = this.list(workEmployeeWrapper);
        //判断当前手机号是否存在通讯录里
        if (myWorkEmployee != null && myWorkEmployee.size() > 0) {
            myWorkEmployee.forEach(item -> {
                item.setLogUserId(logUserId);
            });
            this.updateBatchById(myWorkEmployee);
        }
        //endregion
    }

    /**
     * @Description: 获取用户通讯录详情
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23
     */
    @Override
    public WorkEmployeeEntity getWorkEmployeeInfoById(Integer empId) {
        return this.baseMapper.selectById(empId);
    }

    /**
     * @Description: 通过userId获取用户通讯录详情
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/23logId
     */
    @Override
    public WorkEmployeeEntity getWorkEmployeeInfoByLogId(Integer userId) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.eq("log_user_id", userId);
        workEmployeeEntityQueryWrapper.eq("corp_id", AccountService.getCorpId());
        return this.baseMapper.selectOne(workEmployeeEntityQueryWrapper);
    }


    /**
     * @description:根据wxuserid查找
     * @author: Huayu
     * @time: 2021/3/30 22:51
     */
    @Override
    public WorkEmployeeEntity getWorkEmployeeByWxUserId(String userId, String s) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.select(s);
        workEmployeeEntityQueryWrapper.eq("wx_user_id", userId);
        return this.baseMapper.selectOne(workEmployeeEntityQueryWrapper);
    }

    @Override
    public List<WorkEmployeeEntity> countWorkEmployeesByCorpId(Integer corpId) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.eq("corp_id", corpId);
        return this.baseMapper.selectList(workEmployeeEntityQueryWrapper);
    }


    /**
     * @description 获取用户通讯录详情
     * @author zhaojinjian
     * @createTime 2020/12/16 19:03
     */
    @Override
    public WorkEmployeeEntity getWorkEmployeeInfoByWxEmpId(String userId) {
        QueryWrapper<WorkEmployeeEntity> employeeWrapper = new QueryWrapper<>();
        employeeWrapper.eq("wx_user_id", userId);
        return this.baseMapper.selectOne(employeeWrapper);
    }

    /**
     * @description 根据子账户Id获取成员信息
     * @author zhaojinjian
     * @createTime 2020/12/23 18:34
     */
    @Override
    public List<WorkEmployeeEntity> getWorkEmployeeByLogUserId(Integer logUserId) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeWrapper = new QueryWrapper<>();
        workEmployeeWrapper.eq("log_user_id", logUserId);
        workEmployeeWrapper.eq("status", 1);
        workEmployeeWrapper.isNull("deleted_at");
        return this.list(workEmployeeWrapper);
    }

    @Override
    public List<WorkEmployeeEntity> getWorkEmployeeByUserId(String userId) {
        List<WorkEmployeeEntity> list = this.baseMapper.getWorkEmployeeByUserId(userId);
        return list;
    }

    /**
     * 根据员工id获取员工名称
     *
     * @param empIds
     * @return
     */
    @Override
    public String[] getEmployeeName(List<Integer> empIds) {
        QueryWrapper<WorkEmployeeEntity> employeeWrapper = new QueryWrapper<>();
        employeeWrapper.select("name");
        employeeWrapper.in("id", empIds);
        employeeWrapper.isNull("deleted_at");
        return this.list(employeeWrapper).stream().map(WorkEmployeeEntity::getName).toArray(String[]::new);
    }

    /**
     * @description 获取企业下成员名称和id
     * @author zhaojinjian
     * @createTime 2021/1/7 12:51
     */
    @Override
    public Map<Integer, String> getCorpEmployeeName(Integer corpId, List<Integer> empIds) {
        CorpEntity corpEntity = corpService.getById(corpId);
        QueryWrapper<WorkEmployeeEntity> employeeWrapper = new QueryWrapper<>();
        employeeWrapper.select("name,id");
        employeeWrapper.eq("corp_id", corpId);
        if (empIds != null && empIds.size() != 0) {
            employeeWrapper.in("id", empIds);
        }
        //employeeWrapper.isNull("deleted_at");
        List<WorkEmployeeEntity> list = this.baseMapper.selectList(employeeWrapper);
        Map<Integer, String> map = new HashMap<>();
        list.forEach(item -> {
            map.put(item.getId(), corpEntity.getCorpName() + "--" + item.getName());
        });
        return map;
    }

    /**
     * @description 获取企业下成员名称和id
     * @author zhaojinjian
     * @createTime 2021/1/7 12:51
     */
    @Override
    public JSONArray getCorpEmployeeName(Integer corpId, String searchKeyWords) {
        QueryWrapper<WorkEmployeeEntity> employeeWrapper = new QueryWrapper<>();
        employeeWrapper.select("name,id");
        employeeWrapper.eq("corp_id", corpId);
        if (searchKeyWords != null && !searchKeyWords.isEmpty()) {
            employeeWrapper.like("name", searchKeyWords);
        }
        employeeWrapper.isNull("deleted_at");
        List<WorkEmployeeEntity> list = this.list(employeeWrapper);
        JSONArray resultArray = new JSONArray();
        list.forEach(item -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("employeeId", item.getId());
            jsonObject.put("name", item.getName());
            resultArray.add(jsonObject);
        });
        return resultArray;
    }

    /**
     * @description 根据企业id获取所有成员的微信UserId
     * @author zhaojinjian
     * @createTime 2020/12/18 14:21
     */
    @Override
    public Map<String, Integer> getCorpByUserId(Integer corpId) {
        QueryWrapper<WorkEmployeeEntity> employeeWrapper = new QueryWrapper<>();
        employeeWrapper.select("wx_user_id,id");
        employeeWrapper.eq("corp_id", corpId);
        employeeWrapper.isNull("deleted_at");

        List<WorkEmployeeEntity> employeeList = this.list(employeeWrapper);
        if (employeeList != null && employeeList.size() > 0) {
            return employeeList.stream().collect(Collectors.toMap(WorkEmployeeEntity::getWxUserId, WorkEmployeeEntity::getId));
        }
        return null;
    }

    /**
     * @param userId,corpId
     * @description: 查询当前用户归属的公司
     * @return:
     * @author: Huayu
     * @time: 2020/12/1 11:15
     */
    @Override
    public List<WorkEmployeeEntity> getWorkEmployeeByLogUserId(String userId, String corpId) {
        QueryWrapper<WorkEmployeeEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.setEntity(new WorkEmployeeEntity());
        QueryWrapper.eq("log_user_id", userId);
        QueryWrapper.eq("corp_id", corpId);
        QueryWrapper.eq("status", 1);
        List<WorkEmployeeEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList;
    }

    /**
     * @description: 查询登录用户通讯录信息
     * @return:
     * @author: Huayu
     * @time: 2020/12/1 14:26
     */
    @Override
    public List<WorkEmployeeEntity> getWorkEmployeeByCorpIdLogUserId(String corpId, String userId) {
        QueryWrapper<WorkEmployeeEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("id");
        QueryWrapper.eq("corp_id", corpId);
        QueryWrapper.eq("log_user_id", userId);
        return this.baseMapper.selectList(QueryWrapper);
    }

    /**
     * @description: 查询员工
     * @return:
     * @author: Huayu
     * @time: 2020/12/10 18:39
     */
    @Override
    public List<WorkEmployeeEntity> getWorkEmployeesById(String ownerIdArr) {
        QueryWrapper<WorkEmployeeEntity> QueryWrapper = new QueryWrapper<WorkEmployeeEntity>();
        QueryWrapper.select("id,corp_id as corpId,name");
        QueryWrapper.in("id", ownerIdArr);
        return this.baseMapper.selectList(QueryWrapper);
    }

    /**
     * @description:企业通讯录成员模糊匹配
     * @author: Huayu
     * @time: 2020/12/16 11:22
     */
    @Override
    public List<WorkEmployeeEntity> getWorkEmployeesByCorpIdName(Integer corpId, String name, String clStr) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.select(clStr);
        workEmployeeEntityQueryWrapper.eq("corp_id", corpId);
        workEmployeeEntityQueryWrapper.like("name", name);
        return this.baseMapper.selectList(workEmployeeEntityQueryWrapper);
    }

    @Override
    public List<WorkEmployeeEntity> getWorkEmployeesByCorpId(Integer corpId, String clStr) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.select(clStr);
        workEmployeeEntityQueryWrapper.eq("corp_id", corpId);
        return this.baseMapper.selectList(workEmployeeEntityQueryWrapper);
    }

    @Override
    public List<WorkEmployeeEntity> getWorkEmployeesByMobile(String phone, String clStr) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.select(clStr);
        workEmployeeEntityQueryWrapper.eq("mobile", phone);
        return this.baseMapper.selectList(workEmployeeEntityQueryWrapper);
    }

    @Override
    public List<WorkEmployeeEntity> getWorkEmployeeList(String page, String perPage, String clStr, String empIdArr) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.select(clStr);
        if (empIdArr != null && !empIdArr.equals("")) {
            String[] empIdStr = empIdArr.split(",");
            List<String> list = new ArrayList();
            for (String str :
                    empIdStr) {
                list.add(str);
            }
            workEmployeeEntityQueryWrapper.in("id", list);
        }
        IPage<WorkEmployeeEntity> pageModel = new Page<WorkEmployeeEntity>();
        pageModel.setCurrent(Long.parseLong(page));
        pageModel.setSize(Long.parseLong(perPage));
        return this.baseMapper.selectPage(pageModel, workEmployeeEntityQueryWrapper).getRecords();
    }

    @Override
    public List<WorkEmployeeEntity> getWorkEmployeesByCorpIdsWxUserId(Integer corpId, List<String> participantIdArr, String s) {
        QueryWrapper<WorkEmployeeEntity> workEmployeeEntityQueryWrapper = new QueryWrapper();
        workEmployeeEntityQueryWrapper.select(s);
        StringBuilder sb = new StringBuilder();
        for (String str :
                participantIdArr) {
            sb.append(str).append(",");
        }
        String participantIdStr = sb.substring(0, sb.length() - 1);
        workEmployeeEntityQueryWrapper.in(participantIdStr);
        workEmployeeEntityQueryWrapper.eq("corp_id", corpId);
        return this.baseMapper.selectList(workEmployeeEntityQueryWrapper);
    }

    /**
     * 根据员工 id 列表获取员工微信 id
     *
     * @param empIdList
     */
    @Override
    public List<String> getWxEmpIdListByEmpIdList(List<Integer> empIdList) {
        if (Objects.isNull(empIdList) || empIdList.isEmpty()) {
            return Collections.emptyList();
        }

        return listByIds(empIdList).stream().map(WorkEmployeeEntity::getWxUserId).collect(Collectors.toList());
    }

    @Override
    public List<Integer> listCorpIdByLoginUserId(Integer loginUserId) {
        List<WorkEmployeeEntity> entityList = lambdaQuery()
                .select(WorkEmployeeEntity::getCorpId)
                .eq(WorkEmployeeEntity::getLogUserId, loginUserId)
                .groupBy(WorkEmployeeEntity::getCorpId)
                .list();
        if (entityList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return entityList.stream()
                    .map(WorkEmployeeEntity::getCorpId)
                    .collect(Collectors.toList());
        }
    }
}
