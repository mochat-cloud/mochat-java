package com.mochat.mochat.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;;
import com.mochat.mochat.dao.entity.workroom.WorkRoomGroupEntity;

/**
 * @description:客户群分组
 * @author: Huayu
 * @time: 2020/12/8 14:46
 */
public interface WorkRoomGroupMapper extends BaseMapper<WorkRoomGroupEntity> {
    Integer updateWorkRoomGroupById(WorkRoomGroupEntity workRoomGroupEntity);
}
