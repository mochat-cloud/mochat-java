package com.mochat.mochat.service.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.businesslog.EventEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.entity.channel.ChannelCodeEntity;
import com.mochat.mochat.dao.entity.channel.ChannelCodeGroupEntity;
import com.mochat.mochat.dao.entity.medium.MediumEntity;
import com.mochat.mochat.dao.mapper.channel.ChannelCodeMapper;
import com.mochat.mochat.job.sync.WorkChannelCodeSyncLogic;
import com.mochat.mochat.model.channel.*;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.businesslog.IBusinessLogService;
import com.mochat.mochat.service.contact.IWorkContactTagGroupService;
import com.mochat.mochat.service.emp.IWorkDeptService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.IWorkContactEmployeeService;
import com.mochat.mochat.service.impl.IWorkContactService;
import com.mochat.mochat.service.impl.IWorkContactTagService;
import com.mochat.mochat.service.impl.medium.IMediumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: yangpengwei
 * @time: 2021/2/22 5:06 下午
 * @description 渠道码服务实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class ChannelCodeServiceImpl extends ServiceImpl<ChannelCodeMapper, ChannelCodeEntity> implements IChannelCodeService {

    @Autowired
    private IChannelCodeGroupService channelCodeGroupService;

    @Autowired
    private IWorkContactEmployeeService contactEmployeeService;

    @Autowired
    private IWorkContactService workContactService;

    @Autowired
    private IWorkEmployeeService workEmployeeService;

    @Autowired
    private IWorkDeptService deptService;

    @Autowired
    private WorkChannelCodeSyncLogic workChannelCodeSyncLogic;

    @Autowired
    private IWorkContactTagService workContactTagService;

    @Autowired
    private IWorkContactTagGroupService workContactTagGroupService;

    @Autowired
    private IBusinessLogService businessLogService;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    @Autowired
    private IMediumService mediumService;

    @Override
    public void storeOrUpdateCode(ReqChannelCodeDTO req) {
        String codeName = req.getBaseInfo().getName();
        if (Objects.isNull(codeName) || codeName.isEmpty()) {
            throw new ParamException("活码名称不能为空");
        }

        List<Integer> tagIdList = req.getBaseInfo().getTags();
        if (tagIdList.isEmpty()) {
            throw new ParamException("请添加客户标签");
        }
        int tagCount = workContactTagService.lambdaQuery()
                .eq(WorkContactTagEntity::getCorpId, AccountService.getCorpId())
                .in(WorkContactTagEntity::getId, tagIdList)
                .count();
        if (tagCount < tagIdList.size()) {
            throw new ParamException("客户标签失效, 请刷新页面");
        }

        // 判断是否属于特殊时期
        Map<String, List<?>> map = checkSpecialDate(req.getDrainageEmployee().getSpecialPeriod());
        // 获取成员 id
        if (Objects.isNull(map)) {
            map = checkWeek(req.getDrainageEmployee());
        }

        if (Objects.isNull(map)) {
            throw new ParamException("数据格式异常");
        }

        ChannelCodeEntity entity = new ChannelCodeEntity();
        entity.setCorpId(AccountService.getCorpId());
        entity.setGroupId(req.getBaseInfo().getGroupId());
        entity.setName(req.getBaseInfo().getName());
        entity.setAutoAddFriend(req.getBaseInfo().getAutoAddFriend());
        entity.setTags(JSON.toJSONString(req.getBaseInfo().getTags()));
        entity.setType(req.getDrainageEmployee().getType());
        entity.setDrainageEmployee(JSON.toJSONString(req.getDrainageEmployee()));
        entity.setWelcomeMessage(JSON.toJSONString(req.getWelcomeMessage()));

        boolean isUpdate = Objects.nonNull(req.getChannelCodeId());
        if (isUpdate) {
            entity.setId(req.getChannelCodeId());
        }
        boolean result = entity.insertOrUpdate();

        if (result) {
            workChannelCodeSyncLogic.onCreateWxAddContactWayQrcode(entity, map);
        } else {
            String msg = isUpdate ? "渠道活码创建失败" : "渠道活码更新失败";
            throw new CommonException(msg);
        }

        // 记录业务日志
        businessLogService.createBusinessLog(entity.getId(), entity,
                isUpdate ? EventEnum.CHANNEL_CODE_CREATE : EventEnum.CHANNEL_CODE_UPDATE);
    }

    private Map<String, List<?>> checkSpecialDate(DrainageEmployeeDTO.SpecialPeriodDTO employeeDTO) {
        if (2 != employeeDTO.getStatus()) {
            for (DrainageEmployeeDTO.SpecialPeriodDTO.DetailDTO detailDTO :
                    employeeDTO.getDetail()) {
                if (DateUtils.inDateByS3(detailDTO.getStartDate(), detailDTO.getEndDate())) {
                    for (DrainageEmployeeDTO.SpecialPeriodDTO.DetailDTO.TimeSlotDTO timeSlotDTO :
                            detailDTO.getTimeSlot()) {
                        if (DateUtils.inTimeByS5(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime())) {
                            Map<String, List<?>> map = new HashMap<>(2);
                            // 成员 id 数组
                            map.put("eIds", workEmployeeService.getWxEmpIdListByEmpIdList(timeSlotDTO.getEmployeeId()));
                            // 部门 id 数组
                            map.put("dId", deptService.getWxDeptIdListByDeptIdList(timeSlotDTO.getDepartmentId()));
                            return map;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Map<String, List<?>> checkWeek(DrainageEmployeeDTO drainageEmployeeDTO) {
        for (DrainageEmployeeDTO.EmployeesDTO employeesDTO :
                drainageEmployeeDTO.getEmployees()) {
            if (DateUtils.getDayOfWeek() == employeesDTO.getWeek()) {
                for (DrainageEmployeeDTO.EmployeesDTO.TimeSlotDTO timeSlotDTO :
                        employeesDTO.getTimeSlot()) {
                    if (DateUtils.inTimeByS5(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime())) {
                        Map<String, List<?>> map = new HashMap<>(2);
                        // 成员 id 数组
                        map.put("eIds", workEmployeeService.getWxEmpIdListByEmpIdList(timeSlotDTO.getEmployeeId()));
                        // 部门 id 数组
                        map.put("dId", deptService.getWxDeptIdListByDeptIdList(timeSlotDTO.getDepartmentId()));
                        return map;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public RespChannelCodeVO getChannelCodeDetail(Integer codeId) {
        ChannelCodeEntity entity = getById(codeId);
        if (Objects.isNull(entity)) {
            throw new ParamException("渠道码不存在");
        }

        RespChannelCodeVO vo = new RespChannelCodeVO();
        setBaseInfoVO(entity, vo);
        setDrainageEmployeeVO(entity, vo);
        setWelcomeMessageVO(entity, vo);
        return vo;
    }

    private void setBaseInfoVO(ChannelCodeEntity entity, RespChannelCodeVO vo) {
        RespChannelCodeVO.BaseInfoVO baseInfoVO = new RespChannelCodeVO.BaseInfoVO();

        ChannelCodeGroupEntity groupEntity = channelCodeGroupService.getById(entity.getGroupId());
        baseInfoVO.setGroupId(groupEntity.getId());
        baseInfoVO.setGroupName(groupEntity.getName());
        baseInfoVO.setName(entity.getName());
        baseInfoVO.setAutoAddFriend(entity.getAutoAddFriend());

        List<Integer> tagIdList = JSON.parseArray(entity.getTags(), Integer.class);
        baseInfoVO.setSelectedTags(tagIdList);

        List<WorkContactTagGroupEntity> tagGroupEntityList = workContactTagGroupService.lambdaQuery()
                .eq(WorkContactTagGroupEntity::getCorpId, AccountService.getCorpId())
                .list();
        List<RespChannelCodeVO.BaseInfoVO.TagsDTO> tagsDTOList = new ArrayList<>();
        for (WorkContactTagGroupEntity tagGroupEntity : tagGroupEntityList) {
            RespChannelCodeVO.BaseInfoVO.TagsDTO tagsDTO = new RespChannelCodeVO.BaseInfoVO.TagsDTO();
            tagsDTO.setGroupId(tagGroupEntity.getId());
            tagsDTO.setGroupName(tagGroupEntity.getGroupName());

            List<WorkContactTagEntity> tagEntityList = workContactTagService.lambdaQuery()
                    .eq(WorkContactTagEntity::getCorpId, tagGroupEntity.getCorpId())
                    .eq(WorkContactTagEntity::getContactTagGroupId, tagGroupEntity.getId())
                    .list();
            List<RespChannelCodeVO.BaseInfoVO.TagsDTO.ListDTO> tagDTOList = new ArrayList<>();
            for (WorkContactTagEntity tagEntity : tagEntityList) {
                RespChannelCodeVO.BaseInfoVO.TagsDTO.ListDTO tagDTO = new RespChannelCodeVO.BaseInfoVO.TagsDTO.ListDTO();
                tagDTO.setTagId(tagEntity.getId());
                tagDTO.setTagName(tagEntity.getName());
                tagDTO.setIsSelected(tagIdList.contains(tagEntity.getId()) ? 1 : 2);
                tagDTOList.add(tagDTO);
            }

            tagsDTO.setList(tagDTOList);
            tagsDTOList.add(tagsDTO);
        }
        baseInfoVO.setTags(tagsDTOList);

        vo.setBaseInfo(baseInfoVO);
    }

    private void setDrainageEmployeeVO(ChannelCodeEntity entity, RespChannelCodeVO vo) {
        DrainageEmployeeVO drainageEmployeeVO = JSON.parseObject(entity.getDrainageEmployee(), DrainageEmployeeVO.class);
        setAddMaxVO(drainageEmployeeVO);
        vo.setDrainageEmployee(drainageEmployeeVO);
    }

    private void setAddMaxVO(DrainageEmployeeVO drainageEmployeeVO) {
        DrainageEmployeeVO.AddMaxVO addMaxVO = drainageEmployeeVO.getAddMax();
        List<Integer> empIdList = addMaxVO.getSpareEmployeeIds();
        if (empIdList.isEmpty()) {
            addMaxVO.setSpareEmployeeName(Collections.emptyList());
            return;
        }

        List<String> empNameList = workEmployeeService.lambdaQuery()
                .select(WorkEmployeeEntity::getName)
                .in(WorkEmployeeEntity::getId, empIdList)
                .list()
                .stream()
                .map(WorkEmployeeEntity::getName)
                .collect(Collectors.toList());
        addMaxVO.setSpareEmployeeName(empNameList);
    }

    private void setWelcomeMessageVO(ChannelCodeEntity entity, RespChannelCodeVO vo) {
        WelcomeMessageVO welcomeMessageVO = JSON.parseObject(entity.getWelcomeMessage(), WelcomeMessageVO.class);
        setWelcomeMessageContent(welcomeMessageVO);
        vo.setWelcomeMessage(welcomeMessageVO);
    }

    private void setWelcomeMessageContent(WelcomeMessageVO welcomeMessageVO) {
        List<JSONObject> messageDetailVOList = welcomeMessageVO.getMessageDetail();
        JSONObject messageDetailVO1 = messageDetailVOList.get(0);
        setMediumToMessageContent(messageDetailVO1);

        JSONObject messageDetailVO2 = messageDetailVOList.get(1);
        JSONArray detailArray2 = messageDetailVO2.getJSONArray("detail");
        if (detailArray2 != null && detailArray2.size() > 0) {
            for (int i = 0; i < detailArray2.size(); i++) {
                JSONObject detail2 = detailArray2.getJSONObject(i);
                JSONArray timeSlotArray2 = detail2.getJSONArray("timeSlot");
                if (timeSlotArray2 != null && timeSlotArray2.size() > 0) {
                    for (int i1 = 0; i1 < timeSlotArray2.size(); i1++) {
                        JSONObject timeSlot2 = timeSlotArray2.getJSONObject(i);
                        setMediumToMessageContent(timeSlot2);
                    }
                }
            }
        }

        JSONObject messageDetailVO3 = messageDetailVOList.get(2);
        JSONArray detailArray3 = messageDetailVO3.getJSONArray("detail");
        if (detailArray3 != null && detailArray3.size() > 0) {
            for (int i = 0; i < detailArray3.size(); i++) {
                JSONObject detail3 = detailArray3.getJSONObject(i);
                JSONArray timeSlotArray3 = detail3.getJSONArray("timeSlot");
                if (timeSlotArray3 != null && timeSlotArray3.size() > 0) {
                    for (int i1 = 0; i1 < timeSlotArray3.size(); i1++) {
                        JSONObject timeSlot3 = timeSlotArray3.getJSONObject(i);
                        setMediumToMessageContent(timeSlot3);
                    }
                }
            }
        }
    }

    private void setMediumToMessageContent(JSONObject jsonObject) {
        int mediumId = jsonObject.getIntValue("mediumId");
        jsonObject.put("content", JSONObject.parseObject(getMediumContentById(mediumId)));
    }

    private String getMediumContentById(int mediumId) {
        if (mediumId > 0) {
            MediumEntity mediumEnyity = mediumService.getMediumById(mediumId);
            return mediumEnyity.getContent();
        } else {
            return "{}";
        }
    }

    @Override
    public Map<String, String> getWelcomeMessageMap(ChannelCodeEntity channelCodeEntity) {
        WelcomeMessageDTO welcomeMessageDTO =
                JSON.parseObject(channelCodeEntity.getWelcomeMessage(), WelcomeMessageDTO.class);
        boolean open = 1 == welcomeMessageDTO.getScanCodePush();
        if (open) {
            List<WelcomeMessageDTO.MessageDetailDTO> messageDetailDTOList = welcomeMessageDTO.getMessageDetail();
            if (Objects.nonNull(messageDetailDTOList) && messageDetailDTOList.size() > 0) {
                Map<String, String> map = null;
                int size = messageDetailDTOList.size();
                if (3 == size) {
                    WelcomeMessageDTO.MessageDetailDTO messageDetailDTO = messageDetailDTOList.get(2);
                    map = checkWelcomeSpecial(messageDetailDTO);
                }
                if (Objects.isNull(map) || 2 == size) {
                    WelcomeMessageDTO.MessageDetailDTO messageDetailDTO = messageDetailDTOList.get(1);
                    map = checkWelcomeWeek(messageDetailDTO);
                }
                if (Objects.isNull(map) || 1 == size) {
                    WelcomeMessageDTO.MessageDetailDTO messageDetailDTO = messageDetailDTOList.get(0);
                    String welcomeContent = messageDetailDTO.getWelcomeContent();
                    int mediumId = messageDetailDTO.getMediumId();

                    map = new HashMap<>(2);
                    map.put("welcomeContent", welcomeContent);
                    map.put("content", getMediumContentById(mediumId));
                }
                return map;
            }
        }
        return null;
    }

    private Map<String, String> checkWelcomeSpecial(WelcomeMessageDTO.MessageDetailDTO messageDetailDTO) {
        if (1 == messageDetailDTO.getStatus()) {
            for (WelcomeMessageDTO.MessageDetailDTO.DetailDTO detailDTO :
                    messageDetailDTO.getDetail()) {
                if (DateUtils.inDateByS3(detailDTO.getStartDate(), detailDTO.getEndDate())) {
                    for (WelcomeMessageDTO.MessageDetailDTO.DetailDTO.TimeSlotDTO timeSlotDTO :
                            detailDTO.getTimeSlot()) {
                        if (DateUtils.inTimeByS5(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime())) {
                            String welcomeContent = timeSlotDTO.getWelcomeContent();
                            int mediumId = timeSlotDTO.getMediumId();

                            Map<String, String> map = new HashMap<>(2);
                            map.put("welcomeContent", welcomeContent);
                            map.put("content", getMediumContentById(mediumId));
                            return map;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Map<String, String> checkWelcomeWeek(WelcomeMessageDTO.MessageDetailDTO messageDetailDTO) {
        if (1 == messageDetailDTO.getStatus()) {
            for (WelcomeMessageDTO.MessageDetailDTO.DetailDTO detailDTO :
                    messageDetailDTO.getDetail()) {
                if (detailDTO.getChooseCycle().contains(DateUtils.getDayOfWeek())) {
                    for (WelcomeMessageDTO.MessageDetailDTO.DetailDTO.TimeSlotDTO timeSlotDTO :
                            detailDTO.getTimeSlot()) {
                        if (DateUtils.inTimeByS5(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime())) {
                            String welcomeContent = timeSlotDTO.getWelcomeContent();
                            int mediumId = timeSlotDTO.getMediumId();

                            Map<String, String> map = new HashMap<>(2);
                            map.put("welcomeContent", welcomeContent);
                            map.put("content", getMediumContentById(mediumId));
                            return map;
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * @author: yangpengwei
     * @time: 2021/3/1 3:02 下午
     * @description 编辑渠道码所在分组
     */
    @Override
    public void updateGroupId(Integer codeId, Integer codeGroupId) {
        QueryWrapper<ChannelCodeEntity> codeQueryWrapper = new QueryWrapper<>();
        codeQueryWrapper.eq("corp_id", AccountService.getCorpId());
        codeQueryWrapper.eq("id", codeId);
        int codeCount = count(codeQueryWrapper);
        if (codeCount < 1) {
            throw new ParamException("渠道码 id 不存在");
        }

        if (codeGroupId != 0) {
            QueryWrapper<ChannelCodeGroupEntity> codeGroupQueryWrapper = new QueryWrapper<>();
            codeGroupQueryWrapper.eq("corp_id", AccountService.getCorpId());
            codeGroupQueryWrapper.eq("id", codeGroupId);
            int codeGroupCount = channelCodeGroupService.count(codeGroupQueryWrapper);
            if (codeGroupCount < 1) {
                throw new ParamException("渠道码分组 id 不存在");
            }
        }

        ChannelCodeEntity.builder()
                .id(codeId)
                .corpId(AccountService.getCorpId())
                .groupId(codeGroupId)
                .build()
                .updateById();
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/1 3:00 下午
     * @description 获取渠道码 - 客户列表
     */
    @Override
    public Page<RespChannelCodeContactVO> getChannelCodeContactByReq(Integer channelCodeId, RequestPage page) {
        QueryWrapper<WorkContactEmployeeEntity> queryContactEmployeeWrapper = Wrappers.query();
        queryContactEmployeeWrapper.select("contact_id c_id", "GROUP_CONCAT(employee_id) e_ids");
        queryContactEmployeeWrapper.eq("state", "channelCode-" + channelCodeId);
        queryContactEmployeeWrapper.eq("corp_id", AccountService.getCorpId());
        queryContactEmployeeWrapper.groupBy("contact_id");
        Page<Map<String, Object>> pageQuery = ApiRespUtils.initPage(page);
        contactEmployeeService.pageMaps(pageQuery, queryContactEmployeeWrapper);

        Page<RespChannelCodeContactVO> pageResult = ApiRespUtils.initPage(page);
        List<RespChannelCodeContactVO> voList = new ArrayList<>();
        for (Map<String, Object> map : pageQuery.getRecords()) {
            RespChannelCodeContactVO vo = new RespChannelCodeContactVO();

            String cId = map.get("c_id").toString();
            String eIds = map.get("e_ids").toString();

            List<String> eIdList = Arrays.asList(eIds.split(","));
            QueryWrapper<WorkEmployeeEntity> queryEmployeeWrapper = Wrappers.query();
            queryEmployeeWrapper.select("GROUP_CONCAT(`name`) `name`");
            queryEmployeeWrapper.in("id", eIdList);
            queryEmployeeWrapper.eq("corp_id", AccountService.getCorpId());
            List<Map<String, Object>> eListMap = workEmployeeService.listMaps(queryEmployeeWrapper);
            String names = "";
            if (!eListMap.isEmpty()) {
                names = eListMap.get(0).get("name").toString();
            }

            // 查询客户名
            WorkContactEntity contactEntity = workContactService.getById(cId);
            vo.setContactId(contactEntity.getId());
            vo.setName(contactEntity.getName());
            vo.setEmployees(Arrays.asList(names.split(",")));
            vo.setCreateTime(DateUtils.formatS1(contactEntity.getCreatedAt().getTime()));
            voList.add(vo);
        }

        pageResult.setRecords(voList);
        pageResult.setTotal(pageQuery.getTotal());
        pageResult.setPages(pageQuery.getPages());

        return pageResult;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/1 3:00 下午
     * @description 获取渠道码列表
     */
    @Override
    public Page<RespChannelCodeListVO> getChannelCodePageByReq(ReqChannelCodeListDTO req, RequestPage page, ReqPerEnum permission) {

        Page<ChannelCodeEntity> codeEntityPage = ApiRespUtils.initPage(page);
        LambdaQueryChainWrapper<ChannelCodeEntity> lambdaQueryWrapper = lambdaQuery();
        // 权限管理查询配置
        setWrapperPermission(lambdaQueryWrapper, permission);

        lambdaQueryWrapper.eq(ChannelCodeEntity::getCorpId, AccountService.getCorpId());

        String name = req.getName();
        if (StringUtils.hasLength(name)) {
            lambdaQueryWrapper.like(ChannelCodeEntity::getName, name);
        }

        Integer type = req.getType();
        if (!Objects.isNull(type) && type > 0 && type < 3) {
            lambdaQueryWrapper.eq(ChannelCodeEntity::getType, type);
        }

        Integer groupId = req.getGroupId();
        if (!Objects.isNull(groupId)) {
            lambdaQueryWrapper.eq(ChannelCodeEntity::getGroupId, groupId);
        }

        lambdaQueryWrapper.page(codeEntityPage);

        List<ChannelCodeEntity> codeEntityList = codeEntityPage.getRecords();
        List<RespChannelCodeListVO> voList = new ArrayList<>();
        for (ChannelCodeEntity e : codeEntityList) {
            RespChannelCodeListVO vo = new RespChannelCodeListVO();
            vo.setChannelCodeId(e.getId());
            vo.setQrcodeUrl(AliyunOssUtils.getUrl(e.getQrcodeUrl()));
            vo.setName(e.getName());

            String typeStr = "";
            if (1 == e.getType()) {
                typeStr = "单人";
            } else if (2 == e.getType()) {
                typeStr = "多人";
            }
            vo.setType(typeStr);

            ChannelCodeGroupEntity groupEntity = channelCodeGroupService.getById(e.getGroupId());
            if (Objects.isNull(groupEntity)) {
                vo.setGroupName("未分组");
            } else {
                vo.setGroupName(groupEntity.getName());
            }

            int countNum = contactEmployeeService.lambdaQuery()
                    .eq(WorkContactEmployeeEntity::getCorpId, AccountService.getCorpId())
                    .eq(WorkContactEmployeeEntity::getState, "channelCode-" + e.getId())
                    .count();
            vo.setContactNum(countNum);

            List<String> tagNames = new ArrayList<>();
            List<Integer> tagIdList = JSON.parseArray(e.getTags(), Integer.class);
            if (!Objects.isNull(tagIdList) && !tagIdList.isEmpty()) {
                List<WorkContactTagEntity> tagEntityList = workContactTagService.listByIds(tagIdList);
                tagNames.addAll(tagEntityList.stream().map(WorkContactTagEntity::getName).collect(Collectors.toList()));
                vo.setTags(tagNames);
            }
            vo.setTags(tagNames);
            vo.setAutoAddFriend(e.getAutoAddFriend() == 1 ? "自动添加好友" : "验证添加好友");
            voList.add(vo);
        }

        return ApiRespUtils.transPage(codeEntityPage, voList);
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private void setWrapperPermission(LambdaQueryChainWrapper<ChannelCodeEntity> wrapper, ReqPerEnum permission) {
        if (permission == ReqPerEnum.ALL) {
            return;
        }

        LambdaQueryChainWrapper<BusinessLogEntity> logWrapper = businessLogService.lambdaQuery();
        if (permission == ReqPerEnum.DEPARTMENT) {
            // 查询员工所属的部门 id 列表
            List<Integer> idList = employeeDepartmentService.getDeptAndChildDeptEmpIdList();
            logWrapper.in(BusinessLogEntity::getOperationId, idList);
        }

        if (permission == ReqPerEnum.EMPLOYEE) {
            int empId = AccountService.getEmpId();
            logWrapper.eq(BusinessLogEntity::getOperationId, empId);
        }

        // 渠道码业务 id 列表 (渠道码 id 列表)
        List<Integer> idList = logWrapper.in(
                BusinessLogEntity::getEvent,
                Arrays.asList(EventEnum.CHANNEL_CODE_CREATE.getCode(), EventEnum.CHANNEL_CODE_UPDATE.getCode())
        ).list().stream().map(BusinessLogEntity::getBusinessId).collect(Collectors.toList());

        if (idList.isEmpty()) {
            wrapper.eq(ChannelCodeEntity::getId, -1);
        } else {
            wrapper.in(ChannelCodeEntity::getId, idList);
        }
    }

    @Override
    public void updateChannelCodeQr(Integer channelCodeId) {
        ChannelCodeEntity entity = getById(channelCodeId);

        DrainageEmployeeDTO dto = JSON.parseObject(entity.getDrainageEmployee(), DrainageEmployeeDTO.class);

        // 判断是否属于特殊时期
        Map<String, List<?>> map = checkSpecialDate(dto.getSpecialPeriod());
        // 获取成员 id
        if (Objects.isNull(map)) {
            map = checkWeek(dto);
        }

        if (Objects.isNull(map)) {
            throw new ParamException("数据格式异常");
        }

        workChannelCodeSyncLogic.onUpdateWxAddContactWayQrcode(entity.getId(), map);
    }
}
