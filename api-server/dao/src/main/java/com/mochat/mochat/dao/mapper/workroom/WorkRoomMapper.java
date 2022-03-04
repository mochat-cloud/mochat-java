package com.mochat.mochat.dao.mapper.workroom;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.WorkRoomEntity;

import java.util.Map;

/**
 * @description:客户群
 * @author: Huayu
 * @time: 2020/12/8 14:46
 */
public interface WorkRoomMapper extends BaseMapper<WorkRoomEntity> {
    Integer updateWorkRoomsByRoomGroupId(Map<String, Object> map);
}
