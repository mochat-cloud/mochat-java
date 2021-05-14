package com.mochat.mochat.service.greeting;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.dao.entity.greeting.GreetingEntity;

import java.util.List;
import java.util.Map;

public interface IGreetingService {


    Map<String, Object> handle(RequestPage page, ReqPerEnum permission);

    Integer createGreeting(GreetingEntity greetingEntity);

    Integer updateGreetingById(String greetingId, Map<String, Object> mapData);

    GreetingEntity getGreetingById(Integer greetingId, String clStr);

    Integer deleteGreeting(Integer greetingId);

    List<GreetingEntity> getGreetingsByCorpId(Integer corpId, String s);

    Map<String, Object> getGreeting(String userId);

    void applyWxSendContactMessage(int corpId, String welcomeCode, Map<String, JSONObject> contactInfo, Map<String, Object> content);
}
