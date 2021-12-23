package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.em.workcontact.AddWayEnum;
import com.mochat.mochat.common.em.workcontact.EventEnum;
import com.mochat.mochat.common.em.workupdatetime.TypeEnum;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.FileUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.entity.channel.ChannelCodeEntity;
import com.mochat.mochat.dao.entity.medium.MediumEntity;
import com.mochat.mochat.dao.mapper.ContactEmployeeTrackMapper;
import com.mochat.mochat.dao.mapper.ContactMapper;
import com.mochat.mochat.model.contact.ContactDetailVO;
import com.mochat.mochat.model.contact.ContactTrackVO;
import com.mochat.mochat.model.transfer.GetContactRoom;
import com.mochat.mochat.model.workcontact.*;
import com.mochat.mochat.model.workcontacttag.GetContactTapModel;
import com.mochat.mochat.model.workcontacttag.GetEmployeeTagModel;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.IWorkUpdateTimeService;
import com.mochat.mochat.service.channel.IChannelCodeService;
import com.mochat.mochat.service.contact.IExternalContactService;
import com.mochat.mochat.service.contact.ISendWelcomeMsgService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.medium.IMediumService;
import com.mochat.mochat.service.workroom.IWorkRoomAutoPullService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 客户实现类
 * @author: zhaojinjian
 * @create: 2020-11-26 15:07
 **/
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, WorkContactEntity> implements IContactService {

    /**
     * 同步更新时间
     */
    @Autowired
    private IWorkUpdateTimeService updateTimeService;

    /**
     * 用户画像
     */
    @Autowired
    private IWorkContactFieldPivotService contactFieldPivotService;

    /**
     * 媒体库
     */
    @Autowired
    private IMediumService mediumService;

    @Autowired
    private IWorkContactService workContactServiceImpl;

    @Autowired
    private IWorkContactEmployeeService workContactEmployeeServiceImpl;

    @Autowired
    private IWorkContactService workContactService;

    /**
     * 客户标签
     */
    @Autowired
    @Lazy
    private IWorkContactTagService contactTagService;
    @Autowired
    @Lazy
    private IWorkContactTagPivotService contactTagPivotService;
    @Autowired
    @Lazy
    private IWorkContactEmployeeService workContactEmployeeService;

    /**
     * 客户群
     */
    @Autowired
    private IWorkContactRoomService contactRoomService;

    /**
     * 通讯录
     */
    @Autowired
    private IWorkEmployeeService workEmployeeService;
    /**
     * 企业微信外部联系人
     */
    @Autowired
    @Lazy
    private IExternalContactService externalContactService;

    @Autowired
    private ISendWelcomeMsgService sendWelcomeMsgService;

    @Autowired
    private IWorkRoomAutoPullService workRoomAutoPullService;

    /**
     * 企业
     */
    @Autowired
    private ICorpService corpService;

    @Autowired
    private IWorkRoomService roomService;

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/19 5:33 下午
     * @description 员工与客户的互动轨迹
     */
    @Resource
    private ContactEmployeeTrackMapper contactEmployeeTrackMapper;

    @Autowired
    private IChannelCodeService channelCodeService;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    @Autowired
    private IWorkEmployeeService employeeService;

    /**
     * @Description: 获取客户列表分页
     * @Param:
     * @return:
     * @Author: zhaojinjian
     * @Date: 2020/11/26
     */
    @Override
    public GetContactPageResponse getContactPage(GetContactRequest param, int empId, int corpId, ReqPerEnum perEnum) {
        GetContactPageResponse resp = new GetContactPageResponse();

        //region 如果客户持群数等于 0 而群聊数大于 0 返回空数据
        if (StringUtils.hasLength(param.getGroupNum()) && StringUtils.hasLength(param.getRoomId())) {
            String[] roomIds = param.getRoomId().split(",");
            if ("0".equals(param.getGroupNum()) && roomIds.length > 0) {
                return resp;
            }
        }
        //endregion

        // 权限管理过滤
        List<WorkContactEmployeeEntity> contactEmployeeEntityList = getContactEmployeeListByEmpIdList(param, perEnum);
        if (contactEmployeeEntityList.isEmpty()) {
            return resp;
        }

        List<Integer> contactEmployeeIdListResult = contactEmployeeEntityList.stream()
                .map(WorkContactEmployeeEntity::getId)
                .collect(Collectors.toList());

        List<Integer> contactIdListResult = contactEmployeeEntityList.stream()
                .map(WorkContactEmployeeEntity::getContactId)
                .collect(Collectors.toList());

        if (StringUtils.hasLength(param.getGroupNum()) || StringUtils.hasLength(param.getRoomId())) {
            contactIdListResult = getContactIdListByGroupNumAndRoomIds(contactIdListResult, param.getGroupNum(), param.getRoomId());
            if (contactIdListResult.isEmpty()) {
                return resp;
            }
        }

        if (Objects.nonNull(param.getFieldId()) && param.getFieldId() > 0) {
            contactIdListResult = getContactIdListByField(contactIdListResult, param.getFieldId(), param.getFieldValue());
            if (contactIdListResult.isEmpty()) {
                return resp;
            }
        }

        List<WorkContactEntity> contactEntityList = getContactEntityListByParam(param, corpId, contactIdListResult);

        if (contactEntityList.isEmpty()) {
            return resp;
        }

        List<Integer> contactIdList = contactEntityList.stream()
                .map(WorkContactEntity::getId)
                .collect(Collectors.toList());

        Map<Integer, WorkContactEntity> contactEntityMap = new HashMap<>(contactEntityList.size());
        for (WorkContactEntity entity : contactEntityList) {
            contactEntityMap.put(entity.getId(), entity);
        }

        Page<WorkContactEmployeeEntity> entityPage = workContactEmployeeService.lambdaQuery()
                .eq(WorkContactEmployeeEntity::getCorpId, corpId)
                .in(WorkContactEmployeeEntity::getContactId, contactIdList)
                .in(WorkContactEmployeeEntity::getId, contactEmployeeIdListResult)
                .page(ApiRespUtils.initPage(param));

        List<WorkContactEmployeeEntity> entityList = entityPage.getRecords();

        List<WorkContactRoomEntity> contactRoomEntityList = contactRoomService.lambdaQuery()
                .in(WorkContactRoomEntity::getContactId, contactIdList)
                .list();
        Map<Integer, List<Integer>> contactIdRoomIdListMap = new HashMap<>(contactIdList.size());
        List<Integer> roomIdList = new ArrayList<>();
        for (WorkContactRoomEntity entity : contactRoomEntityList) {
            roomIdList.add(entity.getRoomId());

            List<Integer> roomIds = contactIdRoomIdListMap.get(entity.getContactId());
            if (Objects.isNull(roomIds)) {
                roomIds = new ArrayList<>();
                contactIdRoomIdListMap.put(entity.getContactId(), roomIds);
            } else {
                roomIds.add(entity.getRoomId());
            }
        }

        Map<Integer, String> roomIdNameMap = new HashMap<>(roomIdList.size());
        if (!roomIdList.isEmpty()) {
            List<WorkRoomEntity> roomEntityList = roomService.lambdaQuery()
                    .select(WorkRoomEntity::getId, WorkRoomEntity::getName)
                    .in(WorkRoomEntity::getId, roomIdList)
                    .list();
            for (WorkRoomEntity entity : roomEntityList) {
                roomIdNameMap.put(entity.getId(), entity.getName());
            }
        }

        List<WorkContactTagPivotEntity> contactTagPivotEntityList = contactTagPivotService.lambdaQuery()
                .in(WorkContactTagPivotEntity::getContactId, contactIdList)
                .list();
        Map<String, List<Integer>> contactEmpAndTagIdListMap = new HashMap<>();
        List<Integer> tagIdListAll = new ArrayList<>();
        for (WorkContactTagPivotEntity entity : contactTagPivotEntityList) {
            tagIdListAll.add(entity.getContactTagId());
            String key = entity.getContactId() + "-" + entity.getEmployeeId();

            List<Integer> tagIdList = null;
            if (contactEmpAndTagIdListMap.containsKey(key)) {
                tagIdList = contactEmpAndTagIdListMap.get(key);
            } else {
                tagIdList = new ArrayList<>();
                contactEmpAndTagIdListMap.put(key, tagIdList);
            }
            tagIdList.add(entity.getContactTagId());
        }

        Map<Integer, String> tagIdNameMap = new HashMap<>(tagIdListAll.size());
        if (!tagIdListAll.isEmpty()) {
            List<WorkContactTagEntity> tagEntityList = contactTagService.lambdaQuery()
                    .select(WorkContactTagEntity::getId, WorkContactTagEntity::getName)
                    .in(WorkContactTagEntity::getId, tagIdListAll)
                    .list();
            for (WorkContactTagEntity entity : tagEntityList) {
                tagIdNameMap.put(entity.getId(), entity.getName());
            }
        }

        Map<Integer, Integer> contactEmpIdEmpIdMap = new HashMap<>(entityList.size());
        List<Integer> empIdList = new ArrayList<>();
        for (WorkContactEmployeeEntity entity : entityList) {
            empIdList.add(entity.getEmployeeId());
            contactEmpIdEmpIdMap.put(entity.getId(), entity.getEmployeeId());
        }
        List<WorkEmployeeEntity> employeeEntityList = employeeService.listByIds(empIdList);
        Map<Integer, String> empIdNameMap = new HashMap<>();
        for (WorkEmployeeEntity entity : employeeEntityList) {
            empIdNameMap.put(entity.getId(), entity.getName());
        }

        List<ContactData> voList = new ArrayList<>();
        for (WorkContactEmployeeEntity entity : entityList) {
            int cId = entity.getContactId();

            ContactData vo = new ContactData();
            vo.setId(entity.getId());

            WorkContactEntity contactEntity = contactEntityMap.get(cId);
            vo.setContactId(cId);
            vo.setAvatar(AliyunOssUtils.getUrl(contactEntity.getAvatar()));
            vo.setGender(contactEntity.getGender());
            if (contactEntity.getGender() == 0) {
                vo.setGenderText("未知");
            } else {
                vo.setGenderText(contactEntity.getGender() == 1 ? "男" : "女");
            }
            vo.setName(contactEntity.getName());
            vo.setBusinessNo(contactEntity.getBusinessNo());

            vo.setRemark(entity.getRemark());
            vo.setAddWay(entity.getAddWay());
            vo.setAddWayText(AddWayEnum.getByCode(entity.getAddWay()));
            vo.setCreateTime(DateUtils.formatS1(entity.getCreateTime().getTime() * 1000));

            List<String> roomNameList = new ArrayList<>();
            List<Integer> roomIds = contactIdRoomIdListMap.get(cId);
            if (Objects.nonNull(roomIds)) {
                for (Integer roomId : roomIds) {
                    roomNameList.add(roomIdNameMap.get(roomId));
                }
            }
            vo.setRoomName(roomNameList);

            List<String> tagNameList = new ArrayList<>();
            String key = entity.getContactId() + "-" + entity.getEmployeeId();
            if (contactEmpAndTagIdListMap.containsKey(key)) {
                List<Integer> tagIds = contactEmpAndTagIdListMap.get(key);
                for (Integer tagId : tagIds) {
                    tagNameList.add(tagIdNameMap.get(tagId));
                }
            }
            vo.setTag(tagNameList);

            int eId = contactEmpIdEmpIdMap.get(entity.getId());
            vo.setEmployeeId(eId);
            vo.setEmployeeName(empIdNameMap.get(eId));
            vo.setIsContact(eId == empId ? 1 : 2);

            voList.add(vo);
        }

        resp.setList(voList);

        GetContactPageResponse.ContactPage contactPage = new GetContactPageResponse.ContactPage();
        contactPage.setPerPage(entityPage.getSize());
        contactPage.setTotal(entityPage.getTotal());
        contactPage.setTotalPage(entityPage.getPages());
        resp.setPage(contactPage);

        resp.setSyncContactTime(updateTimeService.getLastUpdateTime(TypeEnum.CONTACT));

        return resp;
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private List<Integer> getPermissionEmpIdList(ReqPerEnum permission) {
        if (permission == ReqPerEnum.ALL) {
            return employeeService.lambdaQuery()
                    .select(WorkEmployeeEntity::getId)
                    .eq(WorkEmployeeEntity::getCorpId, AccountService.getCorpId())
                    .list()
                    .stream()
                    .map(WorkEmployeeEntity::getId)
                    .collect(Collectors.toList());
        }

        if (permission == ReqPerEnum.DEPARTMENT) {
            // 查询员工所属的部门 id 列表
            return employeeDepartmentService.getDeptAndChildDeptEmpIdList();
        }

        if (permission == ReqPerEnum.EMPLOYEE) {
            return Arrays.asList(AccountService.getEmpId());
        }

        return Collections.emptyList();
    }

    private List<Integer> getContactIdListByGroupNumAndRoomIds(List<Integer> contactIdList, String groupNum, String roomIds) {
        if (!StringUtils.hasLength(groupNum)) {
            groupNum = "3";
        }

        if (StringUtils.hasLength(roomIds)) {
            // 根据 roomIds 过滤客户
            List<String> roomIdList = Arrays.asList(roomIds.split(","));
            // 在群里的客户
            List<Integer> contactIdListHas = contactRoomService.lambdaQuery()
                    .select(WorkContactRoomEntity::getContactId)
                    .groupBy(WorkContactRoomEntity::getContactId)
                    .in(WorkContactRoomEntity::getRoomId, roomIdList)
                    .in(WorkContactRoomEntity::getContactId, contactIdList)
                    .list()
                    .stream()
                    .map(WorkContactRoomEntity::getContactId)
                    .collect(Collectors.toList());

            if ("1".equals(groupNum)) {
                // 只有一个群的客户
                List<Integer> contactIdListResult = contactRoomService.lambdaQuery()
                        .select(WorkContactRoomEntity::getContactId)
                        .groupBy(WorkContactRoomEntity::getContactId)
                        .in(WorkContactRoomEntity::getContactId, contactIdList)
                        .having("count(contact_id) = 1")
                        .list()
                        .stream()
                        .map(WorkContactRoomEntity::getContactId)
                        .collect(Collectors.toList());
                // 交集
                contactIdListResult.retainAll(contactIdListHas);
                return contactIdListResult;
            }

            if ("2".equals(groupNum)) {
                // 只有一个群的客户
                List<Integer> contactIdListResult = contactRoomService.lambdaQuery()
                        .select(WorkContactRoomEntity::getContactId)
                        .groupBy(WorkContactRoomEntity::getContactId)
                        .in(WorkContactRoomEntity::getContactId, contactIdList)
                        .having("count(contact_id) > 1")
                        .list()
                        .stream()
                        .map(WorkContactRoomEntity::getContactId)
                        .collect(Collectors.toList());
                // 交集
                contactIdListResult.retainAll(contactIdListHas);
                return contactIdListResult;
            }

            // 在群内的客户
            return contactIdListHas;
        } else {
            // 获取所有没有群的客户
            if ("0".equals(groupNum)) {
                // 查出所有群
                List<Integer> roomIdList = roomService.lambdaQuery()
                        .select(WorkRoomEntity::getId)
                        .eq(WorkRoomEntity::getCorpId, AccountService.getCorpId())
                        .list()
                        .stream()
                        .map(WorkRoomEntity::getId)
                        .collect(Collectors.toList());

                // 查出所有客户
                List<Integer> contactIdListResult = lambdaQuery()
                        .select(WorkContactEntity::getId)
                        .eq(WorkContactEntity::getCorpId, AccountService.getCorpId())
                        .in(WorkContactEntity::getId, contactIdList)
                        .list()
                        .stream()
                        .map(WorkContactEntity::getId)
                        .collect(Collectors.toList());

                // 没有客户群直接返回所有客户
                if (roomIdList.isEmpty()) {
                    return contactIdListResult;
                }

                // 查出所有有群的客户
                List<Integer> contactIdListHasRoom = contactRoomService.lambdaQuery()
                        .select(WorkContactRoomEntity::getContactId)
                        .in(WorkContactRoomEntity::getRoomId, roomIdList)
                        .in(WorkContactRoomEntity::getContactId, contactIdList)
                        .groupBy(WorkContactRoomEntity::getContactId)
                        .list()
                        .stream()
                        .map(WorkContactRoomEntity::getContactId)
                        .collect(Collectors.toList());

                contactIdListResult.removeAll(contactIdListHasRoom);

                return contactIdListResult;
            }

            // 获取只有 1 个群的客户
            if ("1".equals(groupNum)) {
                return contactRoomService.lambdaQuery()
                        .select(WorkContactRoomEntity::getContactId)
                        .groupBy(WorkContactRoomEntity::getContactId)
                        .in(WorkContactRoomEntity::getContactId, contactIdList)
                        .having("count(contact_id) = 1")
                        .list()
                        .stream()
                        .map(WorkContactRoomEntity::getContactId)
                        .collect(Collectors.toList());
            }

            // 获取有多个群的客户
            if ("2".equals(groupNum)) {
                // 获取只有 1 个群的客户
                return contactRoomService.lambdaQuery()
                        .select(WorkContactRoomEntity::getContactId)
                        .groupBy(WorkContactRoomEntity::getContactId)
                        .in(WorkContactRoomEntity::getContactId, contactIdList)
                        .having("count(contact_id) > 1")
                        .list()
                        .stream()
                        .map(WorkContactRoomEntity::getContactId)
                        .collect(Collectors.toList());
            }

            // 查出所有客户
            return lambdaQuery()
                    .select(WorkContactEntity::getId)
                    .eq(WorkContactEntity::getCorpId, AccountService.getCorpId())
                    .in(WorkContactEntity::getId, contactIdList)
                    .list()
                    .stream().map(WorkContactEntity::getId)
                    .collect(Collectors.toList());
        }
    }

    private List<Integer> getContactIdListByField(List<Integer> contactIdList, int fieldId, String fieldValue) {
        if (fieldId == 0) {
            return contactFieldPivotService.lambdaQuery()
                    .select(ContactFieldPivotEntity::getContactId)
                    .in(ContactFieldPivotEntity::getContactId, contactIdList)
                    .list()
                    .stream()
                    .map(ContactFieldPivotEntity::getContactId)
                    .collect(Collectors.toList());
        }

        if (!StringUtils.hasLength(fieldValue)) {
            return Collections.emptyList();
        }

        return contactFieldPivotService.lambdaQuery()
                .select(ContactFieldPivotEntity::getContactId)
                .eq(ContactFieldPivotEntity::getContactFieldId, fieldId)
                .in(ContactFieldPivotEntity::getContactId, contactIdList)
                .like(ContactFieldPivotEntity::getValue, fieldValue)
                .list()
                .stream()
                .map(ContactFieldPivotEntity::getContactId)
                .collect(Collectors.toList());
    }

    private List<WorkContactEmployeeEntity> getContactEmployeeListByEmpIdList(GetContactRequest param, ReqPerEnum perEnum) {
        List<Integer> empIdList = getPermissionEmpIdList(perEnum);

        String empIds = param.getEmployeeId();
        if (StringUtils.hasLength(empIds)) {
            empIdList.retainAll(Arrays.asList(empIds.split(",")));
        }

        if (empIdList.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryChainWrapper<WorkContactEmployeeEntity> wrapper = workContactEmployeeService.lambdaQuery()
                .select(WorkContactEmployeeEntity::getId, WorkContactEmployeeEntity::getContactId)
                .in(WorkContactEmployeeEntity::getEmployeeId, empIdList);

        String remark = param.getRemark();
        if (StringUtils.hasLength(remark)) {
            wrapper.like(WorkContactEmployeeEntity::getRemark, remark);
        }

        String addWay = param.getAddWay();
        if (StringUtils.hasLength(addWay)) {
            wrapper.eq(WorkContactEmployeeEntity::getAddWay, addWay);
        }

        if (StringUtils.hasLength(param.getStartTime())) {
            wrapper.ge(WorkContactEmployeeEntity::getCreateTime, param.getStartTime());
        }
        if (StringUtils.hasLength(param.getBusinessNo())) {
            wrapper.le(WorkContactEmployeeEntity::getCreateTime, param.getEndTime());
        }

        return wrapper.list();
    }

    private List<WorkContactEntity> getContactEntityListByParam(GetContactRequest param, int corpId, List<Integer> contactIdList) {
        if (contactIdList.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryChainWrapper<WorkContactEntity> wrapper = lambdaQuery();
        wrapper.eq(WorkContactEntity::getCorpId, corpId);
        wrapper.in(WorkContactEntity::getId, contactIdList);
        if (StringUtils.hasLength(param.getKeyWords())) {
            wrapper.and(i -> {
                        i.like(WorkContactEntity::getName, param.getKeyWords())
                                .or()
                                .like(WorkContactEntity::getNickName, param.getKeyWords());
                    }
            );
        }
        if (Objects.nonNull(param.getGender()) && param.getGender() < 3) {
            wrapper.eq(WorkContactEntity::getGender, param.getGender());
        }
        if (StringUtils.hasLength(param.getBusinessNo())) {
            wrapper.like(WorkContactEntity::getBusinessNo, param.getBusinessNo());
        }
        return wrapper.list();
    }

    private List<ContactData> pageContactList(List<ContactData> list, Integer pageNum, Integer pageSize) {
        if (list != null && list.size() > 0) {
            Integer count = list.size(); // 记录总数
            Integer pageCount = 0; // 页数
            if (count % pageSize == 0) {
                pageCount = count / pageSize;
            } else {
                pageCount = count / pageSize + 1;
            }
            int fromIndex = 0; // 开始索引
            int toIndex = 0; // 结束索引

            if (pageNum != pageCount) {
                fromIndex = (pageNum - 1) * pageSize;
                toIndex = fromIndex + pageSize;
            } else {
                fromIndex = (pageNum - 1) * pageSize;
                toIndex = count;
            }
            return list.subList(fromIndex, toIndex);
        }
        return null;
    }

    /**
     * @description 获取客户详情
     * @author zhaojinjian
     * @createTime 2020/12/6 11:22
     */
    @Override
    public GetContactInfoResponse getContactInfo(Integer contactId, Integer empId, Integer corpId) {
        GetContactInfoResponse contactInfoResponse = new GetContactInfoResponse();

        //region 客户信息数据

        WorkContactEntity workContact = this.baseMapper.selectById(contactId);
        contactInfoResponse.setBusinessNo(workContact.getBusinessNo());
        contactInfoResponse.setName(workContact.getName());
        contactInfoResponse.setGender(workContact.getGender());
        //endregion

        //region 获取客户的群

        List<Integer> contactIds = new ArrayList<>();
        contactIds.add(contactId);
        List<GetContactRoom> contactRooms = contactRoomService.getBaseMapper().getContactRoomList(contactIds, null);
        //endregion

        //region 获取客户的标签
        List<GetContactTapModel> tags = contactTagService.getContactTapName(empId, contactId);
        contactInfoResponse.setTag(tags);
        //endregion

        //region 客户通讯录中间表数据
        WorkContactEmployeeEntity workContactEmployee = workContactEmployeeService.getWorkContactEmployeeInfo(corpId, empId, contactId, null);
        contactInfoResponse.setRemark(workContactEmployee.getRemark());
        contactInfoResponse.setDescription(workContactEmployee.getDescription());
        List<Integer> empIds = workContactEmployeeService.getBelongToEmployeeId(contactId, corpId);

        //endregion

        //region 通讯录 企业
        if (empIds != null && empIds.size() > 0) {
            String[] empName = workEmployeeService.getEmployeeName(empIds);
            CorpEntity corpEntity = corpService.getById(corpId);
            empName = Arrays.stream(empName).map(value -> corpEntity.getCorpName() + "--" + value).toArray(String[]::new);
            contactInfoResponse.setEmployeeName(empName);
        } else {
            contactInfoResponse.setEmployeeName(new String[0]);
        }
        //endregion

        return contactInfoResponse;
    }

    @Override
    public String getWxExternalUserId(int contactId) {
        WorkContactEntity workContactEntity = this.baseMapper.selectById(contactId);
        if (workContactEntity != null) {
            return workContactEntity.getWxExternalUserid();
        }
        return "";
    }

    /**
     * @description 根据微信外部联系人id获取当前客户的本地id
     * @author zhaojinjian
     * @createTime 2020/12/17 15:35
     */
    @Override
    public Integer getContactId(String wxExternalUserid) {
        QueryWrapper<WorkContactEntity> contactWrapper = new QueryWrapper<>();
        contactWrapper.select("id");
        contactWrapper.eq("wx_external_userid", wxExternalUserid);
        WorkContactEntity contactEntity = this.baseMapper.selectOne(contactWrapper);
        if (contactEntity == null) {
            return null;
        } else {
            return contactEntity.getId();
        }
    }

    /**
     * @description 修改客户编号
     * @author zhaojinjian
     * @createTime 2020/12/6 11:22
     */
    @Override
    public boolean updateBusinessNo(Integer empId, Integer contactId, String businessNo) {
        WorkContactEntity entity = new WorkContactEntity();
        entity.setId(contactId);
        entity.setBusinessNo(businessNo);
        boolean result = this.baseMapper.updateById(entity) == 1;
        if (result) {
            saveTrack(empId, contactId, EventEnum.INFO, EventEnum.INFO.getMsg() + ": 客户编号");
        }
        return result;
    }

    /**
     * @description 客户 - 修改客户详情基本信息
     * @author zhaojinjian
     * @createTime 2020/12/28 10:41
     */
    @Override
    @Transactional
    public boolean updateContact(UpdateContactResponse parem, Integer corpId, Integer empId) {
        if (parem.getContactId() != null) {
            if (StringUtils.hasLength(parem.getBusinessNo())) {
                updateBusinessNo(empId, parem.getContactId(), parem.getBusinessNo());
            }
            if (parem.getTag() != null && !parem.getTag().isEmpty()) {
                contactTagPivotService.updateContactTagPivot(empId, parem.getContactId(), parem.getTag());
            }
            if (StringUtils.hasLength(parem.getRemark()) || StringUtils.hasLength(parem.getDescription())) {
                workContactEmployeeService.updateRemarkOrDescription(corpId, empId, parem.getContactId(), parem.getRemark(), parem.getDescription());
            }
            return true;
        }
        throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
    }

    @Override
    public boolean insertAllContact(List<WorkContactEntity> contacts) {
        return this.saveBatch(contacts);
    }

    /**
     * @description 添加企业的客户（存在则修改）
     * @author zhaojinjian
     * @createTime 2020/12/19 11:04
     */
    @Override
    @Transactional
    public boolean insertAllContact(List<WorkContactEntity> contacts, Integer corpId) {
        boolean result = false;
        QueryWrapper<WorkContactEntity> contactWrapper = new QueryWrapper<>();
        contactWrapper.select("wx_external_userid,id");
        contactWrapper.eq("corp_id", corpId);
        contactWrapper.isNull("deleted_at");
        List<WorkContactEntity> contactList = this.list(contactWrapper);

        List<WorkContactEntity> diffContactList = contacts.stream().filter(item -> !contactList.stream().map(e -> e.getWxExternalUserid()).collect(Collectors.toList()).contains(item.getWxExternalUserid())).collect(Collectors.toList());
        contacts.removeAll(diffContactList);
        contacts.forEach(item -> {
            Optional<WorkContactEntity> contactOp = contactList.stream().filter(c -> c.getWxExternalUserid().equals(item.getWxExternalUserid())).findAny();
            WorkContactEntity model = contactOp.get();
            if (model != null) {
                item.setId(model.getId());
            }
        });
        if (diffContactList != null && diffContactList.size() > 0) {
            //result = this.saveBatch(diffContactList);
            int result1 = 0;
            for (WorkContactEntity workContactEntity :
                    diffContactList) {
                workContactEntity.setName(workContactEntity.getName());
                result1 = this.baseMapper.insert(workContactEntity);
            }
            if (result1 > 0) {
                result = true;
            }
        }
        if (contacts != null && contacts.size() > 0) {
            int result2 = 0;
            for (WorkContactEntity workContactEntity :
                    contacts) {
                workContactEntity.setName(workContactEntity.getName());
                result2 = this.baseMapper.updateById(workContactEntity);
            }
            if (result2 > 0) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean insertContact(WorkContactEntity contact) {
        QueryWrapper<WorkContactEntity> contactWrapper = new QueryWrapper<>();
        contactWrapper.eq("wx_external_userid", contact.getWxExternalUserid());
        contactWrapper.isNull("deleted_at");
        WorkContactEntity resultContact = this.baseMapper.selectOne(contactWrapper);
        if (resultContact != null) {
            resultContact.setPosition(contact.getPosition());
            resultContact.setName(contact.getName());
            resultContact.setGender(contact.getGender());
            resultContact.setCorpName(contact.getCorpName());
            resultContact.setCorpFullName(contact.getCorpFullName());
            resultContact.setExternalProfile(contact.getExternalProfile());
            contact.setId(resultContact.getId());
            return this.updateById(resultContact);
        }
        return this.baseMapper.insert(contact) == 1;
    }

    /**
     * @description 同步客户时，添加客户信息
     * @author zhaojinjian
     * @createTime 2020/12/17 11:07
     */
    @Override
    public Integer insertWXSynContact(JSONObject contactJson, Integer corpId) {
        //region 客户信息赋值

        WorkContactEntity contact = new WorkContactEntity();
        contact.setAvatar(contactJson.getString("avatar"));
        contact.setName(contactJson.getString("name"));
        contact.setUnionid(contactJson.getString("unionid"));
        contact.setPosition(contactJson.getString("position"));
        contact.setWxExternalUserid(contactJson.getString("external_userid"));
        contact.setType(contactJson.getInteger("type"));
        contact.setGender(contactJson.getInteger("gender"));
        contact.setCorpFullName(contactJson.getString("corp_full_name"));
        contact.setCorpName(contactJson.getString("corp_name"));
        contact.setExternalProfile(contactJson.getString("external_profile"));
        contact.setCreatedAt(new Date());
        contact.setCorpId(corpId);
        contact.setBusinessNo("");
        contact.setFollowUpStatus(0);
        contact.setNickName("");
        insertContact(contact);
        return contact.getId();
        //endregion
    }

    /**
     * @description 同步客户
     * @author zhaojinjian
     * @createTime 2020/12/12 11:43
     */
    @Override
    @Transactional
    public boolean synContact(Integer corpId) {
        //获取当前企业下所有成员的微信UserId
        Map<String, Integer> empIdAndUserId = workEmployeeService.getCorpByUserId(corpId);
        List<String> wx_UserIds = empIdAndUserId.keySet().stream().collect(Collectors.toList());

        Map<String, Integer> tagIds = contactTagService.getContactTagId(corpId);
        //根据成员微信id获取当前成员下客户列表
        JSONArray array = externalContactService.getAllExternalUserId(wx_UserIds, corpId);
        List<Object> arrayList = Arrays.stream(array.toArray()).distinct().collect(Collectors.toList());
        //根据成员微信id数组，获取多个微信客户详细信息
        Map<String, JSONObject> list = externalContactService.getExternalContactMap(arrayList, corpId);
        //处理多个微信客户详细信息本地化
        Map<WorkContactEntity, Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>>> modelMap = externalContactToWorkContact(list, corpId, null);

        List<WorkContactEntity> contactList = modelMap.keySet().stream().collect(Collectors.toList());

        boolean result = insertAllContact(contactList, corpId);

        if (result) {
            List<WorkContactEmployeeEntity> contactEmployeeList = new ArrayList<>();
            List<WorkContactTagPivotEntity> contactTagPivotList = new ArrayList<>();
            for (Map.Entry<WorkContactEntity, Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>>> entry : modelMap.entrySet()) {
                for (Map.Entry<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>> aa : entry.getValue().entrySet()) {
                    Integer empId = empIdAndUserId.get(aa.getKey());
                    for (Map.Entry<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>> bb : aa.getValue().entrySet()) {
                        WorkContactEmployeeEntity contactEmployeeEntity = new WorkContactEmployeeEntity();
                        Gson gson = new Gson();
                        contactEmployeeEntity = gson.fromJson(gson.toJson(bb.getKey()), WorkContactEmployeeEntity.class);
                        contactEmployeeEntity.setContactId(entry.getKey().getId());
                        contactEmployeeEntity.setEmployeeId(empId);
                        if (Objects.nonNull(tagIds) && !tagIds.isEmpty()) {
                            for (Map.Entry<String, WorkContactTagPivotEntity> cc : bb.getValue().entrySet()) {
                                cc.getValue().setContactId(entry.getKey().getId());
                                cc.getValue().setEmployeeId(empId);
                                cc.getValue().setContactTagId(tagIds.get(cc.getKey()));
                                contactTagPivotList.add(cc.getValue());
                            }
                        }
                        contactEmployeeList.add(contactEmployeeEntity);
                    }
                }
            }
            if (contactTagPivotList != null && contactTagPivotList.size() > 0) {
                result = contactTagPivotService.insertAllTagPivot(contactTagPivotList);
            }
            if (contactEmployeeList != null && contactEmployeeList.size() > 0) {
                result = workContactEmployeeService.insertAllContactEmployee(contactEmployeeList);
            }
            return result;
        }
        return result;
    }

    /**
     * @description 同步客户时，对微信返回的客户详情数据做处理
     * @author zhaojinjian
     * @createTime 2020/12/19 14:41
     * @Param list-多个微信客户详情（key-外部联系人的userid,value-请求微信客户详情接口返回的数据），corpid-哪个企业的客户，userId-如果指定跟进成员，则在创建客户成员表实体时过滤掉其他跟进成员的信息，若为空，则创建当前客户所有跟进成员的中间表信息
     */
    private Map<WorkContactEntity, Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>>> externalContactToWorkContact(Map<String, JSONObject> list, Integer corpId, String userId) {
        if (!list.isEmpty() && list.size() > 0) {
            Map<WorkContactEntity, Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>>> contactBelongEmployee = new HashMap<>();
            Map<String, String> cotactAvatarPathMap = new HashMap<>();
            //region 遍历企业微信的客户详情信息，转成本地库实体
            for (Map.Entry<String, JSONObject> mapEntry : list.entrySet()) {
                JSONObject json = mapEntry.getValue();
                if (json.getInteger("errcode") == 0) {
                    JSONObject contactJson = json.getJSONObject("external_contact");
                    String contactAvatar = FileUtils.getContactAvatarPath();
                    cotactAvatarPathMap.put(contactJson.getString("avatar"), contactAvatar);
                    //添加同步时，微信客户信息 返回客户id
                    WorkContactEntity contact = new WorkContactEntity();
                    contact.setAvatar(contactAvatar);
                    contact.setName(contactJson.getString("name"));
                    contact.setUnionid(contactJson.getString("unionid"));
                    contact.setPosition(contactJson.getString("position"));
                    contact.setWxExternalUserid(contactJson.getString("external_userid"));
                    contact.setType(contactJson.getInteger("type"));
                    contact.setGender(contactJson.getInteger("gender"));
                    contact.setCorpFullName(contactJson.getString("corp_full_name"));
                    contact.setCorpName(contactJson.getString("corp_name"));
                    contact.setExternalProfile(contactJson.getString("external_profile"));
                    //contact.setCreatedAt(new Date());
                    contact.setCorpId(corpId);
                    contact.setBusinessNo("");
                    contact.setFollowUpStatus(0);
                    contact.setNickName("");

                    Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>> contactEmployeeMap = new HashMap<>();

                    JSONArray followUser = json.getJSONArray("follow_user");

                    for (int i = 0; i < followUser.size(); i++) {
                        Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>> employeeToTag = new HashMap<>();
                        JSONObject followUserItem = followUser.getJSONObject(i);

                        //region 如果成员id存在，则只能获取当前成员的信息

                        if (userId != null && !userId.isEmpty()) {
                            if (!followUserItem.getString("userid").equals(userId)) {
                                continue;
                            }
                        }
                        //endregion

                        //region 客户员工中间表数据赋值
                        WorkContactEmployeeEntity contactEmployee = new WorkContactEmployeeEntity();
                        contactEmployee.setRemark(followUserItem.getString("remark"));
                        contactEmployee.setDescription(followUserItem.getString("description"));
                        contactEmployee.setCreateTime(new Date(followUserItem.getLong("createtime")));
                        contactEmployee.setRemarkCorpName(followUserItem.getString("remark_corp_name"));
                        contactEmployee.setRemarkMobiles(followUserItem.getString("remark_mobiles"));
                        contactEmployee.setAddWay(followUserItem.getInteger("add_way"));
                        contactEmployee.setOperUserid(followUserItem.getString("oper_userid"));
                        contactEmployee.setState(followUserItem.getString("state"));
                        // contactEmployee.setContactId(contactId);
                        contactEmployee.setCorpId(corpId);
                        //contactEmployee.setEmployeeId(empId);
                        contactEmployee.setStatus(1);
                        contactEmployee.setCreatedAt(new Date());
                        //endregion

                        Map<String, WorkContactTagPivotEntity> contactTagPivotMap = new HashMap<>();
                        JSONArray tags = followUserItem.getJSONArray("tags");
                        for (int l = 0; l < tags.size(); l++) {
                            JSONObject tag = tags.getJSONObject(l);
                            String tagId = tag.getString("tag_id");
                            if (tagId == null || tagId.isEmpty()) {
                                continue;
                            }
                            WorkContactTagPivotEntity contactTagPivot = new WorkContactTagPivotEntity();
                            contactTagPivot.setType(tag.getInteger("type"));
                            //contactTagPivot.setEmployeeId(empId);
                            // contactTagPivot.setContactId(contactId);
                            contactTagPivotMap.put(tag.getString("tag_id"), contactTagPivot);
                        }
                        employeeToTag.put(contactEmployee, contactTagPivotMap);
                        contactEmployeeMap.put(followUserItem.getString("userid"), employeeToTag);
                    }
                    contactBelongEmployee.put(contact, contactEmployeeMap);
                }
            }
            //endregion
            //region 上传头像到阿里云
            if (cotactAvatarPathMap.size() > 0) {
                externalContactService.uploadContactAvatar(cotactAvatarPathMap);
            }
            //endregion
            return contactBelongEmployee;
        }
        return null;
    }

    /**
     * @description 获取流失客户
     * @author zhaojinjian
     * @createTime 2020/12/12 11:46
     */
    @Override
    public JSONObject getlossContact(Integer corpId, List<Integer> empIds, Integer page, Integer perPage) {
        //获取员工下所有流失的客户Id map<empId,contactId>
        LossContact lossContact = workContactEmployeeService.getEmployeeLossContactId(corpId, empIds, page, perPage);

        List<Integer> contactIds = new ArrayList<>(lossContact.getEmpIdAndContactId().values());

        List<WorkContactEntity> contactList = new ArrayList<>();
        if (!contactIds.isEmpty()) {
            //根据流失客户id获取客户详情集合
            QueryWrapper<WorkContactEntity> contactWrapper = new QueryWrapper<>();
            contactWrapper.in("id", contactIds);
            //获取流失客户信息
            contactList.addAll(this.list(contactWrapper));
        }

        //获取所有成员针对于客户的标签
        List<GetEmployeeTagModel> workContactTagList = contactTagService.getEmployeeTapName(empIds);

        //获取多个客户的归属成员
        Map<Integer, String> corpEmployeeName = workEmployeeService.getCorpEmployeeName(corpId, empIds);

        JSONArray resultList = new JSONArray();
        for (Map.Entry<String, Integer> entry : lossContact.getEmpIdAndContactId().entrySet()) {
            Optional<WorkContactEntity> contactOp = contactList.stream().filter(c -> c.getId().equals(entry.getValue())).findAny();
            WorkContactEntity contactEntity = contactOp.get();
            Integer empId = Integer.parseInt(entry.getKey().split("-")[0]);
            Integer id = Integer.parseInt(entry.getKey().split("-")[1]);
            JSONObject json = new JSONObject();
            json.put("contactId", entry.getValue());
            json.put("avatar", AliyunOssUtils.getUrl(contactEntity.getAvatar()));
            WorkContactEmployeeEntity workContactEmployeeEntity = workContactEmployeeServiceImpl.getWorkContactEmployeeInfo(corpId, empId, contactEntity.getId(), id);
            WorkEmployeeEntity workEmployeeEntity = employeeService.getWorkEmployeeInfoById(empId);
            json.put("remark", workEmployeeEntity.getName());
            json.put("nickName", contactEntity.getNickName());
            json.put("deletedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(workContactEmployeeEntity.getDeletedAt().getTime()));
            json.put("tag", workContactTagList.stream().filter(w -> w.getContactId().equals(contactEntity.getId()) && w.getEmpId().equals(empId)).map(GetEmployeeTagModel::getTagName).toArray());
            json.put("employeeName", corpEmployeeName.get(empId));
            resultList.add(json);
        }
        JSONObject resultPage = new JSONObject();
        resultPage.put("perPage", lossContact.getPerPage());
        resultPage.put("total", lossContact.getTotal());
        resultPage.put("totalPage", lossContact.getTotalPage());
        JSONObject result = new JSONObject();
        result.put("list", resultList);
        result.put("page", resultPage);
        return result;
    }

    /**
     * @description 回调函数--添加企业客户事件
     * @author zhaojinjian
     * @createTime 2020/12/16 19:05
     */
    @Override
    public void addExternalContact(int corpId, String wxEmpId, String wxContactId, String welcomeCode, String state) {
        WorkEmployeeEntity employeeEntity = workEmployeeService.getWorkEmployeeInfoByWxEmpId(wxEmpId);
        if (employeeEntity == null) {
            return;
        }

        Integer empId = employeeEntity.getId();
        boolean result = workContactService.createAndSyncContact(corpId, empId, wxEmpId, wxContactId);
        if (result) {
            WorkContactEntity contactEntity = lambdaQuery()
                    .select(WorkContactEntity::getId, WorkContactEntity::getName)
                    .eq(WorkContactEntity::getWxExternalUserid, wxContactId)
                    .one();
            int contactId = contactEntity.getId();
            String contactName = contactEntity.getName();

            if (!StringUtils.hasLength(state)) {
                sendWelcomeMsgService.sendMsg(corpId, welcomeCode, "");
                return;
            }

            int id = Integer.parseInt(state.split("-")[1]);

            if (state.contains("workRoomAutoPullId")) {
                addExternalContactOfRoomAutoLogic(corpId, empId, contactId, contactName, id, welcomeCode);
                return;
            }
            if (state.contains("channelCode")) {
                addExternalContactOfChannelCodeLogic(corpId, empId, contactId, contactName, id, welcomeCode);
                return;
            }
        }
    }

    public void addExternalContactOfRoomAutoLogic(int corpId, int empId, int contactId, String contactName,
                                                  int roomAutoPullId, String welcomeCode) {
        // 获取自动拉群详情
        WorkRoomAutoPullEntity roomAutoPullDetail = workRoomAutoPullService.getRoomAutoPullInfo(roomAutoPullId);
        if (roomAutoPullDetail != null) {
            String tags = roomAutoPullDetail.getTags();
            incrementalContactTagPivot(empId, contactId, tags);

            String leadingWords = roomAutoPullDetail.getLeadingWords().replaceAll("##客户名称##", contactName);
            String roomJson = roomAutoPullDetail.getRooms();
            JSONArray roomJsonArray = JSON.parseArray(roomJson);
            String roomQrcodeMediaId = "";
            for (int i = 0; i < roomJsonArray.size(); i++) {
                JSONObject roomJsonObj = roomJsonArray.getJSONObject(i);
                int roomId = roomJsonObj.getIntValue("roomId");
                int maxNum = roomJsonObj.getIntValue("maxNum");
                WorkRoomEntity roomEntity = roomService.getById(roomId);
                int roomMax = roomEntity.getRoomMax();
                int count = contactRoomService.lambdaQuery()
                        .eq(WorkContactRoomEntity::getRoomId, roomEntity.getId())
                        .count();
                if (count < roomMax && count < maxNum) {
                    // 拉人中 发送此群二维码
                    File roomQrcodeFile = AliyunOssUtils.getFile(roomJsonObj.getString("roomQrcodeUrl"));
                    roomQrcodeMediaId = WxApiUtils.uploadImageToTemp(corpId, roomQrcodeFile);
                    break;
                }
            }
            if (StringUtils.hasLength(roomQrcodeMediaId)) {
                sendWelcomeMsgService.sendMsgOfRoomAutoPull(corpId, welcomeCode, leadingWords, roomQrcodeMediaId);
            }
        }
    }

    public void addExternalContactOfChannelCodeLogic(int corpId, int empId, int contactId, String contactName,
                                                     int channelCodeId, String welcomeCode) {
        ChannelCodeEntity channelCodeEntity = channelCodeService.getById(channelCodeId);
        String tags = channelCodeEntity.getTags();
        incrementalContactTagPivot(empId, contactId, tags);

        Map<String, String> map = channelCodeService.getWelcomeMessageMap(channelCodeEntity);
        if (map != null) {
            map.put("contactName", contactName);
            sendWelcomeMsgService.sendMsgOfChannelCode(corpId, welcomeCode, map);
        }
    }

    private void incrementalContactTagPivot(int empId, int contactId, String tags) {
        JSONArray tagArray = JSONArray.parseArray(tags);
        List<Integer> tagIdList = tagArray.toJavaList(Integer.class);
        workContactService.incrementalContactTagPivot(empId, contactId, tagIdList);
    }

    private MediumEntity getMedium(String mediumId) {
        return mediumService.getMediumById(Integer.valueOf(mediumId));
    }

    @Override
    public ContactDetailVO getContactDetailByWxExternalUserId(String wxExternalUserid) {
        WorkContactEntity workContactEntity = new WorkContactEntity();
        workContactEntity.setWxExternalUserid(wxExternalUserid);
        List<WorkContactEntity> contactEntityList = list(new QueryWrapper(workContactEntity));
        if (contactEntityList.size() < 1) {
            throw new CommonException("未找到客户信息");
        }

        workContactEntity = contactEntityList.get(0);
        ContactDetailVO vo = new ContactDetailVO();
        vo.setId(workContactEntity.getId());
        vo.setName(workContactEntity.getName());
        vo.setAvatar(AliyunOssUtils.getUrl(workContactEntity.getAvatar()));
        vo.setCorpId(workContactEntity.getCorpId());
        return vo;
    }

    @Override
    public List<ContactTrackVO> getContactTrackByContactId(Integer contactId) {
        ContactEmployeeTrackEntity contactEmployeeTrackEntity = new ContactEmployeeTrackEntity();
        contactEmployeeTrackEntity.setContactId(contactId);
        List<ContactEmployeeTrackEntity> list = contactEmployeeTrackMapper.selectList(
                new QueryWrapper<>(contactEmployeeTrackEntity).orderByDesc("created_at")
        );
        List<ContactTrackVO> listVo = new ArrayList<>();
        for (ContactEmployeeTrackEntity entity : list) {
            ContactTrackVO vo = new ContactTrackVO();
            vo.setId(entity.getId());
            vo.setContent(entity.getContent());
            vo.setCreatedAt(DateUtils.formatS1(entity.getCreatedAt().getTime()));
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public void saveTrack(Integer employeeId, Integer contactId, EventEnum eventEnum, String content) {
        ContactEmployeeTrackEntity contactEmployeeTrackEntity = new ContactEmployeeTrackEntity();
        contactEmployeeTrackEntity.setEmployeeId(employeeId);
        contactEmployeeTrackEntity.setContactId(contactId);
        contactEmployeeTrackEntity.setEvent(eventEnum.getCode());
        contactEmployeeTrackEntity.setContent(content);
        contactEmployeeTrackMapper.insert(contactEmployeeTrackEntity);
    }

    private Map<List<WorkContactEmployeeEntity>, List<WorkContactTagPivotEntity>> getList(Map<WorkContactEntity, Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>>> modelMap, Integer empId, Integer contactId) {
        Map<List<WorkContactEmployeeEntity>, List<WorkContactTagPivotEntity>> map = new HashMap<>();
        List<WorkContactEmployeeEntity> contactEmployeeList = new ArrayList<>();
        List<WorkContactTagPivotEntity> contactTagPivotList = new ArrayList<>();
        for (Map.Entry<WorkContactEntity, Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>>> entry : modelMap.entrySet()) {
            for (Map.Entry<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>> aa : entry.getValue().entrySet()) {
                for (Map.Entry<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>> bb : aa.getValue().entrySet()) {
                    if (contactId == null) {
                        contactId = entry.getKey().getId();
                    }
                    bb.getKey().setContactId(contactId);
                    bb.getKey().setEmployeeId(empId);
                    Map<String, Integer> tagIds = contactTagPivotService.getContactTapIdOrWxTagId(empId, contactId);
                    for (Map.Entry<String, WorkContactTagPivotEntity> cc : bb.getValue().entrySet()) {
                        cc.getValue().setContactId(entry.getKey().getId());
                        cc.getValue().setEmployeeId(empId);
                        cc.getValue().setContactTagId(tagIds.get(cc.getKey()));
                    }
                    contactTagPivotList.addAll(bb.getValue().values().stream().filter(c -> c.getContactTagId() != null).collect(Collectors.toList()));
                }
                contactEmployeeList.addAll(aa.getValue().keySet().stream().collect(Collectors.toList()));
            }
        }
        map.put(contactEmployeeList, contactTagPivotList);
        return map;
    }

    /**
     * @description 回调函数--编辑企业客户事件
     * @author zhaojinjian
     * @createTime 2020/12/21 10:57
     */
    @Override
    public void editExternalContact(String externalUserid, String userId) {
        WorkEmployeeEntity employeeEntity = workEmployeeService.getWorkEmployeeInfoByWxEmpId(userId);
        if (employeeEntity != null) {
            JSONArray contactUserId = new JSONArray();
            //获取微信客户详情
            Map<String, JSONObject> contactInfo = externalContactService.getExternalContactMap(contactUserId, employeeEntity.getCorpId());
            Map<WorkContactEntity, Map<String, Map<WorkContactEmployeeEntity, Map<String, WorkContactTagPivotEntity>>>> modelMap = externalContactToWorkContact(contactInfo, employeeEntity.getCorpId(), userId);
            Integer contactId = getContactId(externalUserid);
            if (contactId != null) {
                Optional<WorkContactEntity> contactOp = modelMap.keySet().stream().findAny();
                WorkContactEntity contactEntity = contactOp.get();
                if (contactEntity != null) {
                    contactEntity.setId(contactId);
                    this.updateById(contactEntity);
                    Map<List<WorkContactEmployeeEntity>, List<WorkContactTagPivotEntity>> map = getList(modelMap, employeeEntity.getId(), contactId);
                    for (Map.Entry<List<WorkContactEmployeeEntity>, List<WorkContactTagPivotEntity>> list : map.entrySet()) {
                        //做判断不存在要新增
                        List<WorkContactEmployeeEntity> contactEmployeeEntities = list.getKey();
                        List<WorkContactTagPivotEntity> contactFieldPivots = list.getValue();
                        WorkContactEmployeeEntity contactEmployeeEntity = workContactEmployeeService.getWorkContactEmployeeInfo(employeeEntity.getCorpId(), employeeEntity.getId(), contactId, null);
                        contactEmployeeEntities.get(0).setId(contactEmployeeEntity.getId());
                        workContactEmployeeService.updateContactEmployee(contactEmployeeEntities.get(0));
                        contactTagPivotService.updateContactTagPivot(contactFieldPivots);
                    }
                }
            }
        }
    }

    /**
     * @description 回调函数--删除企业客户事件
     * @author zhaojinjian
     * @createTime 2020/12/21 11:46
     */
    @Override
    public void deleteExternalContact(String externalUserid, String userId) {
        WorkEmployeeEntity employeeEntity = workEmployeeService.getWorkEmployeeInfoByWxEmpId(userId);
        if (employeeEntity != null) {
            QueryWrapper<WorkContactEntity> contactWrapper = new QueryWrapper<>();
            contactWrapper.eq("wx_external_userid", externalUserid);
            WorkContactEntity contactEntity = new WorkContactEntity();
            contactEntity.setDeletedAt(new Date());
            this.update(contactEntity, contactWrapper);
            contactEntity = this.baseMapper.selectOne(contactWrapper);
            workContactEmployeeService.deleteContactEmployee(employeeEntity.getCorpId(), employeeEntity.getId(), contactEntity.getId());
            contactTagPivotService.deleteContactTagPivot(employeeEntity.getId(), contactEntity.getId());
        }
    }

    /**
     * @description 回调函数--客户删除企业成员事件
     * @author zhaojinjian
     * @createTime 2020/12/21 11:54
     */
    @Override
    public void externalContactDeleteEmployee(String externalUserid, String userId) {
        WorkEmployeeEntity employeeEntity = workEmployeeService.getWorkEmployeeInfoByWxEmpId(userId);
        if (employeeEntity != null) {
            Integer contactId = getContactId(externalUserid);
            workContactEmployeeService.deleteContactEmployee(employeeEntity.getCorpId(), employeeEntity.getId(), contactId);
            contactTagPivotService.deleteContactTagPivot(employeeEntity.getId(), contactId);
        }
    }
}
