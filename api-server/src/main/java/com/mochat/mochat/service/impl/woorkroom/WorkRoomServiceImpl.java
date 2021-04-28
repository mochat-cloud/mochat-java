package com.mochat.mochat.service.impl.woorkroom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.em.workcontactroom.JoinSceneEnum;
import com.mochat.mochat.common.em.workcontactroom.Status;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.entity.workroom.WorkRoomGroupEntity;
import com.mochat.mochat.dao.mapper.workroom.WorkRoomMapper;
import com.mochat.mochat.model.WorkRoomIndexRespModel;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.IWorkContactRoomService;
import com.mochat.mochat.service.impl.IWorkContactService;
import com.mochat.mochat.service.workroom.IWorkRoomGroupService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import com.mochat.mochat.model.workroom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:客户群
 * @author: Huayu
 * @time: 2020/12/8 14:42
 */
@Service
public class WorkRoomServiceImpl extends ServiceImpl<WorkRoomMapper, WorkRoomEntity> implements IWorkRoomService {

    @Autowired
    private IWorkEmployeeService workEmployeeServiceImpl;

    @Autowired
    private IWorkRoomGroupService roomGroupService;

    @Autowired
    private IWorkContactRoomService workContactRoomServiceImpl;

    @Autowired
    private IWorkContactService workContactServiceImpl;

    @Autowired
    private ICorpService corpServiceImpl;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    @Override
    @Transactional
    public Integer updateWorkRoomsByRoomGroupId(Integer workRoomGroupId, int roomGroupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("workRoomGroupId", workRoomGroupId);
        map.put("roomGroupId", roomGroupId);
        return baseMapper.updateWorkRoomsByRoomGroupId(map);
    }

