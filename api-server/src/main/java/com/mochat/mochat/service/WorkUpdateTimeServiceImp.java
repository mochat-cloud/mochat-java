package com.mochat.mochat.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mochat.mochat.common.em.workupdatetime.TypeEnum;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.dao.entity.WorkUpdateTimeEntity;
import com.mochat.mochat.dao.mapper.WorkUpdateTimeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class WorkUpdateTimeServiceImp implements IWorkUpdateTimeService {

    @Autowired
    private WorkUpdateTimeMapper workUpdateTimeMapper;

    /**
     * 更新业务最后一次同步时间
     *
     * @param typeEnum 业务枚举类
     */
    @Override
    public void updateSynTime(Integer corpId, TypeEnum typeEnum) {
        if (corpId == null) {
            return;
        }

        // 更新 mc_work_update_time
        WorkUpdateTimeEntity entity = new WorkUpdateTimeEntity();
        entity.setCorpId(corpId);
        entity.setType(typeEnum.getCode());
        entity = workUpdateTimeMapper.selectOne(new QueryWrapper<>(entity));
        if (entity == null) {
            entity = new WorkUpdateTimeEntity();
            entity.setCorpId(corpId);
            entity.setType(typeEnum.getCode());
            entity.setLastUpdateTime(new Date());
            workUpdateTimeMapper.insert(entity);
        } else {
            entity.setLastUpdateTime(new Date());
            workUpdateTimeMapper.updateById(entity);
        }

    }

    /**
     * 获取业务最后一次同步时间
     *
     * @param typeEnum 业务枚举类
     * @return
     */
    @Override
    public String getLastUpdateTime(TypeEnum typeEnum) {
        int corpId = AccountService.getCorpId();

        // 更新 mc_work_update_time
        WorkUpdateTimeEntity entity = new WorkUpdateTimeEntity();
        entity.setCorpId(corpId);
        entity.setType(typeEnum.getCode());
        entity = workUpdateTimeMapper.selectOne(new QueryWrapper<>(entity));
        if (entity == null) {
            return "";
        }
        return DateUtils.formatS1(entity.getLastUpdateTime().getTime());
    }
}
