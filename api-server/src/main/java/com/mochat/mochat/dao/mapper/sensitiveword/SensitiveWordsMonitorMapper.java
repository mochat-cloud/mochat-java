package com.mochat.mochat.dao.mapper.sensitiveword;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordsMonitorEntity;
import com.mochat.mochat.model.sensitiveword.ReqSensitiveWordsMonitorIndex;

import java.util.List;

public interface SensitiveWordsMonitorMapper extends BaseMapper<SensitiveWordsMonitorEntity> {
    List<SensitiveWordsMonitorEntity> getSensitiveWordMonitorList(ReqSensitiveWordsMonitorIndex reqSensitiveWordsMonitorIndex);
}
