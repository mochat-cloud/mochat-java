package com.mochat.mochat.service;

import com.mochat.mochat.common.em.workupdatetime.TypeEnum;

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
}
