package com.mochat.mochat.controller.workroom;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.model.workroom.ReqRoomAutoPullCreateDTO;
import com.mochat.mochat.model.workroom.ReqRoomAutoPullUpdateDTO;
import com.mochat.mochat.model.workroom.WorkRoomAutoPullVO;
import com.mochat.mochat.service.workroom.IWorkRoomAutoPullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author: yangpengwei
 * @time: 2020/12/16 4:16 下午
 * @description 自动拉群管理
 */
@Slf4j
@RestController
@RequestMapping("/workRoomAutoPull")
@Validated
public class WorkRoomAutoPullController {

    @Autowired
    private IWorkRoomAutoPullService workRoomAutoPullService;

    @GetMapping("/index")
    public ApiRespVO getList(
            @RequestParam(defaultValue = "") String qrcodeName,
            ReqPageDto requestPage,
            @RequestAttribute ReqPerEnum permission
    ) {
        Page<WorkRoomAutoPullVO> page = workRoomAutoPullService.getList(qrcodeName, requestPage, permission);
        return ApiRespUtils.okPage(page);
    }

    @PostMapping("/store")
    public ApiRespVO createRoomAutoPull(@Validated @RequestBody ReqRoomAutoPullCreateDTO req) {
        workRoomAutoPullService.createRoomAutoPull(req);
        return ApiRespUtils.ok("");
    }

    @PutMapping("/update")
    public ApiRespVO updateRoomAutoPullDetail(@Validated @RequestBody ReqRoomAutoPullUpdateDTO req) {
        workRoomAutoPullService.updateRoomAutoPullDetail(req);
        return ApiRespUtils.ok("");
    }

    @GetMapping("/show")
    public ApiRespVO getRoomAutoPullDetail(@NotNull(message = "workRoomAutoPullId 不能为空") Integer workRoomAutoPullId) {
        return ApiRespUtils.ok(workRoomAutoPullService.getRoomAutoPullDetail(workRoomAutoPullId));
    }
}
