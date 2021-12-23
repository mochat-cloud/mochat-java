package com.mochat.mochat.service.workroom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.businesslog.EventEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.mapper.WorkRoomAutoPullMapper;
import com.mochat.mochat.job.sync.WorkRoomAutoPullSyncLogic;
import com.mochat.mochat.model.workroom.ReqRoomAutoPullCreateDTO;
import com.mochat.mochat.model.workroom.ReqRoomAutoPullUpdateDTO;
import com.mochat.mochat.model.workroom.WorkRoomAutoPullDetailVO;
import com.mochat.mochat.model.workroom.WorkRoomAutoPullVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.business.IBusinessLogService;
import com.mochat.mochat.service.contact.IWorkContactTagGroupService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.IWorkContactRoomService;
import com.mochat.mochat.service.impl.IWorkContactTagService;
import com.mochat.mochat.dao.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/15 4:39 下午
 * @description 自动拉群管理
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class WorkRoomAutoPullServiceImp extends ServiceImpl<WorkRoomAutoPullMapper, WorkRoomAutoPullEntity> implements IWorkRoomAutoPullService {

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private IWorkContactTagGroupService contactTagGroupService;

    @Autowired
    private IWorkContactTagService contactTagService;

    @Autowired
    private IWorkRoomService roomService;

    @Autowired
    private IWorkContactRoomService contactRoomService;

    @Autowired
    private WorkRoomAutoPullSyncLogic workRoomAutoPullSyncLogic;

    @Autowired
    private IBusinessLogService businessLogService;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    /**
     * 获取自动拉群管理 - 列表
     *
     * @param qrcodeName  群活码名称[非必填]
     * @param reqPageDto 分页参数[非必填]
     */
    @Override
    public Page<WorkRoomAutoPullVO> getList(String qrcodeName, ReqPageDto reqPageDto, ReqPerEnum permission) {
        int corpId = AccountService.getCorpId();

        LambdaQueryChainWrapper<WorkRoomAutoPullEntity> wrapper = lambdaQuery()
                .eq(WorkRoomAutoPullEntity::getCorpId, corpId);
        setWrapperPermission(wrapper, permission);

        if (Objects.nonNull(qrcodeName) && !qrcodeName.isEmpty()) {
            wrapper.like(WorkRoomAutoPullEntity::getQrcodeName, qrcodeName);
        }

        Page<WorkRoomAutoPullEntity> pageEntity = ApiRespUtils.initPage(reqPageDto);
        wrapper.page(pageEntity);

        List<WorkRoomAutoPullEntity> autoPullEntityList = pageEntity.getRecords();

        List<WorkRoomAutoPullVO> autoPullVOList = new ArrayList<>();
        for (WorkRoomAutoPullEntity entity : autoPullEntityList) {
            WorkRoomAutoPullVO vo = new WorkRoomAutoPullVO();
            vo.setWorkRoomAutoPullId(entity.getId());

            String qrcodeUrl = AliyunOssUtils.getUrl(entity.getQrcodeUrl());
            vo.setQrcodeUrl(qrcodeUrl);
            vo.setQrcodeName(entity.getQrcodeName());

            vo.setCreatedAt(DateUtils.formatS1(entity.getCreatedAt().getTime()));

            String empJson = entity.getEmployees();
            List<Integer> empIds = JSON.parseArray(empJson, Integer.class);
            List<String> employeesNameList = employeeService.lambdaQuery()
                    .select(WorkEmployeeEntity::getName)
                    .in(WorkEmployeeEntity::getId, empIds)
                    .list()
                    .stream()
                    .map(WorkEmployeeEntity::getName)
                    .collect(Collectors.toList());

            vo.setEmployees(employeesNameList);

            String tagJson = entity.getTags();
            List<Integer> tagIds = JSON.parseArray(tagJson, Integer.class);
            List<String> tagNameList = contactTagService.lambdaQuery()
                    .select(WorkContactTagEntity::getName)
                    .in(WorkContactTagEntity::getId, tagIds)
                    .list()
                    .stream()
                    .map(WorkContactTagEntity::getName)
                    .collect(Collectors.toList());

            vo.setTags(tagNameList);

            String roomJson = entity.getRooms();
            JSONArray roomJsonArray = JSON.parseArray(roomJson);
            List<WorkRoomAutoPullVO.RoomsDTO> roomsDTOList = new ArrayList<>();
            boolean nextStart = true;
            int contactNum = 0;
            for (int i = 0; i < roomJsonArray.size(); i++) {
                JSONObject roomJsonObj = roomJsonArray.getJSONObject(i);
                WorkRoomAutoPullVO.RoomsDTO roomDTO = new WorkRoomAutoPullVO.RoomsDTO();
                int roomId = roomJsonObj.getIntValue("roomId");
                WorkRoomEntity roomEntity = roomService.getById(roomId);
                roomDTO.setRoomName(roomEntity.getName());

                int maxNum = roomJsonObj.getIntValue("maxNum");
                int roomMax = roomEntity.getRoomMax();

                int count = contactRoomService.lambdaQuery()
                        .eq(WorkContactRoomEntity::getRoomId, roomEntity.getId())
                        .eq(WorkContactRoomEntity::getType, "2")
                        .eq(WorkContactRoomEntity::getStatus, "1")
                        .count();

                contactNum += count;

                if (nextStart) {
                    if (count < roomMax && count < maxNum) {
                        // 拉人中
                        roomDTO.setStateText("拉人中");
                        nextStart = false;
                    } else {
                        // 已拉满, 换群
                        roomDTO.setStateText("已拉满");
                    }
                } else {
                    roomDTO.setStateText("未开始");
                }

                roomsDTOList.add(roomDTO);
            }
            vo.setContactNum(contactNum);
            vo.setRooms(roomsDTOList);
            autoPullVOList.add(vo);
        }

        return ApiRespUtils.transPage(pageEntity, autoPullVOList);
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private void setWrapperPermission(LambdaQueryChainWrapper<WorkRoomAutoPullEntity> wrapper, ReqPerEnum permission) {
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

        // 业务 id 列表 (自动拉群 id 列表)
        List<Integer> idList = logWrapper.in(
                BusinessLogEntity::getEvent,
                Arrays.asList(EventEnum.ROOM_AUTO_PULL_CREATE.getCode(), EventEnum.ROOM_AUTO_PULL_UPDATE.getCode())
        ).list().stream().map(BusinessLogEntity::getBusinessId).collect(Collectors.toList());

        if (idList.isEmpty()) {
            wrapper.eq(WorkRoomAutoPullEntity::getId, -1);
        } else {
            wrapper.in(WorkRoomAutoPullEntity::getId, idList);
        }
    }

    /**
     * 自动拉群管理 - 创建提交
     * <p>
     * synchronized 为防止重复添加导致微信异步更新出错
     * 添加客户回调需要关联到这里
     */
    @Override
    public synchronized void createRoomAutoPull(ReqRoomAutoPullCreateDTO req) {
        WorkRoomAutoPullEntity entity = new WorkRoomAutoPullEntity();
        entity.setCorpId(req.getCorpId());
        entity.setQrcodeName(req.getQrcodeName());
        entity.setIsVerified(req.getIsVerified());
        entity.setLeadingWords(req.getLeadingWords());
        entity.setTags(JSON.toJSONString(req.getTags().split(",")));
        entity.setEmployees(JSON.toJSONString(req.getEmployees().split(",")));
        entity.setRooms(req.getRooms());

        if (entity.insert()) {
            workRoomAutoPullSyncLogic.onCreateWxAddContactWayQrcode(entity);
        } else {
            throw new CommonException("自动拉群创建失败");
        }

        businessLogService.createBusinessLog(entity.getId(), entity, EventEnum.ROOM_AUTO_PULL_CREATE);
    }

    /**
     * 自动拉群管理 - 更新提交
     */
    @Override
    public void updateRoomAutoPullDetail(ReqRoomAutoPullUpdateDTO req) {
        int autoPullId = req.getWorkRoomAutoPullId();
        WorkRoomAutoPullEntity entity = getById(autoPullId);
        if (entity == null) {
            throw new ParamException("自动拉群 id 不存在");
        }

        entity.setIsVerified(req.getIsVerified());
        entity.setTags(JSON.toJSONString(req.getTags().split(",")));
        entity.setEmployees(JSON.toJSONString(req.getEmployees().split(",")));
        entity.setRooms(req.getRooms());
        entity.updateById();

        // 更新微信 联系我二维码
        int corpId = entity.getCorpId();
        String wxConfigId = entity.getWxConfigId();

        List<String> empIdList = JSON.parseArray(entity.getEmployees(), String.class);
        List<WorkEmployeeEntity> employeeEntityList = employeeService.listByIds(empIdList);
        empIdList.clear();
        for (WorkEmployeeEntity emp : employeeEntityList) {
            empIdList.add(emp.getWxUserId());
        }
        WxApiUtils.requestUpdateContactWay(corpId, wxConfigId, empIdList);

        businessLogService.createBusinessLog(entity.getId(),entity,EventEnum.ROOM_AUTO_PULL_UPDATE);
    }

    /**
     * 自动拉群管理 - 详情
     *
     * @param workRoomAutoPullId 自动拉群ID
     */
    @Override
    public WorkRoomAutoPullDetailVO getRoomAutoPullDetail(Integer workRoomAutoPullId) {
        if (workRoomAutoPullId == null) {
            throw new ParamException("自动拉群 id 无效");
        }

        WorkRoomAutoPullEntity entity = getById(workRoomAutoPullId);
        if (entity == null) {
            throw new ParamException("自动拉群数据不存在");
        }

        WorkRoomAutoPullDetailVO vo = new WorkRoomAutoPullDetailVO();
        vo.setWorkRoomAutoPullId(entity.getId());
        vo.setQrcodeName(entity.getQrcodeName());

        String qrcodeUrl = AliyunOssUtils.getUrl(entity.getQrcodeUrl());
        vo.setQrcodeUrl(qrcodeUrl);

        vo.setIsVerified(entity.getIsVerified());
        vo.setLeadingWords(entity.getLeadingWords());
        vo.setCreatedAt(DateUtils.formatS1(entity.getCreatedAt().getTime()));

        String empJson = entity.getEmployees();
        List<Integer> empIds = JSON.parseArray(empJson, Integer.class);
        List<WorkEmployeeEntity> employeeEntityList = employeeService.listByIds(empIds);
        List<WorkRoomAutoPullDetailVO.EmployeesDTO> employeesDTOList = new ArrayList<>();
        for (WorkEmployeeEntity emp : employeeEntityList) {
            WorkRoomAutoPullDetailVO.EmployeesDTO employeesDTO = new WorkRoomAutoPullDetailVO.EmployeesDTO();
            employeesDTO.setEmployeeId(emp.getId());
            employeesDTO.setEmployeeName(emp.getName());
            employeesDTOList.add(employeesDTO);
        }
        vo.setEmployees(employeesDTOList);

        String tagJson = entity.getTags();
        List<Integer> tagIds = JSON.parseArray(tagJson, Integer.class);
        vo.setSelectedTags(tagIds);

        List<WorkContactTagGroupEntity> tagGroupEntityList = contactTagGroupService.lambdaQuery()
                .select(WorkContactTagGroupEntity::getId, WorkContactTagGroupEntity::getGroupName)
                .eq(WorkContactTagGroupEntity::getCorpId, entity.getCorpId())
                .list();

        List<WorkRoomAutoPullDetailVO.TagsDTO> tagsDTOList = new ArrayList<>();
        for (WorkContactTagGroupEntity tagGroupEntity : tagGroupEntityList) {
            WorkRoomAutoPullDetailVO.TagsDTO tagsDTO = new WorkRoomAutoPullDetailVO.TagsDTO();
            tagsDTO.setGroupId(tagGroupEntity.getId());
            tagsDTO.setGroupName(tagGroupEntity.getGroupName());

            // 查询标签组下面的标签
            List<WorkContactTagEntity> tagEntityList = contactTagService.lambdaQuery()
                    .select(WorkContactTagEntity::getId, WorkContactTagEntity::getName)
                    .eq(WorkContactTagEntity::getContactTagGroupId, tagGroupEntity.getId())
                    .list();

            List<WorkRoomAutoPullDetailVO.TagsDTO.ListDTO> tagDTOList = new ArrayList<>();
            for (WorkContactTagEntity tagEntity : tagEntityList) {
                WorkRoomAutoPullDetailVO.TagsDTO.ListDTO tagDTO = new WorkRoomAutoPullDetailVO.TagsDTO.ListDTO();
                tagDTO.setTagId(tagEntity.getId());
                tagDTO.setTagName(tagEntity.getName());
                tagDTO.setIsSelected(tagIds.contains(tagEntity.getId()) ? 1 : 2);
                tagDTOList.add(tagDTO);
            }

            tagsDTO.setList(tagDTOList);
            tagsDTOList.add(tagsDTO);
        }
        vo.setTags(tagsDTOList);

        String roomJson = entity.getRooms();
        JSONArray roomJsonArray = JSON.parseArray(roomJson);
        List<WorkRoomAutoPullDetailVO.RoomsDTO> roomsDTOList = new ArrayList<>();
        boolean nextStart = true;
        for (int i = 0; i < roomJsonArray.size(); i++) {
            JSONObject roomJsonObj = roomJsonArray.getJSONObject(i);
            WorkRoomAutoPullDetailVO.RoomsDTO roomDTO = new WorkRoomAutoPullDetailVO.RoomsDTO();
            roomDTO.setRoomId(roomJsonObj.getIntValue("roomId"));

            WorkRoomEntity roomEntity = roomService.getById(roomDTO.getRoomId());
            roomDTO.setRoomName(roomEntity.getName());
            roomDTO.setRoomMax(roomEntity.getRoomMax());

            roomDTO.setMaxNum(roomJsonObj.getIntValue("maxNum"));

            int count = contactRoomService.lambdaQuery()
                    .eq(WorkContactRoomEntity::getRoomId, roomEntity.getId())
                    .count();

            roomDTO.setNum(count);

            if (nextStart) {
                if (count < roomDTO.getRoomMax() && count < roomDTO.getMaxNum()) {
                    // 拉人中
                    roomDTO.setState(2);
                    nextStart = false;
                } else {
                    // 已拉满, 换群
                    roomDTO.setState(3);
                }
            } else {
                roomDTO.setState(1);
            }

            roomDTO.setRoomQrcodeUrl(roomJsonObj.getString("roomQrcodeUrl"));
            roomDTO.setLongRoomQrcodeUrl(AliyunOssUtils.getUrl(roomDTO.getRoomQrcodeUrl()));

            roomsDTOList.add(roomDTO);
        }
        vo.setRooms(roomsDTOList);
        vo.setRoomNum(roomsDTOList.size());

        return vo;
    }

    /**
     * @description 获取自动拉群详情
     * @author zhaojinjian
     * @createTime 2020/12/19 16:10
     */
    @Override
    public WorkRoomAutoPullEntity getRoomAutoPullInfo(Integer workRoomAutoPullId) {
        return getById(workRoomAutoPullId);
    }
}
