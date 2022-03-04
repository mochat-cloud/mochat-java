package com.mochat.mochat.service;

import com.mochat.mochat.common.em.workupdatetime.TypeEnum;
import com.mochat.mochat.dao.entity.WorkUpdateTimeEntity;

public interface IWorkUpdateTimeService {

    /**
     * 更新业务最后一次同步时间
     *
     * @param typeEnum 业务枚举类
     */
    void updateSynTime(Integer corpId, TypeEnum typeEnum);

    /**
     * 获取业务最后一次同步时间
     *
     * @param typeEnum 业务枚举类
     * @return
     */
    String getLastUpdateTime(TypeEnum typeEnum);

    WorkUpdateTimeEntity getWorkUpdateTimeByCorpIdType(Integer corpId, int code);

    Integer updateWorkUpdateTimeById(Integer id, WorkUpdateTimeEntity workUpdateTimeEntity);

    Integer createWorkUpdateTime(WorkUpdateTimeEntity workUpdateTimeEntity1);
}
