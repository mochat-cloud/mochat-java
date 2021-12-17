package com.mochat.mochat.controller.wm;

import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.wm.ReqMsgIndexDTO;
import com.mochat.mochat.model.wm.ReqToUsersDTO;
import com.mochat.mochat.service.wm.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yangpengwei
 * @time: 2020/12/2 11:05 上午
 * @description 会话内容存档
 */
@RestController()
@RequestMapping(path = "/workMessage")
public class ChatServiceController {

    @Autowired
    private IChatService chatService;

    /**
     * 会话内容存档 - 会话员工下拉
     *
     * @param name   搜索名称 [可选]
     */
    @GetMapping("/fromUsers")
    public ApiRespVO fromUsers(@RequestParam(required = false) String name) {
        return ApiRespUtils.ok(chatService.getFromUserInfoList(name));
    }

    /**
     * 会话内容存档 - 会话对象列表
     */
    @GetMapping("/toUsers")
    public ApiRespVO toUsers(ReqToUsersDTO req) {
        return ApiRespUtils.getApiRespByPage(chatService.getToUserInfoList(req));
    }

    /**
     * 会话内容存档 - 列表
     */
    @GetMapping("/index")
    public ApiRespVO chatList(ReqMsgIndexDTO req) {
        return ApiRespUtils.getApiRespByPage(chatService.index(req));
    }

}
