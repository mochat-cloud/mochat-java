package com.mochat.mochat.service.wm;

import com.mochat.mochat.dao.entity.wm.WorkMsgEntity;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 5:11 下午
 * @description 会话内容存档业务
 */
public interface IWorkMsgService {

    /**
     * 对所有开启会话内容存档的企业进行数据同步
     */
    void onAsyncMsg();

    /**
     * 同步会话内容数据
     *
     * @param corpId 企业 id
     * @return 是否同步成功
     */
    boolean onAsyncMsg(int corpId);

    List<WorkMsgEntity> getWorkMessagesByMsgId(List<WorkMsgEntity> value);

    List<WorkMsgEntity> getWorkMessagesRangeByCorpIdWxRoomId(String corpId, String wxRoomId, Integer id, int i, String s);

    List<WorkMsgEntity> getWorkMessagesRangeByCorpId(String corpId, String from, String tolist, Integer id, int i, String s);
}
