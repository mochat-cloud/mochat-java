package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.WorkContactRoomEntity;
import com.mochat.mochat.dao.mapper.WorkContactRoomMapper;
import com.mochat.mochat.dao.vo.GetContactRoomVo;
import com.mochat.mochat.model.workroom.WorkContactRoomIndexReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkContactRoomServiceImpl extends ServiceImpl<WorkContactRoomMapper, WorkContactRoomEntity> implements IWorkContactRoomService {

    @Resource
    private WorkContactRoomMapper workContactRoomMapper;

    @Override
    public List<GetContactRoomVo> GetContactRoomArray(List<String> roomIds, List<Integer> contactIds) {
        return this.baseMapper.getContactRoomList(contactIds, roomIds);
    }


    /**
     * @description:通过客户群id获得信息
     * @return:
     * @author: Huayu
     * @time: 2020/12/11 9:43
     */
    @Override
    public List<WorkContactRoomEntity> getWorkContactRoomsByRoomId(Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("room_id", id);
        return this.workContactRoomMapper.getWorkContactRoomsByRoomId(map);
    }

    /**
     * @description 获取当前群人数
     * @author zhaojinjian
     * @createTime 2020/12/14 16:00
     */
    @Override
    public Map<Integer, Long> getContactRoomSum(List<Integer> roomIds) {
        QueryWrapper<WorkContactRoomEntity> contactRoomWrapper = new QueryWrapper<>();
        contactRoomWrapper.eq("status", 1);
        contactRoomWrapper.in("room_id", roomIds);
        List<WorkContactRoomEntity> contactRoomList = this.list(contactRoomWrapper);
        if (contactRoomList != null) {
            return contactRoomList.stream().collect(Collectors.groupingBy(WorkContactRoomEntity::getRoomId, Collectors.counting()));
        }
        return null;
    }


    /**
     * @description:通过条件获得成员基础信息
     * @return:
     * @author: Huayu
     * @time: 2020/12/11 9:43
     */
    @Override
    public List<WorkContactRoomEntity> getWorkContactRoomIndex(WorkContactRoomIndexReq workContactRoomIndexReq, String workEmployeeIds, String workContactIds) {
        String clStr = "wxUserId,contact_id,contact_id,employee_id,unionid,room_id,join_scene,type,status,join_time,out_time";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("roomId", workContactRoomIndexReq.getWorkRoomId());
        map.put("status", workContactRoomIndexReq.getStatus());
        map.put("joinTimeStart", workContactRoomIndexReq.getStartTime());
        map.put("joinTimeEnd", workContactRoomIndexReq.getEndTime());
        map.put("workEmployeeIds", workEmployeeIds);
        map.put("workContactIds", workContactIds);
        List<WorkContactRoomEntity> workContactRoomList = workContactRoomMapper.getWorkContactRoomIndex(map);
        return workContactRoomList;
    }

    /**
     * @description:根据条件检索成员所在群的信息
     * @return:
     * @author: Huayu
     * @time: 2020/12/17 14:13
     */
    @Override
    public List<WorkContactRoomEntity> getWorkContactRoomsByWxUserId(String wxUserId, String room_id) {
        QueryWrapper<WorkContactRoomEntity> workContactRoomQueryWrapper = new QueryWrapper<WorkContactRoomEntity>();
        workContactRoomQueryWrapper.select(room_id);
        workContactRoomQueryWrapper.eq("wx_user_id", wxUserId);
        return this.baseMapper.selectList(workContactRoomQueryWrapper);
    }

    @Override
    public List<WorkContactRoomEntity> getIdByRoomIdAndWxUserId(Integer id, String wxUserId) {
        QueryWrapper<WorkContactRoomEntity> workContactRoomQueryWrapper = new QueryWrapper<WorkContactRoomEntity>();
        workContactRoomQueryWrapper.select("id");
        workContactRoomQueryWrapper.eq("wx_user_id", wxUserId);
        workContactRoomQueryWrapper.eq("room_id", id);
        return this.baseMapper.selectList(workContactRoomQueryWrapper);
    }

    @Override
    public Integer createWorkContactRoom(WorkContactRoomEntity workContactRoomEntity) {
        return this.baseMapper.insert(workContactRoomEntity);
    }

    @Override
    public boolean batchUpdateByIds(List<WorkContactRoomEntity> workContactRoomEntityUpdateList) {
        return this.updateBatchById(workContactRoomEntityUpdateList);
    }

    @Override
    @Transactional
    public Integer updateWorkContactRoomByIds(String deleteContactRoomIdArr, long time, Integer status) {
        String[] str = deleteContactRoomIdArr.split(",");
        Integer i = 0;
        for (String id : str) {
            WorkContactRoomEntity workContactRoomEntity = new WorkContactRoomEntity();
            workContactRoomEntity.setId(Integer.valueOf(id));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sd = sdf.format(new Date(time)); // 时间戳转换日期
            workContactRoomEntity.setOutTime(sd);
            workContactRoomEntity.setStatus(2);
            i = workContactRoomMapper.updateWorkContactRoomByIds(workContactRoomEntity);
        }
        return i;
    }

    @Override
    public List<WorkContactRoomEntity> getWorkContactRoomsInfoByRoomId(Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("room_id", id);
        return this.workContactRoomMapper.getWorkContactRoomsByRoomId(map);
    }

    @Override
    public List<WorkContactRoomEntity> countWorkEmployeesByRoomIds(String roomIds) {
        QueryWrapper<WorkContactRoomEntity> workContactRoomQueryWrapper = new QueryWrapper<WorkContactRoomEntity>();
        workContactRoomQueryWrapper.in("room_id", roomIds);
        return this.baseMapper.selectList(workContactRoomQueryWrapper);
    }

    @Override
    public Long countAddWorkContactRoomsByRoomIdTime(String roomIds, String startTime, String endTime) {
        return lambdaQuery()
                .in(WorkContactRoomEntity::getRoomId, roomIds)
                .ge(WorkContactRoomEntity::getJoinTime, startTime)
                .le(WorkContactRoomEntity::getJoinTime, endTime)
                .count();
    }

    @Override
    public Long countQuitWorkContactRoomsByRoomIdTime(String roomIds, String startTime, String endTime) {
        return lambdaQuery()
                .in(WorkContactRoomEntity::getRoomId, roomIds)
                .ge(WorkContactRoomEntity::getOutTime, startTime)
                .le(WorkContactRoomEntity::getOutTime, endTime)
                .count();
    }

    @Override
    public boolean createWorkContactRooms(List<WorkContactRoomEntity> workContactRoomEntityCreateList) {
        return this.saveBatch(workContactRoomEntityCreateList);
    }

}
