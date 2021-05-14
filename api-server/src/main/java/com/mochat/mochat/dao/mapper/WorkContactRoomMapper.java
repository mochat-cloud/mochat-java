package com.mochat.mochat.dao.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.WorkContactRoomEntity;
import com.mochat.mochat.model.transfer.GetContactRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WorkContactRoomMapper extends BaseMapper<WorkContactRoomEntity> {
    List<GetContactRoom> getContactRoomList(@Param("contactIds") List<Integer> contactIds, @Param("roomIds") List<String> roomIds);
    List<WorkContactRoomEntity> getWorkContactRoomIndex(Map<String, Object> map);
    Integer updateWorkContactRoomByIds(WorkContactRoomEntity workContactRoomEntity);
    List<WorkContactRoomEntity> getWorkContactRoomsByRoomId(Map<String, Object> map);
}
