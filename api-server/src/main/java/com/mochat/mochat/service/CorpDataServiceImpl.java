package com.mochat.mochat.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.workupdatetime.TypeEnum;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.mapper.CorpDataMapper;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.IWorkContactEmployeeService;
import com.mochat.mochat.service.impl.IWorkContactRoomService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description:首页数据
 * @author: Huayu
 * @time: 2021/4/14 13:33
 */
@Service
public class CorpDataServiceImpl extends ServiceImpl<CorpDataMapper, CorpDataEntity> implements ICorpDataService {
    @Autowired
    private ICorpService corpServiceImpl;

    @Autowired
    private IWorkContactEmployeeService workContactEmployeeServiceImpl;

    @Autowired
    private IWorkRoomService workRoomServiceImpl;

    @Autowired
    private IWorkContactRoomService workContactRoomServiceImpl;

    @Autowired
    private IWorkUpdateTimeService workUpdateTimeServiceImpl;

    private final static Logger logger = LoggerFactory.getLogger(CorpDataServiceImpl.class);

    @Override
    public void handle() {
        //查询所有企业
        List<CorpEntity> corpEntityList = corpServiceImpl.getCorps("id");
        //处理企业数据
        for (CorpEntity corpEntity :
                corpEntityList) {
            //计算企业当日新增数据 并插入日数据表
            handleDayData(corpEntity.getCorpId());
            //记录更新时间
            updateTime(corpEntity.getCorpId());
        }
    }

    @Override
    public CorpDataEntity getCorpDayDataByCorpIdDate(Integer corpId, Date date) {
        String dateStr = DateUtils.formatS3(date.getTime());
        String startTime = DateUtils.getDateOfDayStartByS3(dateStr);
        String endTime = DateUtils.getDateOfDayEndByS3(dateStr);
        QueryWrapper<CorpDataEntity> corpDataEntityWrapper = new QueryWrapper<>();
        corpDataEntityWrapper.eq("corp_id", corpId);
        corpDataEntityWrapper.between("date", startTime, endTime);
        List<CorpDataEntity> entityList = list(corpDataEntityWrapper);
        if (entityList.isEmpty()) {
            return new CorpDataEntity();
        } else {
            return entityList.get(0);
        }
    }

    @Override
    public List<CorpDataEntity> getCorpDayDatasByCorpIdTime(Integer corpId, Date beginDate, Date endDate) {
        QueryWrapper<CorpDataEntity> corpDataEntityWrapper = new QueryWrapper();
        corpDataEntityWrapper.getSqlSelect();
        corpDataEntityWrapper.eq("corp_id", corpId);
        corpDataEntityWrapper.ge("date", beginDate);
        corpDataEntityWrapper.lt("date", endDate);
        return this.baseMapper.selectList(corpDataEntityWrapper);
    }

    @Override
    public List<CorpDataEntity> getCorpDayDatasByCorpIdDateOther(Date startDate, Date endDate, String cls) {
        QueryWrapper<CorpDataEntity> corpDataEntityWrapper = new QueryWrapper();
        corpDataEntityWrapper.select(cls);
        corpDataEntityWrapper.eq("corp_id", AccountService.getCorpId());
        corpDataEntityWrapper.ge("date", startDate);
        corpDataEntityWrapper.lt("date", endDate);
        corpDataEntityWrapper.orderByAsc("date");
        return this.baseMapper.selectList(corpDataEntityWrapper);
    }


