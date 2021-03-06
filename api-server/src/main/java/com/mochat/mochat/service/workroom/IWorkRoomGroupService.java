package com.mochat.mochat.service.workroom;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.workroom.WorkRoomGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2021/3/17 5:46 下午
 * @description 客户群分组服务
 */
public interface IWorkRoomGroupService extends IService<WorkRoomGroupEntity> {
    Map<String,Object> getWorkRoomGroupList(String corpIds, Integer pageNo, Integer pageCount);

    WorkRoomGroupEntity getWorkRoomGroupById(Integer workRoomGroupId);

    Integer deleteWorkRoomGroup(Integer workRoomGroupId);

    WorkRoomGroupEntity getWorkRoomGroupsByCorpId(Integer corpId,String name);

    boolean createWorkRoomGroup(WorkRoomGroupEntity workRoomGroupEntity);

    Integer updateWorkRoomGroupById(Integer id, String workRoomGroupName);

    List<WorkRoomGroupEntity> getWorkRoomGroupByIds(String roomGroupIdArr);
}
