package com.mochat.mochat.service.wm;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.model.wm.*;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 5:45 下午
 * @description 运营-聊天记录业务实现
 */
public interface IChatService {

    /**
     * 会话内容存档 - 会话员工下拉
     * 获取对应公司员工简略信息
     *
     * @param name   员工名
     * @return 员工信息集合
     */
    List<FromUserInfoBO> getFromUserInfoList(String name);

    /**
     * 会话内容存档 - 会话对象列表
     *
     * @return Page 分页对象, 内包含聊天对象信息集合
     */
    Page<ToUserInfoBO> getToUserInfoList(ReqToUsersDTO req);

    /**
     * 会话内容存档 - 列表
     *
     * @return 聊天记录集合
     */
    Page<IndexMsgBO> index(ReqMsgIndexDTO req);

}
