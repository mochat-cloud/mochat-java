package com.mochat.mochat.service;

import com.mochat.mochat.dao.entity.CorpDataEntity;

import java.util.Date;
import java.util.List;

public interface ICorpDataService {
    void handle();

    CorpDataEntity getCorpDayDataByCorpIdDate(Integer corpId, Date time);

    List<CorpDataEntity> getCorpDayDatasByCorpIdTime(Integer corpId, Date beginDate, Date endDate);

    List<CorpDataEntity> getCorpDayDatasByCorpIdDateOther(Date startDate, Date endDate, String cls);
}
