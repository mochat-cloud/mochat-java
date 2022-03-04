package com.mochat.mochat.service.sidebar;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.WorkAgentEntity;

import java.util.List;
import java.util.Map;

public interface IWorkAgentService extends IService<WorkAgentEntity> {

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/19 4:35 下午
     * @description 根据应用 id 获取企业 id
     */
    Integer getCorpIdById(Integer agentId);

    int getFirstAgentId(Integer corpId);

    String getWxAgentById(Integer agentId);

    void storeAgent(String wxAgentId, String wxSecret, int type);

    List<Map<String, Object>> getChatTools();
}
