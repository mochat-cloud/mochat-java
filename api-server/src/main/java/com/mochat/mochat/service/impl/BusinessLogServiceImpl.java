package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.businesslog.EventEnum;
import com.mochat.mochat.dao.entity.BusinessLogEntity;
import com.mochat.mochat.dao.mapper.BusinessLogMapper;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.businessLog.IBusinessLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:业务日志服务实现类
 * @author: Huayu
 * @time: 2021/1/28 11:25
 */
@Service
public class BusinessLogServiceImpl extends ServiceImpl<BusinessLogMapper, BusinessLogEntity> implements IBusinessLogService {

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 10:38 上午
     * @description 业务日志保存
     *
     * @param businessId 业务 id
     * @param paramJson 业务参数
     * @param eventEnum 业务类型
     */
    @Override
    public boolean createBusinessLog(Integer businessId, String paramJson, EventEnum eventEnum) {
        BusinessLogEntity entity = new BusinessLogEntity();
        entity.setBusinessId(businessId);
        entity.setParams(paramJson);
        entity.setEvent(eventEnum.getCode());
        entity.setOperationId(AccountService.getEmpId());
        return entity.insert();
    }

    /**
     * @param eventEnumList 业务类型列表
     * @author: yangpengwei
     * @time: 2021/3/17 10:44 上午
     * @description 获取本人创建或修改的业务 id 列表
     */
    @Override
    public List<Integer> getBusinessIds(List<EventEnum> eventEnumList) {
        List<Integer> eventCodeList = eventEnumList.stream()
                .map(EventEnum::getCode)
                .collect(Collectors.toList());

        List<Integer> businessIdList = lambdaQuery()
                .select(BusinessLogEntity::getBusinessId)
                .eq(BusinessLogEntity::getOperationId, AccountService.getEmpId())
                .in(BusinessLogEntity::getEvent, eventCodeList)
                .list()
                .stream()
                .map(BusinessLogEntity::getBusinessId)
                .collect(Collectors.toList());

        return businessIdList;
    }
}
