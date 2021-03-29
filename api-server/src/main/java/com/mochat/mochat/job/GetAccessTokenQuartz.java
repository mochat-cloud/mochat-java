package com.mochat.mochat.job;


import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.common.util.HttpClientUtil;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.ICorpService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync
public class GetAccessTokenQuartz {
    private final static Logger logger = LoggerFactory.getLogger(GetAccessTokenQuartz.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ICorpService corpService;


    //刷新access_token 100分钟刷新一次,服务器启动的时候刷新一次（access_token有效期是120分钟，我设置的是每100分钟刷新一次）
    //@Async
    //@Scheduled(initialDelay = 1000, fixedDelay = 100*60*1000)
    public void  get_access_token() {
        Integer corpId = AccountService.getCorpId();
        CorpEntity corpEntity = corpService.getCorpInfoById(corpId);
        String appid = corpEntity.getWxCorpId();
        String appsecret =  corpEntity.getEmployeeSecret();
        String requestUrl = Const.URL_REQUEST_ADDRESS+"/gettoken?corpid=APPID&corpsecret=APPSECRET";
        requestUrl = requestUrl.replace("APPID", appid).replace("APPSECRET", appsecret);
        Map<String,String> createMap = new HashMap<String,String>();
        createMap.put("appid",appid);
        createMap.put("appsecret",appsecret);
        JSONObject jobj1=new JSONObject();
        String charset = "utf-8";
        JSONObject jsonObject = new JSONObject(HttpClientUtil.doPost(requestUrl,createMap, charset));
        //JSONObject jsonObject = jobj1.(HttpClientUtil.doPost(requestUrl,createMap,charset));
        if (jsonObject.getString("access_token") != null) {

            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("access_token", jsonObject.getString("access_token"));
                redisTemplate.opsForHash().putAll(appid,map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("定时刷新access_token失败，微信返回的信息是" + jsonObject.toString());
        }
    }

}

