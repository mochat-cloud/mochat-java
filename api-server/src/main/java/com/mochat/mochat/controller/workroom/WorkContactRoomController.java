package com.mochat.mochat.controller.workroom;

import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.dao.entity.WorkRoomEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.workroom.WorkContactRoomIndexReq;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:客户群成员管理-列表
 * @author: Huayu
 * @time: 2020/12/16 8:34
 */
@RestController
@RequestMapping("/workContactRoom")
public class WorkContactRoomController {

    @Autowired
    private IWorkRoomService workRoomServiceImpl;

    @GetMapping("/index")
    public ApiRespVO index(WorkContactRoomIndexReq workContactRoomIndexReq) {
        Integer workRoomId = workContactRoomIndexReq.getWorkRoomId();
        //查询客户群基本信息
        WorkRoomEntity workRoomEntity = workRoomServiceImpl.getWorkRoom(workRoomId);
        //处理请求参数
        return ApiRespUtils.getApiRespOfOk(workRoomServiceImpl.handelWorkContactRoomData(workRoomEntity, workContactRoomIndexReq));
    }
}
