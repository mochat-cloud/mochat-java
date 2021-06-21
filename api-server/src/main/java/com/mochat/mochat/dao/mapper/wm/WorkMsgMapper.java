package com.mochat.mochat.dao.mapper.wm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.wm.WorkMsgEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2020/11/17 12:57 下午
 * @description 会话内容存储管理
 */
public interface WorkMsgMapper extends BaseMapper<WorkMsgEntity> {
    /**
     * 获取最新的 Seq
     *
     * @param tableName 表名
     * @param corpId 企业 id
     * @return seq
     */
    Integer selectLastSeq(String tableName,long corpId);

    /**
     * 保存数据
     * @param tableName 表名
     * @param list 数据集合
     */
    void insertByMap(String tableName, List<WorkMsgEntity> list);

    /**
     * 查询与会话对象相关的聊天记录信息
     */
    WorkMsgEntity selectToUserLastMsg(Map<String, Object> map);

    /**
     * 查询与会话对象相关的聊天记录信息
     * @return
     */
    List<WorkMsgEntity> selectToUserMsg(Map<String, Object> map);

    Integer selectToUserMsgCount(Map<String, Object> map);

    List<WorkMsgEntity> selectMsgByCorpId(String tableName, String corpId);
}
