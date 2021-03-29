package com.mochat.mochat.service.contact;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.common.util.HttpClientUtil;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.WorkContactEntity;
import com.mochat.mochat.dao.entity.medium.MediumEnyity;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaojinjian
 * @ClassName SendWelcomeMsgImpl.java
 * @Description TODO
 * @createTime 2020/12/19 17:15
 */
@Slf4j
@Service
@EnableAsync
public class SendWelcomeMsgServiceImpl implements ISendWelcomeMsgService {

    private final String charset = "utf-8";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ICorpService corpService;


    @Override
    @Async
    public void send(int corpId, String welcomeCode, String leadingWords, String imageUrl) {
        String accessToken = WxApiUtils.getAccessTokenContact(corpId);
        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/send_welcome_msg?access_token=" + accessToken;
        Map<String, String> paramMap = new HashMap<>();
        JSONObject text = new JSONObject();
        text.put("content", leadingWords);
        JSONObject image = new JSONObject();
        image.put("media_id", imageUrl);
        paramMap.put("welcome_code", welcomeCode);
        paramMap.put("text", text.toJSONString());
        paramMap.put("image", image.toJSONString());
        String requestBody = JSON.toJSONString(paramMap);
        String result = HttpClientUtil.doPost(requestUrl, requestBody);
        log.debug(" >>>>>>> 自动拉群发送欢迎语: requestUrl" + requestUrl);
        log.debug(" >>>>>>> 自动拉群发送欢迎语: requestBody" + requestBody);
        log.debug(" >>>>>>> 自动拉群发送欢迎语: requestResult" + result);
    }

    @Async
    @Override
    public void send(int corpId, String welcomeCode, Map<String, String> map) {
        if (Objects.isNull(map)) {
            log.error("渠道码 - 发送欢迎语出错");
            return;
        }

        String accessToken = WxApiUtils.getAccessTokenContact(corpId);
        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/send_welcome_msg?access_token=" + accessToken;
        Map<String, String> paramMap = new HashMap<>();
        JSONObject text = new JSONObject();
        text.put("content", map.get("welcomeContent"));
        paramMap.put("text", text.toJSONString());

        String contentJson = map.get("content");
        JSONObject jsonObject = new JSONObject();
        JSONObject content = JSON.parseObject(contentJson);
        if (content.containsKey("appid")) {
            String imagePath = jsonObject.getString("imagePath");
            File imageFile = AliyunOssUtils.getFile(imagePath);
            String mediaId = WxApiUtils.uploadImageToTemp(corpId, imageFile);

            jsonObject.put("title", content.get("title"));
            jsonObject.put("pic_media_id", mediaId);
            jsonObject.put("appid", content.get("appid"));
            jsonObject.put("page", content.get("page"));
            paramMap.put("miniprogram", jsonObject.toJSONString());
        } else if (content.containsKey("imageLink")) {
            jsonObject.put("title", content.get("title"));
            jsonObject.put("picurl", content.get("imageFullPath"));
            jsonObject.put("desc", content.get("description"));
            jsonObject.put("url", content.get("imageLink"));
            paramMap.put("link", jsonObject.toJSONString());
        } else {
            String imagePath = jsonObject.getString("imagePath");
            File imageFile = AliyunOssUtils.getFile(imagePath);
            String mediaId = WxApiUtils.uploadImageToTemp(corpId, imageFile);

            jsonObject.put("media_id", mediaId);
            paramMap.put("image", jsonObject.toJSONString());
        }

        paramMap.put("welcome_code", welcomeCode);
        HttpClientUtil.doPost(requestUrl, paramMap, charset);
    }



    @Override
    public void send(String welcomeCode, Map<String,Object> map, WorkContactEntity workContactEntity) {
        Map<String,String> paramMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        //微信消息体 - 文本
        if(((Map)(map.get("content"))).get("text")!= null){
            String content = (String)map.get("text");
            String contentStr = content.replace("##客户名称##",workContactEntity.getName());
            jsonObject.put("content",contentStr);
            paramMap.put("text",jsonObject.toJSONString());
        }
        //微信消息体 - 媒体文件
        if(((Map)(map.get("content"))).get("medium") != null){
            //组织推送消息数据
            MediumEnyity mediumEnyity = (MediumEnyity)((Map)(map.get("content"))).get("medium");
            switch (mediumEnyity.getType()){
                case 2:
                    jsonObject.put("media_id",mediumEnyity.getMediaId());
                    paramMap.put("image",jsonObject.toJSONString());
                    break;
                case 3:
                    jsonObject.put("title",JSONObject.parseObject(mediumEnyity.getContent()).get("title"));
                    jsonObject.put("picurl",JSONObject.parseObject(mediumEnyity.getContent()).get("imagePath"));
                    jsonObject.put("desc",JSONObject.parseObject(mediumEnyity.getContent()).get("description"));
                    jsonObject.put("url",JSONObject.parseObject(mediumEnyity.getContent()).get("imageLink"));
                    paramMap.put("link",jsonObject.toJSONString());
                    break;
                case 6:
                    jsonObject.put("title",JSONObject.parseObject(mediumEnyity.getContent()).get("title"));
                    jsonObject.put("pic_media_id",mediumEnyity.getMediaId());
                    jsonObject.put("appid",JSONObject.parseObject(mediumEnyity.getContent()).get("description"));
                    jsonObject.put("page",JSONObject.parseObject(mediumEnyity.getContent()).get("imageLink"));
                    paramMap.put("miniprogram",jsonObject.toJSONString());
                    break;
            }
        }

//        String accessToken = WxApiUtils.getAccessTokenContact(corpId);
//        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/send_welcome_msg?access_token=" + accessToken;
        Integer corpId = AccountService.getCorpId();
        CorpEntity corpEntity = corpService.getCorpInfoById(corpId);
        Object mapValue = redisTemplate.opsForHash().get(corpEntity.getWxCorpId(), "acccess_token");
        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/send_welcome_msg?access_token=" + mapValue.toString();
        paramMap.put("welcome_code", welcomeCode);
        HttpClientUtil.doPost(requestUrl, paramMap, charset);
    }
}