    @Override
    public Page<WorkRoomIndexRespModel> getWorkRoomList(WorkRoomIndexModel req, ReqPerEnum permission) {
        Page<WorkRoomEntity> roomEntityPage = ApiRespUtils.initPage(req);
        LambdaQueryChainWrapper<WorkRoomEntity> wrapper = lambdaQuery().eq(WorkRoomEntity::getCorpId, AccountService.getCorpId());
        wrapper.orderByDesc(WorkRoomEntity::getCreateTime);
        if (Objects.nonNull(req.getRoomGroupId())) {
            wrapper.eq(WorkRoomEntity::getRoomGroupId, req.getRoomGroupId());
        }
        if (Objects.nonNull(req.getWorkRoomName()) && !req.getWorkRoomName().isEmpty()) {
            wrapper.eq(WorkRoomEntity::getName, req.getWorkRoomName());
        }

        if (Objects.nonNull(req.getWorkRoomStatus()) && req.getWorkRoomStatus() >= 0 && req.getWorkRoomStatus() <= 3) {
            wrapper.eq(WorkRoomEntity::getStatus, req.getWorkRoomStatus());
        }
        if (Objects.nonNull(req.getStartTime()) && !req.getStartTime().isEmpty()) {
            wrapper.ge(WorkRoomEntity::getCreateTime, req.getStartTime());
        }
        if (req.getEndTime() != null && !req.getEndTime().isEmpty()) {
            wrapper.le(WorkRoomEntity::getCreateTime, req.getEndTime());
        }

        // 权限管理数据过滤
        if (Objects.nonNull(req.getWorkRoomOwnerId()) && !req.getWorkRoomOwnerId().isEmpty()) {
            List<String> ownerIdList = Arrays.asList(req.getWorkRoomOwnerId().split(","));
            setWrapperPermission(wrapper, ownerIdList, permission);
        } else {
            setWrapperPermission(wrapper, Collections.EMPTY_LIST, permission);
        }
        wrapper.page(roomEntityPage);

        List<WorkRoomEntity> workRoomEntityList = roomEntityPage.getRecords();

        List<WorkRoomIndexRespModel> voList = new ArrayList<>();

        String currentDayStr = DateUtils.getDateByS3();
        String currentDayStartStr = DateUtils.getDateOfDayStartByS3(currentDayStr);
        String currentDayEndStr = DateUtils.getDateOfDayEndByS3(currentDayStr);
        long currentDayStartMillis = DateUtils.getMillsByS1(currentDayStartStr);
        long currentDayEndMillis = DateUtils.getMillsByS1(currentDayEndStr);

        //处理列表数据
        for (WorkRoomEntity entity : workRoomEntityList) {
            //群主信息
            WorkEmployeeEntity workEmployee = workEmployeeServiceImpl.getById(entity.getOwnerId());

            WorkRoomIndexRespModel workRoomIndexRespModel = new WorkRoomIndexRespModel();
            workRoomIndexRespModel.setOwnerName(workEmployee.getName());
            // 群成员数量统计
            List<WorkContactRoomEntity> contactRoomEntityList = workContactRoomServiceImpl.getWorkContactRoomsByRoomId(entity.getId());
            // 群成员数量
            Integer memberNum = 0;
            // 今日入群数量
            Integer inRoomNum = 0;
            // 今日退群数量
            Integer outRoomNum = 0;
            if (contactRoomEntityList.size() > 0) {
                for (WorkContactRoomEntity workContactRoomEntity : contactRoomEntityList) {
                    if (workContactRoomEntity.getStatus().equals(1)) {
                        memberNum++;
                    }
                    long joinTime = workContactRoomEntity.getJoinTime().getTime();
                    if (joinTime >= currentDayStartMillis && joinTime <= currentDayEndMillis) {
                        inRoomNum++;
                    }
                    String outTimeStr = workContactRoomEntity.getOutTime();
                    if (Objects.nonNull(outTimeStr) && !outTimeStr.isEmpty()) {
                        long outTime = DateUtils.getMillsByS1(outTimeStr);
                        if (outTime >= currentDayStartMillis && outTime <= currentDayEndMillis) {
                            inRoomNum++;
                        }
                    }
                }
            }
            //组装list数据
            workRoomIndexRespModel.setWorkRoomId(entity.getId());
            workRoomIndexRespModel.setMemberNum(memberNum);
            workRoomIndexRespModel.setRoomName(entity.getName());

            WorkRoomGroupEntity roomGroupEntity = roomGroupService.lambdaQuery()
                    .select(WorkRoomGroupEntity::getName)
                    .eq(WorkRoomGroupEntity::getId, entity.getRoomGroupId())
                    .one();
            if (Objects.nonNull(roomGroupEntity)) {
                workRoomIndexRespModel.setRoomGroup(roomGroupEntity.getName());
            }

            workRoomIndexRespModel.setStatus(entity.getStatus());
            workRoomIndexRespModel.setStatusText(entity.getStatus() == 0 ? "正常" : "退群");
            workRoomIndexRespModel.setInRoomNum(inRoomNum);
            workRoomIndexRespModel.setOutRoomNum(outRoomNum);
            workRoomIndexRespModel.setNotice(entity.getNotice());
            workRoomIndexRespModel.setCreateTime(DateUtils.formatS1(entity.getCreatedAt().getTime()));
            voList.add(workRoomIndexRespModel);

        }

        return ApiRespUtils.transPage(roomEntityPage, voList);
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private void setWrapperPermission(LambdaQueryChainWrapper<WorkRoomEntity> wrapper, List<String> ownerIdList, ReqPerEnum permission) {
        if (ownerIdList.isEmpty()) {
            if (permission == ReqPerEnum.ALL) {
                return;
            }

            if (permission == ReqPerEnum.DEPARTMENT) {
                // 查询员工所属的部门 id 列表
                List<Integer> idList = employeeDepartmentService.getDeptAndChildDeptEmpIdList();
                if (idList.isEmpty()) {
                    wrapper.eq(WorkRoomEntity::getOwnerId, -1);
                } else {
                    wrapper.in(WorkRoomEntity::getOwnerId, idList);
                }
                return;
            }

            if (permission == ReqPerEnum.EMPLOYEE) {
                int empId = AccountService.getEmpId();
                wrapper.eq(WorkRoomEntity::getOwnerId, empId);
                return;
            }
        } else {
            if (permission == ReqPerEnum.ALL) {
                wrapper.in(WorkRoomEntity::getOwnerId, ownerIdList);
                return;
            }

            if (permission == ReqPerEnum.DEPARTMENT) {
                // 查询员工所属的部门 id 列表
                List<Integer> idList = employeeDepartmentService.getDeptAndChildDeptEmpIdList();
                if (idList.isEmpty()) {
                    wrapper.eq(WorkRoomEntity::getOwnerId, -1);
                    return;
                }

                List<String> idListStr = idList.stream().map(integer -> "" + integer).collect(Collectors.toList());
                ownerIdList.retainAll(idListStr);
                if (ownerIdList.isEmpty()) {
                    wrapper.eq(WorkRoomEntity::getOwnerId, -1);
                } else {
                    wrapper.in(WorkRoomEntity::getOwnerId, ownerIdList);
                }
                return;
            }

            if (permission == ReqPerEnum.EMPLOYEE) {
                int empId = AccountService.getEmpId();
                if (ownerIdList.contains("" + empId)) {
                    wrapper.eq(WorkRoomEntity::getOwnerId, empId);
                } else {
                    wrapper.eq(WorkRoomEntity::getOwnerId, -1);
                }
            }
        }
    }

    @Override
    public WorkRoomEntity getWorkRoom(Integer workRoomId) {
        WorkRoomEntity workRoomEntity = this.baseMapper.selectById(workRoomId);
        return workRoomEntity;
    }

    @Override
    public WorkContactRoomIndexResp handelWorkContactRoomData(WorkRoomEntity workRoomEntity, WorkContactRoomIndexReq workContactRoomIndexReq) {
        List<WorkEmployeeEntity> workEmployeeEntityList = null;
        List<WorkContactEntity> workContactEntityList = null;
        String workEmployeeIds = "";
        String workContactIds = "";
        String hadName = "0";
        //处理客户群成员名称
        if (workContactRoomIndexReq.getName() != null) {
            //是否存在成员名称的模糊搜索
            hadName = "1";
            //企业通讯录成员模糊匹配
            workEmployeeEntityList = workEmployeeServiceImpl.getWorkEmployeesByCorpIdName(workRoomEntity.getCorpId(), workContactRoomIndexReq.getName(), "id,name,avatar");
            //企业外部联系人模糊匹配
            workContactEntityList = workContactServiceImpl.getWorkContactsByCorpIdName(workRoomEntity.getCorpId(), workContactRoomIndexReq.getName(), "id,name,avatar");
            if (workEmployeeEntityList.size() == 0 && workContactEntityList.size() == 0) {
                workContactRoomIndexReq = null;
            } else {
                StringBuilder sb = new StringBuilder();
                StringBuilder sb1 = new StringBuilder();
                for (int i = 0; i < workEmployeeEntityList.size(); i++) {
                    sb.append(workEmployeeEntityList.get(i).getId()).append(",");
                }
                workEmployeeIds = workEmployeeEntityList.isEmpty() ? "" : sb.toString().substring(0, sb.toString().length() - 1);
                for (int i = 0; i < workContactEntityList.size(); i++) {
                    sb1.append(workContactEntityList.get(i).getId()).append(",");
                }
                workContactIds = workContactEntityList.isEmpty() ? "" : sb1.toString().substring(0, sb1.toString().length() - 1);

            }
        }

        WorkContactRoomIndexResp workContactRoomIndexResp = getContactRooms(workRoomEntity, workContactRoomIndexReq, workEmployeeIds, workContactIds, hadName, workEmployeeEntityList, workContactEntityList);
        if (workContactRoomIndexResp == null) {
            return null;
        }
        return workContactRoomIndexResp;
    }

    /**
     * @description:通过roomIds找到对应的群名称
     * @return:
     * @author: Huayu
     * @time: 2020/12/17 14:25
     */
    @Override
    public List<WorkRoomEntity> getWorkRoomsByIds(String workContactRoomIds, String clStr) {
        QueryWrapper<WorkRoomEntity> workContactRoomEntity = new QueryWrapper<WorkRoomEntity>();
        workContactRoomEntity.select(clStr);
        List<String> typeList = new ArrayList<>();
        if (clStr != null) {
            String[] typeStr = workContactRoomIds.split(",");
            for (int i = 0; i < typeStr.length; i++) {
                typeList.add(typeStr[i]);
            }
        }
        workContactRoomEntity.in("id", typeList);
        return this.baseMapper.selectList(workContactRoomEntity);
    }


    /**
     * @description:同步客户群列表
     * @return:
     * @author: Huayu
     * @time: 2020/12/17 17:18
     */
    @Override
    public boolean syncWorkRoomIndex(Integer corpIds, List<WXWorkRoomModel> WXWorkRoomModelList, Integer isFlag) {
        CorpEntity corpEntity = corpServiceImpl.getCorpInfoById(corpIds);
        if (isFlag.equals(0)) {
            String result = WxApiUtils.getWorkRoomIndexData(corpEntity, null);
            if (result == null) {
                return false;
            }
            List<WXWorkRoomIdsModel> workRoomModelList = JSONArray.parseArray(result, WXWorkRoomIdsModel.class);
            WXWorkRoomModelList = new ArrayList<WXWorkRoomModel>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (WXWorkRoomIdsModel workRoomModel :
                    workRoomModelList) {
                WXWorkRoomModel WXWorkRoomModel = new WXWorkRoomModel();
                WXWorkRoomModel.setChatId(workRoomModel.getChatId());
                WXWorkRoomModel.setStatus(workRoomModel.getStatus());
                //获取群聊详情
                String workRoomInfo = WxApiUtils.getWorkRoomInfoData(corpEntity, workRoomModel.getChatId());
                try {
                    //合并客户群和客户群详情数据
                    //取出member_list
                    JSONObject workRoomInfoJson = JSONObject.parseObject(workRoomInfo);
                    WXWorkRoomModel.setName(workRoomInfoJson.get("name").toString());
                    WXWorkRoomModel.setOwner(workRoomInfoJson.get("owner").toString());
                    String dateStr = null;
                    dateStr = DateUtils.formatS1(workRoomInfoJson.get("create_time") + "000");
                    WXWorkRoomModel.setCreateTime(Timestamp.valueOf(dateStr));
                    WXWorkRoomModel.setNotice(workRoomInfoJson.get("notice") == null ? "" : workRoomInfoJson.get("notice").toString());
                    JSONArray jsonArray = workRoomInfoJson.getJSONArray("member_list");
                    List<WXWorkRoomInfoModel> resultInfoList = jsonArray.toJavaList(WXWorkRoomInfoModel.class);
                    WXWorkRoomModel.setWXWorkRoomInfoModel(resultInfoList);
                    WXWorkRoomModelList.add(WXWorkRoomModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //获取系统中当前企业所有客户群聊列表
        Map<String, Object> map = handelWXWorkRoomModelData(WXWorkRoomModelList, corpEntity.getCorpId(), isFlag);
        Map<String, Object> workContactRoomModelMap = (Map<String, Object>) map.get("roomList");
        Map<String, Object> employeeMap = (Map<String, Object>) map.get("employeeList");
        Map<String, Object> contactMap = (Map<String, Object>) map.get("contactList");
        //客户群-新增数据
        List<WorkRoomEntity> workRoomEntityCreateList = new ArrayList<WorkRoomEntity>();
        //客户群-更新数据
        List<WorkRoomEntity> workRoomEntityUpdateList = new ArrayList<WorkRoomEntity>();
        //客户群-删除数据
        String deleteContactRoomIdArr = null;
        //客户成员-新增数据
        List<WorkContactRoomEntity> workContactRoomEntityCreateList = new ArrayList<WorkContactRoomEntity>();
        //客户成员-更新数据
        List<WorkContactRoomEntity> workContactRoomEntityUpdateList = new ArrayList<WorkContactRoomEntity>();
        //客户成员-删除数据
        String deleteRoomIdArr = null;
        for (WXWorkRoomModel WXWorkRoomModel :
                WXWorkRoomModelList) {
            WXWorkContactRoomModel currentRoomModel = null;
            WorkRoomEntity workRoomEntity = new WorkRoomEntity();
            if (workContactRoomModelMap.get(WXWorkRoomModel.getChatId()) != null) {
                currentRoomModel = (WXWorkContactRoomModel) workContactRoomModelMap.get(String.valueOf(WXWorkRoomModel.getChatId()));
                //群
                workRoomEntity.setId(currentRoomModel.getId());
                workRoomEntity.setStatus(WXWorkRoomModel.getStatus());
                workRoomEntity.setName(WXWorkRoomModel.getName().isEmpty() ? "群聊" : WXWorkRoomModel.getName());
                Integer owner = (Integer) employeeMap.get(WXWorkRoomModel.getOwner());
                workRoomEntity.setOwnerId(owner != null ? owner : 0);
                workRoomEntity.setNotice(WXWorkRoomModel.getNotice().isEmpty() ? "" : WXWorkRoomModel.getNotice());
                workRoomEntity.setCreatedAt(WXWorkRoomModel.getCreateTime());
                workRoomEntityUpdateList.add(workRoomEntity);
                //删除Key
                workContactRoomModelMap.remove(WXWorkRoomModel.getChatId());
                //群成员
                List<WXWorkRoomInfoModel> WXWorkRoomInfoModelList = WXWorkRoomModel.getWXWorkRoomInfoModel();
                for (WXWorkRoomInfoModel WXWorkRoomInfoModel1 :
                        WXWorkRoomInfoModelList) {
                    Integer contactId = 0;
                    Integer employeeId = 0;
                    if (WXWorkRoomInfoModel1.getType().equals(1)) {
                        employeeId = employeeMap.get(WXWorkRoomInfoModel1.getUserid()) != null ? (Integer) employeeMap.get(WXWorkRoomInfoModel1.getUserid()) : 0;
                    } else {
                        contactId = contactMap.get(WXWorkRoomInfoModel1.getUserid()) != null ? (Integer) contactMap.get(WXWorkRoomInfoModel1.getUserid()) : 0;
                    }
                    if (currentRoomModel.getWXWorkContactRoomInfoModelListMap().get(WXWorkRoomInfoModel1.getUserid()) != null) {
                        WorkContactRoomEntity workContactRoomEntity = new WorkContactRoomEntity();
                        List<WorkContactRoomEntity> workContactRoomEntityList = workContactRoomServiceImpl.getWorkContactRoomsByRoomId(currentRoomModel.getId());
                        Integer id = workContactRoomEntityList.get(0).getId();
                        workContactRoomEntity.setId(id);
                        workContactRoomEntity.setContactId(contactId);
                        workContactRoomEntity.setEmployeeId(employeeId);
                        workContactRoomEntity.setUnionid(WXWorkRoomInfoModel1.getUnionid() == null ? "" : WXWorkRoomInfoModel1.getUnionid());
                        workContactRoomEntity.setJoinScene(WXWorkRoomInfoModel1.getJoin_scene());
                        workContactRoomEntity.setType(WXWorkRoomInfoModel1.getType());
                        workContactRoomEntity.setStatus(1);
                        workContactRoomEntity.setJoinTime(WXWorkRoomInfoModel1.getJoin_time());
                        workContactRoomEntity.setOutTime("");
                        workContactRoomEntityUpdateList.add(workContactRoomEntity);
                        //删除wxuserid的key
                        currentRoomModel.getWXWorkContactRoomInfoModelListMap().remove(WXWorkRoomInfoModel1.getUserid().toString());
                    } else {
                        WorkContactRoomEntity workContactRoomEntity = new WorkContactRoomEntity();
                        workContactRoomEntity.setWxUserId(WXWorkRoomInfoModel1.getUserid());
                        workContactRoomEntity.setContactId(contactId);
                        workContactRoomEntity.setRoomId(currentRoomModel.getId());
                        workContactRoomEntity.setEmployeeId(employeeId);
                        workContactRoomEntity.setRoomCase(WXWorkRoomModel.getChatId());
                        workContactRoomEntity.setUnionid(WXWorkRoomInfoModel1.getUnionid() == null ? "" : WXWorkRoomInfoModel1.getUnionid());
                        workContactRoomEntity.setJoinScene(WXWorkRoomInfoModel1.getJoin_scene());
                        workContactRoomEntity.setType(WXWorkRoomInfoModel1.getType());
                        workContactRoomEntity.setStatus(1);
                        workContactRoomEntity.setJoinTime(WXWorkRoomInfoModel1.getJoin_time());
                        workContactRoomEntity.setOutTime("");
                        workContactRoomEntityCreateList.add(workContactRoomEntity);
                    }
                }
                //获得要删除的客户-客户群id
                if (currentRoomModel.getWXWorkContactRoomInfoModelListMap().size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String key :
                            currentRoomModel.getWXWorkContactRoomInfoModelListMap().keySet()) {
                        WXWorkContactRoomInfoModel WXWorkContactRoomInfoModel = (WXWorkContactRoomInfoModel) currentRoomModel.getWXWorkContactRoomInfoModelListMap().get(key);
                        String str = WXWorkContactRoomInfoModel.getId().toString();
                        sb.append(str).append(",");
                    }
                    deleteContactRoomIdArr = sb.toString().substring(0, sb.toString().length() - 1);
                }
            } else {
                WorkRoomEntity workRoomCreateEntity = new WorkRoomEntity();
                workRoomCreateEntity.setCorpId(corpEntity.getCorpId());
                workRoomCreateEntity.setWxChatId(WXWorkRoomModel.getChatId());
                workRoomCreateEntity.setName(WXWorkRoomModel.getName().isEmpty() ? "群聊" : WXWorkRoomModel.getName());
                Integer owner = (Integer) employeeMap.get(WXWorkRoomModel.getOwner());
                workRoomCreateEntity.setOwnerId(owner != null ? owner : 0);
                workRoomCreateEntity.setNotice(WXWorkRoomModel.getNotice().isEmpty() ? "" : WXWorkRoomModel.getNotice());
                workRoomCreateEntity.setStatus(WXWorkRoomModel.getStatus());
                workRoomCreateEntity.setRoomMax(200);
                workRoomEntityCreateList.add(workRoomCreateEntity);
                List<WXWorkRoomInfoModel> WXWorkRoomInfoModelList = WXWorkRoomModel.getWXWorkRoomInfoModel();
                for (WXWorkRoomInfoModel workRoomInfoModel :
                        WXWorkRoomInfoModelList) {
                    Integer contactId = 0;
                    Integer employeeId = 0;
                    if (workRoomInfoModel.getType().equals(1)) {
                        employeeId = employeeMap.get(workRoomInfoModel.getUserid()) != null ? (Integer) employeeMap.get(workRoomInfoModel.getUserid()) : 0;
                    } else {
                        contactId = contactMap.get(workRoomInfoModel.getUserid()) != null ? (Integer) contactMap.get(workRoomInfoModel.getUserid()) : 0;
                    }
                    WorkContactRoomEntity workContactRoomEntity = new WorkContactRoomEntity();
                    workContactRoomEntity.setWxUserId(workRoomInfoModel.getUserid());
                    workContactRoomEntity.setContactId(contactId);
                    workContactRoomEntity.setEmployeeId(employeeId);
                    workContactRoomEntity.setUnionid(workRoomInfoModel.getUnionid() == null ? "" : workRoomInfoModel.getUnionid());

                    // TODO 此处的 roomid 为 work_room.id, 需要先插入 work_room
                    workContactRoomEntity.setRoomCase(WXWorkRoomModel.getChatId());
                    workContactRoomEntity.setJoinScene(workRoomInfoModel.getJoin_scene());
                    workContactRoomEntity.setType(workRoomInfoModel.getType());
                    workContactRoomEntity.setStatus(1);
                    workContactRoomEntity.setJoinTime(new Date(workRoomInfoModel.getJoin_time().getTime() * 1000));
                    workContactRoomEntity.setOutTime("");
                    workContactRoomEntityCreateList.add(workContactRoomEntity);
                }
            }
        }
        //根据删除的成员取到要删除的群
        if (workContactRoomModelMap.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String key :
                    workContactRoomModelMap.keySet()) {
                WXWorkContactRoomModel WXWorkContactRoomModel = (WXWorkContactRoomModel) workContactRoomModelMap.get(key);
                String str = WXWorkContactRoomModel.getId().toString();
                sb.append(str).append(",");
            }
            deleteRoomIdArr = sb.toString().substring(0, sb.toString().length() - 1);
        }
        //数据入库
        return dataIntoDb(workRoomEntityCreateList, workRoomEntityUpdateList, deleteRoomIdArr, workContactRoomEntityCreateList, workContactRoomEntityUpdateList, deleteContactRoomIdArr);
    }

    @Override
    public List<WorkRoomEntity> getWorkRoomsByCorpId(Integer corpId, String clStr) {
        QueryWrapper<WorkRoomEntity> workContactRoomEntity = new QueryWrapper<WorkRoomEntity>();
        workContactRoomEntity.select(clStr);
        workContactRoomEntity.eq("corp_id", corpId);
        return this.baseMapper.selectList(workContactRoomEntity);
    }

    @Override
    public List<WorkRoomEntity> getWorkRoomsByWxChatId(List<WXWorkRoomModel> wxWorkRoomModelList, String clStr) {
        QueryWrapper<WorkRoomEntity> workContactRoomEntity = new QueryWrapper<WorkRoomEntity>();
        StringBuilder sb = new StringBuilder();
        for (WXWorkRoomModel WXWorkRoomModel :
                wxWorkRoomModelList) {
            String ids = WXWorkRoomModel.getChatId();
            sb.append(ids).append(",");
        }
        String idAttr = sb.toString().substring(0, sb.toString().length() - 1);
        workContactRoomEntity.select(clStr);
        workContactRoomEntity.in("wx_chat_id", idAttr);
        return this.baseMapper.selectList(workContactRoomEntity);
    }


    /**
     * @description:数据入库
     * @return:
     * @author: Huayu
     * @time: 2020/12/20 16:30
     */
    private boolean dataIntoDb(List<WorkRoomEntity> workRoomEntityCreateList, List<WorkRoomEntity> workRoomEntityUpdateList, String deleteRoomIdArr, List<WorkContactRoomEntity> workContactRoomEntityCreateList, List<WorkContactRoomEntity> workContactRoomEntityUpdateList, String deleteContactRoomIdArr) {
        try {
            //客户群新增数据
            boolean flag = true;
            if (workRoomEntityCreateList.size() > 0) {
                flag = this.saveBatch(workRoomEntityCreateList);
            }
            if (workRoomEntityUpdateList.size() > 0) {
                //客户群更新数据
                for (WorkRoomEntity workRoomEntity :
                        workRoomEntityUpdateList) {
                    this.baseMapper.updateById(workRoomEntity);
                }

            }
            if (deleteRoomIdArr != null && deleteRoomIdArr.length() > 0) {
                //客户群删除数据
                this.baseMapper.deleteBatchIds(Collections.singleton(deleteRoomIdArr));
                String[] strings = deleteRoomIdArr.split(",");
                for (String string :
                        strings) {
                    this.baseMapper.deleteById(string);
                }
            }
            //客户成员新增数据
            if (workContactRoomEntityCreateList.size() > 0) {
                if (flag) {
                    if (workRoomEntityCreateList.size() > 0) {
                        Map<String, Integer> map = new HashMap<>();
                        for (WorkRoomEntity workRoomEntity :
                                workRoomEntityCreateList) {
                            map.put(workRoomEntity.getWxChatId(), workRoomEntity.getId());
                        }
                        for (WorkContactRoomEntity workContactRoomEntity :
                                workContactRoomEntityCreateList) {
                            if (map.get(workContactRoomEntity.getRoomCase()) != null) {
                                workContactRoomEntity.setRoomId(map.get(workContactRoomEntity.getRoomCase()));
                                workContactRoomServiceImpl.createWorkContactRoom(workContactRoomEntity);
                            }
                        }
                    } else {
                        workContactRoomServiceImpl.createWorkContactRooms(workContactRoomEntityCreateList);
                    }
                }
            }
            //客户成员更新数据
            if (workContactRoomEntityUpdateList.size() > 0) {
                workContactRoomServiceImpl.batchUpdateByIds(workContactRoomEntityUpdateList);
            }
            //客户成员删除数据
            if (deleteContactRoomIdArr != null && deleteContactRoomIdArr.length() > 0) {
                Integer status = 2;
                Date date = new Date();
                workContactRoomServiceImpl.updateWorkContactRoomByIds(deleteContactRoomIdArr, date.getTime(), status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            return true;
        }
    }

    /**
     * @description:微信客户群聊列表信息
     * @return:
     * @author: Huayu
     * @time: 2020/12/18 16:48
     */
    @Override
    public Map<String, Object> handelWXWorkRoomModelData(List<WXWorkRoomModel> WXWorkRoomModelList, Integer corpId, int isSingle) {
        //客户群聊列表
        List<WorkRoomEntity> workRoomEntityList = null;
        if (isSingle == 0) {
            workRoomEntityList = getWorkRoomsByCorpId(corpId, "id,wx_chat_id,owner_id");
        } else {
            workRoomEntityList = getWorkRoomsByWxChatId(WXWorkRoomModelList, "id,wx_chat_id,owner_id");
        }
        //客户成员群聊列表
        Map<String, Object> workContactRoomMap = new HashMap<String, Object>();
        for (WorkRoomEntity workRoomEntity :
                workRoomEntityList) {
            List<WorkContactRoomEntity> workContactRoomEntityList = workContactRoomServiceImpl.getWorkContactRoomsInfoByRoomId(workRoomEntity.getId());
            if (workContactRoomEntityList.size() > 0) {
                Map<String, Object> workContactRoomInfoMap = new HashMap<String, Object>();
                WXWorkContactRoomModel WXWorkContactRoomModel = new WXWorkContactRoomModel();
                for (WorkContactRoomEntity workContactRoomEntity:
                        workContactRoomEntityList) {
                    if (workContactRoomEntity.getStatus().equals(2)) {
                        continue;
                    }
                    WXWorkContactRoomModel.setChatId(workRoomEntity.getWxChatId());
                    WXWorkContactRoomModel.setId(workRoomEntity.getId());
                    WXWorkContactRoomModel.setOwnerId(workRoomEntity.getOwnerId());
                    WXWorkContactRoomInfoModel WXWorkContactRoomInfoModel = new WXWorkContactRoomInfoModel();
                    WXWorkContactRoomInfoModel.setId(workContactRoomEntity.getId());
                    WXWorkContactRoomInfoModel.setWxUserId(workContactRoomEntity.getWxUserId());
                    WXWorkContactRoomInfoModel.setRoomId(workContactRoomEntity.getRoomId().toString());
                    WXWorkContactRoomInfoModel.setStatus(workContactRoomEntity.getStatus());
                    workContactRoomInfoMap.put(workContactRoomEntity.getWxUserId(), WXWorkContactRoomInfoModel);
                    WXWorkContactRoomModel.setWXWorkContactRoomInfoModelListMap(workContactRoomInfoMap);
                    workContactRoomMap.put(workRoomEntity.getWxChatId(), WXWorkContactRoomModel);
                }
            }
        }
        //企业通讯录列表
        List<WorkEmployeeEntity> workEmployeeEntityList = workEmployeeServiceImpl.getWorkEmployeesByCorpId(corpId, "id,wx_user_id");
        //企业客户列表
        List<WorkContactEntity> workContactList = workContactServiceImpl.getWorkContactsByCorpId(corpId, "id,wx_external_userid");
        Map<String, Object> workEmployeeMap = new HashMap<String, Object>();
        for (WorkEmployeeEntity workEmployeeEntity :
                workEmployeeEntityList) {
            workEmployeeMap.put(workEmployeeEntity.getWxUserId(), workEmployeeEntity.getId());
        }
        Map<String, Object> workContactMap = new HashMap<String, Object>();
        for (WorkContactEntity workContactEntity :
                workContactList) {
            workContactMap.put(workContactEntity.getWxExternalUserid(), workContactEntity.getId());
        }
        Map<String, Object> compactMap = new HashMap<String, Object>();
        compactMap.put("roomList", workContactRoomMap);
        compactMap.put("employeeList", workEmployeeMap);
        compactMap.put("contactList", workContactMap);
        return compactMap;
    }


    /**
     * @description:组装成客户群需要的数据
     * @return:
     * @author: Huayu
     * @time: 2020/12/17 15:53
     */
    public WorkContactRoomIndexResp getContactRooms(WorkRoomEntity workRoomEntity, WorkContactRoomIndexReq workContactRoomIndexReq, String workEmployeeIds, String workContactIds, String hadName, List<WorkEmployeeEntity> workEmployeeEntityList, List<WorkContactEntity> workContactEntityList) {
        Integer memberNum = 0;
        Integer outRoomNum = 0;
        WorkContactRoomIndexResp workContactRoomIndexResp = new WorkContactRoomIndexResp();
        SimpleDateFormat sdf = new SimpleDateFormat();
        //群成员数量统计
        List<WorkContactRoomEntity> workContactRoomEntityList = workContactRoomServiceImpl.getWorkContactRoomsByRoomId(workContactRoomIndexReq.getWorkRoomId());
        if (workContactRoomEntityList.size() != 0) {
            for (WorkContactRoomEntity workContactRoom :
                    workContactRoomEntityList) {
                if (workContactRoom.getStatus().equals(Integer.valueOf(Status.NORMAL.getCode()))) {
                    memberNum++;
                }
                if (workContactRoom.getStatus().equals(Integer.valueOf(Status.QUIT.getCode()))) {
                    outRoomNum++;
                }
            }

        }
        workContactRoomIndexResp.setMemberNum(memberNum);
        workContactRoomIndexResp.setOutRoomNum(outRoomNum);
        //分页查询数据表
        List<WorkContactRoomEntity> workContactRoomEntityList1 = workContactRoomServiceImpl.getWorkContactRoomIndex(workContactRoomIndexReq, workEmployeeIds, workContactIds);
        List<WorkContactRoomIndex> workContactRoomIndexList = new ArrayList<WorkContactRoomIndex>();
        if (workContactRoomEntityList1 != null && workContactRoomEntityList1.size() > 0) {
            //获取客户群成员的基本信息
            if (hadName.equals("0")) {
                workEmployeeEntityList = new ArrayList<WorkEmployeeEntity>();
                workContactEntityList = new ArrayList<WorkContactEntity>();
                String clStr = "id,name,avatar";
                for (WorkContactRoomEntity workContactRoom :
                        workContactRoomEntityList1) {
                    if (workContactRoom.getEmployeeId() != 0) {
                        //企业通讯录成员信息
                        WorkEmployeeEntity workEmployee = workEmployeeServiceImpl.getWorkEmployeeInfo(workContactRoom.getEmployeeId());
                        workEmployeeEntityList.add(workEmployee);
                    }
                    if (workContactRoom.getContactId() != 0) {
                        // 外部联系人
                        WorkContactEntity workContact = workContactServiceImpl.getWorkContactsById(workContactRoom.getContactId(), clStr);
                        workContactEntityList.add(workContact);
                    }

                }
            }
            //处理列表数据
            for (WorkContactRoomEntity workContactRoom :
                    workContactRoomEntityList1) {
                StringBuilder sb = new StringBuilder();
                StringBuilder sb1 = new StringBuilder();
                WorkContactRoomIndex workContactRoomIndex = new WorkContactRoomIndex();
                workContactRoomIndex.setWorkContactRoomId(String.valueOf(workContactRoom.getId()));
                //该成员在当前公司加入其它群聊信息
                if (workContactRoom.getType().equals(1)) {
                    for (WorkEmployeeEntity workEmployee :
                            workEmployeeEntityList) {
                        workContactRoomIndex.setName(workEmployee.getName());
                        workContactRoomIndex.setAvatar(workEmployee.getAvatar());
                    }
                } else if (workContactRoom.getType().equals(2)) {
                    for (WorkContactEntity workContact :
                            workContactEntityList) {
                        workContactRoomIndex.setName(workContact.getName());
                        workContactRoomIndex.setAvatar(workContact.getAvatar());
                    }
                }
                Integer isOwner = workContactRoom.getEmployeeId().equals(workRoomEntity.getOwnerId()) ? 1 : 0;
                workContactRoomIndex.setIsOwner(isOwner);
                workContactRoomIndex.setJoinTime(sdf.format(workContactRoom.getJoinTime()));
                workContactRoomIndex.setOutRoomTime(workContactRoom.getOutTime());
                //otherRoom
                List<WorkContactRoomEntity> workContactRoomIdsList = workContactRoomServiceImpl.getWorkContactRoomsByWxUserId(workContactRoom.getWxUserId(), "room_id");
                String workContactRoomIds = null;
                List<WorkRoomEntity> workRoomNamesList = null;
                if (workContactRoomIdsList.size() > 0) {
                    for (int i = 0; i < workContactRoomIdsList.size(); i++) {
                        if (workContactRoomIdsList.get(i).getRoomId().equals(workContactRoom.getRoomId())) {
                            continue;
                        } else {
                            sb.append(workContactRoomIdsList.get(i).getRoomId()).append(",");
                        }
                    }
                    if (!sb.toString().equals("")) {
                        workContactRoomIds = workEmployeeEntityList.size() == 0 ? "" : sb.toString().substring(0, sb.toString().length() - 1);
                        workRoomNamesList = getWorkRoomsByIds(workContactRoomIds, "name");
                    }
                }
                if (workRoomNamesList != null && workRoomNamesList.size() > 0) {
                    for (int i = 0; i < workRoomNamesList.size(); i++) {
                        sb1.append(workRoomNamesList.get(i).getName()).append(",");
                    }
                    String workRoomNames = workRoomNamesList.size() == 0 ? "" : sb1.toString().substring(0, sb1.toString().length() - 1);
                    workContactRoomIndex.setOtherRooms(workRoomNames.split(","));
                    workContactRoomIndex.setJoinScene(workContactRoom.getJoinScene());
                    if (workContactRoom.getJoinScene().equals(Integer.valueOf(JoinSceneEnum.DIRECT_INVITE.getCode()))) {
                        workContactRoomIndex.setJoinSceneText(JoinSceneEnum.DIRECT_INVITE.getMsg());
                    } else if (workContactRoom.getJoinScene().equals(Integer.valueOf(JoinSceneEnum.LINK_INVITE.getCode()))) {
                        workContactRoomIndex.setJoinSceneText(JoinSceneEnum.LINK_INVITE.getMsg());
                    }
                    workContactRoomIndexList.add(workContactRoomIndex);
                }
            }
        }
        //组装分页数据
        Page<WorkContactRoomIndex> page = new Page<WorkContactRoomIndex>();
        Integer pageNum = workContactRoomIndexReq.getPage();
        pageNum = (pageNum == null || pageNum.equals("")) ? 1 : pageNum;
        Integer perPage = workContactRoomIndexReq.getPerPage();
        perPage = (perPage == null || perPage.equals("")) ? 10 : perPage;
        RequestPage requestPage = new RequestPage(pageNum, perPage);
        ApiRespUtils.initPage(page, requestPage);
        page = page.setRecords(workContactRoomIndexList);
        workContactRoomIndexResp = workContactRoomIndexResp.getInstance(page, workContactRoomIndexResp);
        //workContactRoomIndexResp.setRespPageVO(respPageVO);
        return workContactRoomIndexResp;
    }

    /**
     * @description 获取客户 - 群聊列表下拉框
     * @author zhaojinjian
     * @createTime 2020/12/30 15:34
     */
    @Override
    public JSONObject getWorkRoomSelectData(Integer corpId, String roomName, Integer roomGroupId) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<WorkRoomEntity> workRoomWrapper = new QueryWrapper<>();
        if (roomName != null && !roomName.isEmpty()) {
            workRoomWrapper.like("name", roomName);
        }
        if (roomGroupId != null) {
            workRoomWrapper.eq("room_group_id", roomGroupId);
        }
        workRoomWrapper.eq("corp_id", corpId);
        List<WorkRoomEntity> roomList = this.list(workRoomWrapper);
        if (roomList != null && roomList.size() > 0) {
            List<Integer> roomIds = roomList.stream().map(WorkRoomEntity::getId).collect(Collectors.toList());
            Map<Integer, Long> roomCurrentNumMap = workContactRoomServiceImpl.getContactRoomSum(roomIds);
            JSONArray list = new JSONArray();
            roomList.forEach(item -> {
                JSONObject listItem = new JSONObject();
                listItem.put("roomId", item.getId());
                listItem.put("roomName", item.getName());
                listItem.put("currentNum", roomCurrentNumMap.get(item.getId()));
                listItem.put("roomMax", item.getRoomMax());
                list.add(listItem);
            });
            jsonObject.put("total", roomList.size());
            jsonObject.put("list", list);
        } else {
            jsonObject.put("total", 0);
            jsonObject.put("list", new ArrayList<>());
        }
        return jsonObject;
    }


    @Override
    public List<WorkRoomEntity> getWorkRoomsByChatId(List<String> wxRoomIdArr, String s) {
        StringBuilder sb = new StringBuilder();
        for (String str:
        wxRoomIdArr) {
            sb.append(str).append(",");
        }
        String wxRoomIdStr = sb.substring(0,sb.length()-1);
        QueryWrapper<WorkRoomEntity> workRoomWrapper = new QueryWrapper<>();
        workRoomWrapper.select(s);
        workRoomWrapper.in("wx_room_id",wxRoomIdStr);
        return this.baseMapper.selectList(workRoomWrapper);
    }

    @Override
    public List<WorkRoomEntity> countWorkRoomByCorpIds(Integer corpId) {
        QueryWrapper<WorkRoomEntity> workRoomWrapper = new QueryWrapper<>();
        workRoomWrapper.getSqlSelect();
        workRoomWrapper.eq("corp_id",corpId);
        return this.baseMapper.selectList(workRoomWrapper);

    }

    @Override
    public List<WorkRoomEntity> countAddWorkRoomsByCorpIdTime(Integer corpId, Date startTime, Date endTime) {
        QueryWrapper<WorkRoomEntity> workRoomWrapper = new QueryWrapper<>();
        workRoomWrapper.getSqlSelect();
        workRoomWrapper.eq("corp_id",corpId);
        workRoomWrapper.ge("create_time",startTime);
        workRoomWrapper.lt("create_time",endTime);
        return this.baseMapper.selectList(workRoomWrapper);
    }

}


