package com.mochat.mochat.service.workroom;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.dao.entity.WorkRoomEntity;
import com.mochat.mochat.dao.mapper.workroom.WorkRoomMapper;
import com.mochat.mochat.model.WorkRoomIndexRespModel;
import com.mochat.mochat.model.workroom.WXWorkRoomModel;
import com.mochat.mochat.model.workroom.WorkContactRoomIndexReq;
import com.mochat.mochat.model.workroom.WorkContactRoomIndexResp;
import com.mochat.mochat.model.workroom.WorkRoomIndexModel;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/3/17 3:58 下午
 * @description 客户群服务
 */
public interface IWorkRoomService extends IService<WorkRoomEntity> {
    Integer updateWorkRoomsByRoomGroupId(Integer workRoomGroupId, int i);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 3:59 下午
     * @description 获取客户群列表
     */
    Page<WorkRoomIndexRespModel> getWorkRoomList(WorkRoomIndexModel workRoomIndexModel, ReqPerEnum permission);

    WorkRoomEntity getWorkRoom(Integer workRoomId);

    WorkContactRoomIndexResp handelWorkContactRoomData(WorkRoomEntity workRoomEntity, WorkContactRoomIndexReq workContactRoomIndexReq);

    List<WorkRoomEntity> getWorkRoomsByIds(String workContactRoomIds, String clStr);

    boolean syncWorkRoomIndex(Integer corpIds, List<WXWorkRoomModel> WXWorkRoomModelList, Integer isFlag) throws ParseException;

    List<WorkRoomEntity> getWorkRoomsByCorpId(Integer corpId, String s);

    List<WorkRoomEntity> getWorkRoomsByWxChatId(List<WXWorkRoomModel> wxWorkRoomModelList, String s);

    public Map<String, Object> handelWXWorkRoomModelData(List<WXWorkRoomModel> WXWorkRoomModelList, Integer corpId, int isSingle);

    JSONObject getWorkRoomSelectData(Integer corpId, String roomName, Integer roomGroupId);

    List<WorkRoomEntity> getWorkRoomsByChatId(List<String> wxRoomIdArr, String s);

    @Override
    WorkRoomMapper getBaseMapper();

    List<WorkRoomEntity> countWorkRoomByCorpIds(Integer corpId);

    Integer getCountOfRoomByCorpIdStartTimeEndTime(Integer corpId, String startTime, String endTime);
}
