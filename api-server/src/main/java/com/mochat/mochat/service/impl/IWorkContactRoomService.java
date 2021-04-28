package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.WorkContactRoomEntity;
import com.mochat.mochat.dao.mapper.WorkContactRoomMapper;
import com.mochat.mochat.model.transfer.GetContactRoom;
import com.mochat.mochat.model.workroom.WorkContactRoomIndexReq;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IWorkContactRoomService extends IService<WorkContactRoomEntity> {
    List<GetContactRoom> GetContactRoomArray(List<String> roomIds, List<Integer> contactIds);
    Map<Integer, Long> getContactRoomSum(List<Integer> roomIds);
    List<WorkContactRoomEntity> getWorkContactRoomsByRoomId(Integer id);

    List<WorkContactRoomEntity> getWorkContactRoomIndex(WorkContactRoomIndexReq workContactRoomIndexReq, String workEmployeeIds, String workContactIds);

    List<WorkContactRoomEntity> getWorkContactRoomsByWxUserId(String wxUserId, String room_id);

    List<WorkContactRoomEntity> getIdByRoomIdAndWxUserId(Integer id, String wxUserId);

    Integer createWorkContactRoom(WorkContactRoomEntity workContactRoomEntity);

    boolean batchUpdateByIds(List<WorkContactRoomEntity> workContactRoomEntityUpdateList);

    Integer updateWorkContactRoomByIds(String deleteContactRoomIdArr, long time, Integer status);

    List<WorkContactRoomEntity> getWorkContactRoomsInfoByRoomId(Integer id);

    @Override
    WorkContactRoomMapper getBaseMapper();

    List<WorkContactRoomEntity> countWorkEmployeesByRoomIds(String roomIds);

    List<WorkContactRoomEntity> countAddWorkContactRoomsByRoomIdTime(String roomIds, Date startTime, Date endTime);

    List<WorkContactRoomEntity> countQuitWorkContactRoomsByRoomIdTime(String roomIds, Date startTime, Date endTime);

    boolean createWorkContactRooms(List<WorkContactRoomEntity> workContactRoomEntityCreateList);
}
