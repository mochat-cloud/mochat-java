package com.mochat.mochat.dao.mapper.wm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.wm.WorkMsgIndexEntity;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/11/17 12:57 下午
 * @description 会话内容存储
 * <p>
 * 会话内容存储采用了取模分表存储, 所以表名是动态变化的
 */
public interface WorkMsgIndexMapper extends BaseMapper<WorkMsgIndexEntity> {

    /**
     * 新增 聊天索引
     */
    void insertMsgIndex(List<WorkMsgIndexEntity> list);

    Integer selectEmployeeId(int corpId, String eId);

    Integer selectContactId(int corpId, String cId);

    Integer selectRoomId(int corpId, String rId);

}
