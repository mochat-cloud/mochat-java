package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.em.workcontactemployee.Status;
import com.mochat.mochat.common.em.workupdatetime.TypeEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;
import com.mochat.mochat.dao.mapper.corp.CorpMapper;
import com.mochat.mochat.model.corp.CorpPageItemVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.ICorpDataService;
import com.mochat.mochat.service.IWorkUpdateTimeService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/23 20:19
 */
@Service
@Transactional
public class CorpServiceImpl extends ServiceImpl<CorpMapper, CorpEntity> implements ICorpService {

    @Autowired
    private IWorkMsgConfigService msgConfigService;

    @Autowired
    private ICorpDataService corpDataServiceImpl;

    @Autowired
    private IWorkUpdateTimeService workUpdateTimeServiceImpl;

    @Autowired
    private IWorkContactEmployeeService contactEmployeeServiceImpl;

    @Autowired
    private IWorkRoomService workRoomServiceImpl;

    @Autowired
    private IWorkContactRoomService contactRoomServiceImpl;

    @Autowired
    private IWorkEmployeeService employeeServiceImpl;

    @Override
    public List<CorpEntity> listByLoginUserIdAndCorpName(Integer loginUserId, String corpName) {
        List<Integer> corpIdList = employeeServiceImpl.listCorpIdByLoginUserId(loginUserId);
        if (corpIdList.isEmpty()) {
            return Collections.emptyList();
        }

        if (StringUtils.hasLength(corpName)) {
            return lambdaQuery()
                    .like(CorpEntity::getCorpName, corpName)
                    .in(CorpEntity::getCorpId, corpIdList)
                    .list();
        } else {
            return listByIds(corpIdList);
        }
    }

    @Override
    public List<CorpEntity> listByLoginUserId(Integer loginUserId) {
        return listByLoginUserIdAndCorpName(loginUserId, null);
    }

