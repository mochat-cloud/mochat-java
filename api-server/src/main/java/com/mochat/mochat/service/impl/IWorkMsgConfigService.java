package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2021/3/17 2:14 下午
 * @description 会话内容存档配置服务
 */
public interface IWorkMsgConfigService extends IService<WorkMsgConfigEntity> {

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 2:34 下午
     * @description 根据企业 id 获取企业会话内容存档配置
     */
    WorkMsgConfigEntity getByCorpId(Integer corpId);

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 2:33 下午
     * @description 获取所有启动了会话内容配置的配置列表
     */
    List<WorkMsgConfigEntity> getAllAble();

    boolean createWorkMessageConfig(WorkMsgConfigEntity workMsgConfigEntity);

    void getWorkMsgByCorpId(String corpIds, String s);

}
