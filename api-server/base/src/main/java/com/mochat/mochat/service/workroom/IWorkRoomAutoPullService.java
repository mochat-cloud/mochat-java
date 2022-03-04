package com.mochat.mochat.service.workroom;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.dao.entity.WorkRoomAutoPullEntity;
import com.mochat.mochat.model.workroom.ReqRoomAutoPullCreateDTO;
import com.mochat.mochat.model.workroom.ReqRoomAutoPullUpdateDTO;
import com.mochat.mochat.model.workroom.WorkRoomAutoPullDetailVO;
import com.mochat.mochat.model.workroom.WorkRoomAutoPullVO;

public interface IWorkRoomAutoPullService extends IService<WorkRoomAutoPullEntity> {

    /**
     * 获取自动拉群管理 - 列表
     *
     * @param qrcodeName  群活码名称[非必填]
     * @param requestPage 分页参数[非必填]
     */
    Page<WorkRoomAutoPullVO> getList(String qrcodeName, ReqPageDto requestPage, ReqPerEnum permission);

    /**
     * 自动拉群管理 - 创建提交
     */
    void createRoomAutoPull(ReqRoomAutoPullCreateDTO req);

    /**
     * 自动拉群管理 - 更新提交
     */
    void updateRoomAutoPullDetail(ReqRoomAutoPullUpdateDTO req);

    /**
     * 自动拉群管理 - 详情
     *
     * @param workRoomAutoPullId 自动拉群ID
     */
    WorkRoomAutoPullDetailVO getRoomAutoPullDetail(Integer workRoomAutoPullId);

    /**
     * @description 获取自动拉群的详情
     * @author zhaojinjian
     * @createTime 2020/12/19 16:09
     */
    WorkRoomAutoPullEntity getRoomAutoPullInfo(Integer workRoomAutoPullId);
}
