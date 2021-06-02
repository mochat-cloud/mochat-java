package com.mochat.mochat.service.businesslog;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.businesslog.EventEnum;
import com.mochat.mochat.dao.entity.BusinessLogEntity;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2021/3/17 10:00 上午
 * @description 业务日志服务
 */
public interface IBusinessLogService extends IService<BusinessLogEntity> {

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 10:38 上午
     * @description 业务日志保存
     *
     * @param businessId 业务 id
     * @param param 业务参数
     * @param eventEnum 业务类型
     */
    boolean createBusinessLog(Integer businessId, Object param, EventEnum eventEnum);

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 10:44 上午
     * @description 获取本人创建或修改的业务 id 列表
     *
     * @param eventEnumList 业务类型列表
     */
    List<Integer> getBusinessIds(List<EventEnum> eventEnumList);
}
