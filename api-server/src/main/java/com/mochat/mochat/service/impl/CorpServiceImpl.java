package com.mochat.mochat.service.impl;

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

import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * @description: 根据corpId找对象
     * @return:
     * @author: Huayu
     * @time: 2020/11/25 9:25
     */
    @Override
    public List<CorpEntity> getCorpInfoByIdName(String corpIds, String name) {
        List<String> cIds = Arrays.asList(corpIds.split(","));
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("id as corpId", "name as corpName", "wx_corpid as wxCorpId", "created_at as createdAt");
        QueryWrapper.in("id", cIds);
        if (!name.equals("")) {
            QueryWrapper.eq("name", name);
        }
        return this.baseMapper.selectList(QueryWrapper);
    }

    @Override
    public CorpEntity getCorpInfoById(Integer corpId) {
        return this.baseMapper.selectById(corpId);
    }

    @Override
    public List<CorpEntity> getCorpListById(String userId) {
        // Page<CorpEntity> indexDataPage = new Page<CorpEntity>;
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("id as corpId", "name as corpName");
        //QueryWrapper.setEntity(new CorpEntity());
        QueryWrapper.in("id", userId);
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList;
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
     * @time: 2021/3/22 4:14 下午
     * @description
     * @info 因二期权限管理需求, 本人只能查看本公司的信息, 所属其他公司信息查看需要切换公司
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
        //统计总数据
        Map<String, Object> map = getCorpTotalData(corpId);
        //统计日数据
        Map<String, Object> corpDayMap = getCorpDayData(corpId);
        //统计月数据
        Map<String, Object> corpMonthMap = getCorpMonthData(corpId);
        //更新时间
        Map<String, Object> corpTimeMap = getCorpTime(corpId);
        // 合并
        Map<String, Object> combineResultMap = new HashMap();
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
        Date now = new Date();
        Date startDate = org.apache.commons.lang3.time.DateUtils.addDays(now, -31);
        Date endDate = new Date();
        //获取近31天数据
        return getData(startDate, endDate);
    }

    private List<CorpDataEntity> getData(Date startDate, Date endDate) {
        String cls = "id,add_contact_num,add_into_room_num,loss_contact_num,quit_room_num,date";
        List<CorpDataEntity> corpDataEntityList = corpDataServiceImpl.getCorpDayDatasByCorpIdDateOther(startDate, endDate, cls);
        return corpDataEntityList;
    }


    /**
     * @description:统计天数据
     * @return:
     * @author: Huayu
     */
    private Map<String, Object> getCorpDayData(Integer corpId) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //查询今日数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(sdf.format(calendar.getTime()));
        CorpDataEntity corpDataEntityDay = corpDataServiceImpl.getCorpDayDataByCorpIdDate(corpId, date);
        calendar.add(Calendar.DATE, -1);
        //查询昨日数据
        Date date1 = sdf.parse(sdf.format(calendar.getTime()));
        CorpDataEntity corpDataEntityLastDay = corpDataServiceImpl.getCorpDayDataByCorpIdDate(corpId, date1);
        Map<String, Object> map = new HashMap<String, Object>();
        //今日新增客户数
        map.put("addContactNum", corpDataEntityDay.getAddContactNum() == null ? 0 : corpDataEntityDay.getAddContactNum());
        //昨日新增客户数
        map.put("lastAddContactNum", corpDataEntityLastDay.getAddContactNum() == null ? 0 : corpDataEntityLastDay.getAddContactNum());
        //今日新增入群数
        map.put("addIntoRoomNum", corpDataEntityDay.getAddIntoRoomNum() == null ? 0 : corpDataEntityDay.getAddIntoRoomNum());
        //昨日新增入群数
        map.put("lastAddIntoRoomNum", corpDataEntityLastDay.getAddIntoRoomNum() == null ? 0 : corpDataEntityLastDay.getAddIntoRoomNum());
        //今日流失客户数
        map.put("lossContactNum", corpDataEntityDay.getLossContactNum() == null ? 0 : corpDataEntityDay.getLossContactNum());
        //昨日流失客户数
        map.put("lastLossContactNum", corpDataEntityLastDay.getLossContactNum() == null ? 0 : corpDataEntityLastDay.getLossContactNum());
        //今日退群数
        map.put("quitRoomNum", corpDataEntityDay.getLossContactNum() == null ? 0 : corpDataEntityLastDay.getLossContactNum());
        //昨日退群数
        map.put("quitRoomNum", corpDataEntityLastDay.getLossContactNum() == null ? 0 : corpDataEntityLastDay.getLossContactNum());
        return map;
    }


    /**
     * @description:统计总数据
     * @return:
     * @author: Huayu
     */
    private Map<String, Object> getCorpTotalData(Integer corpId) {
        List<WorkContactRoomEntity> totalMember = null;
        //总微信客户数
        List<WorkContactEmployeeEntity> totalContact = contactEmployeeServiceImpl.countWorkContactEmployeesByCorpId(corpId, Status.NORMAL.getCode());
        //总微信群数
        List<WorkRoomEntity> totalRooms = workRoomServiceImpl.countWorkRoomByCorpIds(corpId);
        //查询企业下所有的群
        List<WorkRoomEntity> allRoom = workRoomServiceImpl.getWorkRoomsByCorpId(corpId, "id");
        if (allRoom != null && allRoom.size() > 0) {
            //总群成员数
            StringBuilder sb = new StringBuilder();
            for (WorkRoomEntity workRoomEntity :
                    allRoom) {
                sb.append(workRoomEntity.getId()).append(",");
            }
            String roomIds = sb.substring(0, sb.length() - 1);
            totalMember = contactRoomServiceImpl.countWorkEmployeesByRoomIds(roomIds);
        }
        //总企业有效成员数
        List<WorkEmployeeEntity> totalEmployee = employeeServiceImpl.countWorkEmployeesByCorpId(corpId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("weChatContactNum", totalContact.size());
        map.put("weChatRoomNum", totalRooms.size());
        map.put("roomMemberNum", totalMember.isEmpty() ? 0 : totalMember.size());
        map.put("corpMemberNum", totalEmployee.size());
        return map;
    }


    /**
     * @description:统计月数据
     * @return:
     * @author: Huayu
     */
    private Map<String, Object> getCorpMonthData(Integer corpId) {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        Date beginDate = c.getTime();
        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = ca.getTime();
        List<CorpDataEntity> corpDataEntityMonthList = corpDataServiceImpl.getCorpDayDatasByCorpIdTime(corpId, beginDate, endDate);
        //获取前月的第一天
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        Date lastBeginDate = cal_1.getTime();
        //获取前月的最后一天
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH, 0);//设置为1号,当前日期既为本月第一天
        Date lastEndDate = cale.getTime();
        //查询上月数据
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
        //本月新增客户数countWorkContactEmployeesByCorpId
        map.put("addFriendsNum", addContactSum);
        //上月累计新增客户数
        map.put("lastAddFriendsNum", lastMonthAddContactSum);
        //本月新增社群数
        map.put("monthAddRoomNum", addRoomSum);
        //上月累计新增社群数
        map.put("lastMonthAddRoomNum", lastMonthAddRoomSum);
        //本月新增群成员数
        map.put("monthAddRoomMemberNum", addIntoRoomSum);
        //上月累计新增群成员数
        map.put("lastMonthAddRoomMemberNum", lastMonthAddIntoRoomSum);
        //本月流失客户数
        map.put("monthLossContactNum", lossContactSum);
        //上月累计流失客户数
        map.put("lastMonthLossContactNum", lastMonthLossContactSum);
        return map;
    }


    /**
     * @description:获取最后一次同步客户的时间
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

}