    /**
     * @description:计算企业当日新增数据 并插入日数据表
     * @return:
     * @author: Huayu
     */
    private void handleDayData(Integer corpId) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date());
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        //当日新增客户数
        Date startTime = calendar1.getTime();
        Date endTime = calendar.getTime();
        List<WorkContactEmployeeEntity> contact = workContactEmployeeServiceImpl.countWorkContactEmployeesByCorpIdTime(corpId, startTime, endTime);
        //今日新增社群数
        List<WorkRoomEntity> room = workRoomServiceImpl.countAddWorkRoomsByCorpIdTime(corpId, startTime, endTime);
        //查询企业下所有的群
        List<WorkRoomEntity> WorkRoomEntityList = workRoomServiceImpl.getWorkRoomsByCorpId(corpId, "id");
        List<WorkContactRoomEntity> intoRoom = null;
        List<WorkContactRoomEntity> outRoom = null;
        if (WorkRoomEntityList != null && WorkRoomEntityList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (WorkRoomEntity workRoomEntity :
                    WorkRoomEntityList) {
                sb.append(workRoomEntity.getId()).append(",");
            }
            //当日新增入群数
            String roomIds = sb.substring(0, sb.length() - 1);
            intoRoom = workContactRoomServiceImpl.countAddWorkContactRoomsByRoomIdTime(roomIds, startTime, endTime);
            //今日退群人数
            outRoom = workContactRoomServiceImpl.countQuitWorkContactRoomsByRoomIdTime(roomIds, startTime, endTime);
        }
        //今日流失客户
        List<WorkContactEmployeeEntity> lossContact = workContactEmployeeServiceImpl.countLossWorkContactEmployeesByCorpIdTime(corpId, startTime, endTime);
        CorpDataEntity corpDataEntity = new CorpDataEntity();
        corpDataEntity.setCorpId(corpId);
        corpDataEntity.setAddContactNum(contact.size());
        corpDataEntity.setAddRoomNum(room.size());
        corpDataEntity.setAddIntoRoomNum(intoRoom == null ? 0 : intoRoom.size());
        corpDataEntity.setLossContactNum(lossContact.size());
        corpDataEntity.setQuitRoomNum(outRoom == null ? 0 : outRoom.size());
        corpDataEntity.setDate(startTime);
        //查询日数据表中是否已有今日数据
        CorpDataEntity corpData = getCorpDayDataByCorpIdDate(corpId, startTime);
        if (corpData != null) {
            //更新
            Integer i = updateCorpDayDataById(corpData.getId(), corpDataEntity);
            if (i < 1) {
                logger.error("更新企业日数据失败>>>>>>>>>>>");
            }
        } else {
            //新增
            Integer i = createCorpDayData(corpDataEntity);
            if (i < 1) {
                logger.error("新增企业日数据失败>>>>>>>>>>>");

            }
        }
    }


    private Integer createCorpDayData(CorpDataEntity corpDataEntity) {
        Integer i = this.baseMapper.insert(corpDataEntity);
        return i;
    }


    private Integer updateCorpDayDataById(Integer id, CorpDataEntity corpDataEntity) {
        UpdateWrapper<CorpDataEntity> corpDataEntityUpdateWrapper = new UpdateWrapper();
        corpDataEntityUpdateWrapper.eq("id", id);
        int i = this.baseMapper.update(corpDataEntity, corpDataEntityUpdateWrapper);
        return i;
    }


    /**
     * @description:记录更新时间
     * @return:
     * @author: Huayu
     */
    private void updateTime(Integer corpId) {
        //查询当前企业有没有同步客户的时间
        WorkUpdateTimeEntity workUpdateTimeEntity = workUpdateTimeServiceImpl.getWorkUpdateTimeByCorpIdType(corpId, TypeEnum.CORP_DATA.getCode());
        if (workUpdateTimeEntity != null) {
            workUpdateTimeEntity.setLastUpdateTime(new Date());
            Integer id = workUpdateTimeEntity.getId();
            Integer i = workUpdateTimeServiceImpl.updateWorkUpdateTimeById(id, workUpdateTimeEntity);
            if (i < 1) {
                throw new CommonException("更新企业日数据时间失败");
            }
        } else {
            //如果没有新增
            WorkUpdateTimeEntity workUpdateTimeEntity1 = new WorkUpdateTimeEntity();
            workUpdateTimeEntity1.setCorpId(corpId);
            workUpdateTimeEntity1.setType(TypeEnum.CORP_DATA.getCode());
            workUpdateTimeEntity1.setLastUpdateTime(new Date());
            Integer i = workUpdateTimeServiceImpl.createWorkUpdateTime(workUpdateTimeEntity1);
            if (i < 1) {
                throw new CommonException("添加企业日数据时间失败");
            }
        }
    }

}
