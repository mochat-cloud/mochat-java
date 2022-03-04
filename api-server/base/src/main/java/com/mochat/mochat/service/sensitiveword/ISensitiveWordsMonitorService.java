package com.mochat.mochat.service.sensitiveword;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordsMonitorEntity;
import com.mochat.mochat.dao.model.ReqSensitiveWordsMonitorIndex;

import java.util.List;
import java.util.Map;

public interface ISensitiveWordsMonitorService {

    List<SensitiveWordsMonitorEntity> handle(ReqSensitiveWordsMonitorIndex reqSensitiveWordsMonitorIndex);

    SensitiveWordsMonitorEntity getSensitiveWordMonitorById(String sensitiveWordsMonitorId);

    Map<String,Object> contentFormat(JSONObject jsonObject1);

    void createSensitiveWordMonitors(Map map);
}
