package com.mochat.mochat.dao.mapper.wm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/11/17 12:54 下午
 * @description 用于查询所有配置好企业微信会话内容存档的数据
 */
public interface WorkMsgConfigMapper extends BaseMapper<WorkMsgConfigEntity> {

    /**
     * @author: yangpengwei
     * @time: 2020/11/17 12:56 下午
     * @description 获取所有可以进行会话内容存档的企业微信信息
     * @return 所有可以进行会话内容存档的企业微信信息
     */
    List<WorkMsgConfigEntity> selectAll();

    /**
     * @author: yangpengwei
     * @time: 2020/11/25 9:00 下午
     * @description 获取企业微信会话内容存档配置信息
     * @param corpId 企业 id
     * @return 企业微信会话内容存档配置信息
     */
    WorkMsgConfigEntity selectByCorpId(int corpId);

}