    @Override
    public String getWxCorpIdById(Integer corpId) {
        CorpEntity entity = getById(corpId);
        if (null != entity) {
            return entity.getWxCorpId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean createCorp(CorpEntity corpEntity) {
        boolean flag = this.save(corpEntity);
        return flag;
    }

    @Override
    public List<CorpEntity> getCorpInfoByCorpName(String corpName) {
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("id as corpId");
        //QueryWrapper.setEntity(new CorpEntity());
        QueryWrapper.eq("name", corpName);
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList;
    }

    @Override
    @Transactional
    public Integer updateCorpByCorpId(CorpEntity corpEntity) {
        Integer i = this.baseMapper.updateById(corpEntity);
        return i;
    }

    @Override
    public CorpEntity getCorpsByWxCorpId(String wxCorpId, String id) {
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select(id);
        //QueryWrapper.setEntity(new CorpEntity());
        QueryWrapper.eq("wx_corpid", wxCorpId);
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList.get(0);
    }

    @Override
    public List<CorpEntity> getCorpIds(String clStr) {
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select(clStr + " as corpId");
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/22 4:14 ??????
     * @description
     * @info ???????????????????????????, ????????????????????????????????????, ????????????????????????????????????????????????
     */
    @Override
    public Page<CorpPageItemVO> getCorpPageList(String corpName, RequestPage requestPage, ReqPerEnum permission) {
        Page<CorpEntity> corpPage = ApiRespUtils.initPage(requestPage);
        LambdaQueryChainWrapper<CorpEntity> wrapper = lambdaQuery()
                .eq(CorpEntity::getCorpId, AccountService.getCorpId());
        if (Objects.nonNull(corpName) && !corpName.isEmpty()) {
            wrapper.eq(CorpEntity::getCorpName, corpName);
        }
        wrapper.page(corpPage);

        List<CorpPageItemVO> voList = new ArrayList<>();
        for (CorpEntity corpEntity : corpPage.getRecords()) {
            WorkMsgConfigEntity workMsgConfigEntity = msgConfigService.getByCorpId(corpEntity.getCorpId());
            CorpPageItemVO vo = new CorpPageItemVO();
            vo.setCorpId(corpEntity.getCorpId());
            vo.setCorpName(corpEntity.getCorpName());
            vo.setWxCorpId(corpEntity.getWxCorpId());
            vo.setCreatedAt(DateUtils.formatS1(corpEntity.getCreatedAt().getTime()));
            vo.setChatApplyStatus(workMsgConfigEntity.getChatApplyStatus());
            vo.setChatStatus(workMsgConfigEntity.getChatStatus());
            vo.setMessageCreatedAt(DateUtils.formatS1(workMsgConfigEntity.getCreatedAt().getTime()));
            voList.add(vo);
        }
        return ApiRespUtils.transPage(corpPage, voList);
    }


    @Override
    public Map<String, Object> handleCorpDta() throws Exception {
        Integer corpId = AccountService.getCorpId();
        //???????????????
        Map<String, Object> map = getCorpTotalData(corpId);
        //???????????????
        Map<String, Object> corpDayMap = getCorpDayData(corpId);
        //???????????????
        Map<String, Object> corpMonthMap = getCorpMonthData(corpId);
        //????????????
        Map<String, Object> corpTimeMap = getCorpTime(corpId);
        // ??????
        Map<String, Object> combineResultMap = new HashMap<>();
        combineResultMap.putAll(map);
        combineResultMap.putAll(corpDayMap);
        combineResultMap.putAll(corpMonthMap);
        combineResultMap.putAll(corpTimeMap);
        return combineResultMap;
    }

    @Override
    public List<CorpEntity> getCorps(String id) {
        QueryWrapper<CorpEntity> corpEntityQueryWrapper = new QueryWrapper<>();
        List<CorpEntity> corpList = this.baseMapper.selectList(corpEntityQueryWrapper);
        return corpList;
    }

    @Override
    public List<CorpDataEntity> handleLineChatDta() {
        Calendar c = Calendar.getInstance();
        Date endDate = c.getTime();

        c.add(Calendar.DATE, -31);
        Date startDate = c.getTime();

        //?????????31?????????
        return getData(startDate, endDate);
    }

    private List<CorpDataEntity> getData(Date startDate, Date endDate) {
        String cls = "id,add_contact_num,add_into_room_num,loss_contact_num,quit_room_num,date";
        List<CorpDataEntity> corpDataEntityList = corpDataServiceImpl.getCorpDayDatasByCorpIdDateOther(startDate, endDate, cls);
        return corpDataEntityList;
    }


    /**
     * @description:???????????????
     * @return:
     * @author: Huayu
     */
    private Map<String, Object> getCorpDayData(Integer corpId) {
        // ??????????????????
        Calendar calendar = Calendar.getInstance();
        CorpDataEntity corpDataEntityDay = corpDataServiceImpl.getCorpDayDataByCorpIdDate(corpId, calendar.getTime());
        if (corpDataEntityDay == null) {
            corpDataEntityDay = new CorpDataEntity();
        }

        // ??????????????????
        calendar.add(Calendar.DATE, -1);
        CorpDataEntity corpDataEntityLastDay = corpDataServiceImpl.getCorpDayDataByCorpIdDate(corpId, calendar.getTime());
        if (corpDataEntityLastDay == null) {
            corpDataEntityLastDay = new CorpDataEntity();
        }

        Map<String, Object> map = new HashMap<>();
        // ?????????????????????
        map.put("addContactNum", corpDataEntityDay.getAddContactNum() == null ? 0 : corpDataEntityDay.getAddContactNum());
        // ?????????????????????
        map.put("lastAddContactNum", corpDataEntityLastDay.getAddContactNum() == null ? 0 : corpDataEntityLastDay.getAddContactNum());
        // ?????????????????????
        map.put("addIntoRoomNum", corpDataEntityDay.getAddIntoRoomNum() == null ? 0 : corpDataEntityDay.getAddIntoRoomNum());
        // ?????????????????????
        map.put("lastAddIntoRoomNum", corpDataEntityLastDay.getAddIntoRoomNum() == null ? 0 : corpDataEntityLastDay.getAddIntoRoomNum());
        // ?????????????????????
        map.put("lossContactNum", corpDataEntityDay.getLossContactNum() == null ? 0 : corpDataEntityDay.getLossContactNum());
        // ?????????????????????
        map.put("lastLossContactNum", corpDataEntityLastDay.getLossContactNum() == null ? 0 : corpDataEntityLastDay.getLossContactNum());
        // ???????????????
        map.put("quitRoomNum", corpDataEntityDay.getQuitRoomNum() == null ? 0 : corpDataEntityDay.getQuitRoomNum());
        // ???????????????
        map.put("lastQuitRoomNum", corpDataEntityLastDay.getQuitRoomNum() == null ? 0 : corpDataEntityLastDay.getQuitRoomNum());
        return map;
    }


    /**
     * @description:???????????????
     * @return:
     * @author: Huayu
     */
    private Map<String, Object> getCorpTotalData(Integer corpId) {
        List<WorkContactRoomEntity> totalMember = new ArrayList<>();
        //??????????????????
        List<WorkContactEmployeeEntity> totalContact = contactEmployeeServiceImpl.countWorkContactEmployeesByCorpId(corpId, Status.NORMAL.getCode());
        //???????????????
        List<WorkRoomEntity> totalRooms = workRoomServiceImpl.countWorkRoomByCorpIds(corpId);
        //???????????????????????????
        List<WorkRoomEntity> allRoom = workRoomServiceImpl.getWorkRoomsByCorpId(corpId, "id");
        if (allRoom != null && allRoom.size() > 0) {
            //???????????????
            StringBuilder sb = new StringBuilder();
            for (WorkRoomEntity workRoomEntity :
                    allRoom) {
                sb.append(workRoomEntity.getId()).append(",");
            }
            String roomIds = sb.substring(0, sb.length() - 1);
            totalMember.addAll(contactRoomServiceImpl.countWorkEmployeesByRoomIds(roomIds));
        }
        //????????????????????????
        List<WorkEmployeeEntity> totalEmployee = employeeServiceImpl.countWorkEmployeesByCorpId(corpId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("weChatContactNum", totalContact.size());
        map.put("weChatRoomNum", totalRooms.size());
        map.put("roomMemberNum", totalMember.size());
        map.put("corpMemberNum", totalEmployee.size());
        return map;
    }


    /**
     * @description:???????????????
     * @return:
     * @author: Huayu
     */
    private Map<String, Object> getCorpMonthData(Integer corpId) {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //???????????????????????????
        Calendar c = Calendar.getInstance();
        DateUtils.getMonthOfStart(c);
        Date beginDate = c.getTime();

        //???????????????????????????
        Calendar ca = Calendar.getInstance();
        DateUtils.getMonthOfEnd(ca);
        Date endDate = ca.getTime();
        List<CorpDataEntity> corpDataEntityMonthList = corpDataServiceImpl.getCorpDayDatasByCorpIdTime(corpId, beginDate, endDate);

        //????????????????????????
        Calendar cal_1 = Calendar.getInstance();//??????????????????
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//?????????1???,?????????????????????????????????
        DateUtils.getDayOfStart(cal_1);
        Date lastBeginDate = cal_1.getTime();

        //???????????????????????????
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH, 0);//?????????1???,?????????????????????????????????
        DateUtils.getDayOfEnd(cale);
        Date lastEndDate = cale.getTime();

        //??????????????????
        List<CorpDataEntity> corpDataEntityLastMonthList = corpDataServiceImpl.getCorpDayDatasByCorpIdTime(corpId, lastBeginDate, lastEndDate);
        Map<String, Object> map = new HashMap<String, Object>();
        Integer addContactSum = 0;
        Integer lastMonthAddContactSum = 0;
        Integer addRoomSum = 0;
        Integer lastMonthAddRoomSum = 0;
        Integer addIntoRoomSum = 0;
        Integer lastMonthAddIntoRoomSum = 0;
        Integer lossContactSum = 0;
        Integer lastMonthLossContactSum = 0;
        for (CorpDataEntity corpData :
                corpDataEntityMonthList) {
            Integer addContactNum = corpData.getAddContactNum();
            Integer addRoomNum = corpData.getAddRoomNum();
            Integer addIntoRoomNum = corpData.getAddIntoRoomNum();
            Integer lossContactNum = corpData.getQuitRoomNum();
            addContactSum = addContactSum + addContactNum;
            addRoomSum = addRoomSum + addRoomNum;
            addIntoRoomSum = addIntoRoomSum + addIntoRoomNum;
            lossContactSum = lossContactSum + lossContactNum;
        }
        for (CorpDataEntity corpData :
                corpDataEntityLastMonthList) {
            Integer lastMonthAddContactNum = corpData.getAddContactNum();
            Integer lastMonthAddRoomNum = corpData.getAddRoomNum();
            Integer lastMonthAddIntoRoomNum = corpData.getAddIntoRoomNum();
            Integer lastMonthLossContactNum = corpData.getQuitRoomNum();
            lastMonthAddContactSum = lastMonthAddContactSum + lastMonthAddContactNum;
            lastMonthAddRoomSum = lastMonthAddRoomSum + lastMonthAddRoomNum;
            lastMonthAddIntoRoomSum = lastMonthAddIntoRoomSum + lastMonthAddIntoRoomNum;
            lastMonthLossContactSum = lastMonthLossContactSum + lastMonthLossContactNum;
        }
        //?????????????????????countWorkContactEmployeesByCorpId
        map.put("addFriendsNum", addContactSum);
        //???????????????????????????
        map.put("lastAddFriendsNum", lastMonthAddContactSum);
        //?????????????????????
        map.put("monthAddRoomNum", addRoomSum);
        //???????????????????????????
        map.put("lastMonthAddRoomNum", lastMonthAddRoomSum);
        //????????????????????????
        map.put("monthAddRoomMemberNum", addIntoRoomSum);
        //??????????????????????????????
        map.put("lastMonthAddRoomMemberNum", lastMonthAddIntoRoomSum);
        //?????????????????????
        map.put("monthLossContactNum", lossContactSum);
        //???????????????????????????
        map.put("lastMonthLossContactNum", lastMonthLossContactSum);
        return map;
    }


    /**
     * @description:???????????????????????????????????????
     * @return:
     * @author: Huayu
     */
    private Map<String, Object> getCorpTime(Integer corpId) {
        Integer type = TypeEnum.CORP_DATA.getCode();
        WorkUpdateTimeEntity workUpdateTimeEntity = workUpdateTimeServiceImpl.getWorkUpdateTimeByCorpIdType(corpId, type);
        Map<String, Object> map = new HashMap<String, Object>();
        if (workUpdateTimeEntity == null) {
            return null;
        }
        map.put("updateTime", workUpdateTimeEntity.getLastUpdateTime());
        return map;
    }

    @Override
    public List<Integer> getAllCorpId() {
        return lambdaQuery()
                .select(CorpEntity::getCorpId)
                .list()
                .stream()
                .map(CorpEntity::getCorpId)
                .collect(Collectors.toList());
    }

}
