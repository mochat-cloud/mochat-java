package com.mochat.mochat.service.contact;

import com.mochat.mochat.dao.entity.WorkContactEntity;

import java.util.Map;

public interface ISendWelcomeMsgService {

    void send(int corpId, String welcomeCode, String leadingWords, String imageUrl);

    void send(int corpId, String welcomeCode, Map<String, String> map);

    void send(String welcomeCode, Map<String,Object> map, WorkContactEntity workContactEntity);
}
