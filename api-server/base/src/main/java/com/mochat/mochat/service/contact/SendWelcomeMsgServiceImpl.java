package com.mochat.mochat.service.contact;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.common.util.HttpClientUtil;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaojinjian
 * @ClassName SendWelcomeMsgImpl.java
 * @Description 发送欢迎语
 * @createTime 2020/12/19 17:15
 */
@Slf4j
@Service
@EnableAsync
public class SendWelcomeMsgServiceImpl implements ISendWelcomeMsgService {

    @Override
    public void sendMsg(int corpId, String welcomeCode, String leadingWords) {
        String accessToken = WxApiUtils.getAccessTokenContact(corpId);
        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/send_welcome_msg?access_token=" + accessToken;

        JSONObject requestBody = new JSONObject();
        requestBody.put("welcome_code", welcomeCode);

        JSONObject text = new JSONObject();
        text.put("content", leadingWords);
        requestBody.put("text", text);
        String requestBodyStr = requestBody.toJSONString();

        String result = HttpClientUtil.doPost(requestUrl, requestBodyStr);
        log.debug(" >>>>>>> 发送欢迎语: requestUrl" + requestUrl);
        log.debug(" >>>>>>> 发送欢迎语: requestBody" + requestBodyStr);
        log.debug(" >>>>>>> 发送欢迎语: requestResult" + result);
    }

    @Override
    @Async
    public void sendMsgOfRoomAutoPull(int corpId, String welcomeCode, String leadingWords, String imageUrl) {
        String accessToken = WxApiUtils.getAccessTokenContact(corpId);
        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/send_welcome_msg?access_token=" + accessToken;

        JSONObject requestBody = new JSONObject();
        requestBody.put("welcome_code", welcomeCode);

        JSONObject text = new JSONObject();
        text.put("content", leadingWords);
        requestBody.put("text", text);

        JSONObject image = new JSONObject();
        image.put("media_id", imageUrl);

        JSONObject attachment = new JSONObject();
        attachment.put("msgtype", "image");
        attachment.put("image", image);

        List<JSONObject> attachments = new ArrayList<>();
        attachments.add(attachment);

        requestBody.put("attachments", attachments);

        String requestBodyStr = requestBody.toJSONString();

        String result = HttpClientUtil.doPost(requestUrl, requestBodyStr);
        log.debug(" >>>>>>> 发送欢迎语: requestUrl" + requestUrl);
        log.debug(" >>>>>>> 发送欢迎语: requestBody" + requestBodyStr);
        log.debug(" >>>>>>> 发送欢迎语: requestResult" + result);
    }

    @Async
    @Override
    public void sendMsgOfChannelCode(int corpId, String welcomeCode, Map<String, String> map) {
        if (Objects.isNull(map)) {
            log.error("渠道码 - 发送欢迎语出错");
            return;
        }

        String accessToken = WxApiUtils.getAccessTokenContact(corpId);
        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/send_welcome_msg?access_token=" + accessToken;

        JSONObject requestBody = new JSONObject();
        requestBody.put("welcome_code", welcomeCode);

        String contactName = map.get("contactName");
        String leadingWords = map.get("welcomeContent").replaceAll("##客户名称##", contactName);
        JSONObject text = new JSONObject();
        text.put("content", leadingWords);
        requestBody.put("text", text);

        String contentJson = map.get("content");
        JSONObject content = JSON.parseObject(contentJson);
        if (null != content) {
            JSONObject attachment = new JSONObject();
            JSONObject jsonObject = new JSONObject();

            if (content.containsKey("appid")) {
                String imagePath = content.getString("imagePath");
                File imageFile = AliyunOssUtils.getFile(imagePath);
                String mediaId = WxApiUtils.uploadImageToTemp(corpId, imageFile);

                jsonObject.put("title", content.get("title"));
                jsonObject.put("pic_media_id", mediaId);
                jsonObject.put("appid", content.get("appid"));
                jsonObject.put("page", content.get("page"));

                attachment.put("msgtype", "miniprogram");
                attachment.put("miniprogram", jsonObject);
            } else if (content.containsKey("imageLink")) {
                jsonObject.put("title", content.get("title"));
                jsonObject.put("picurl", AliyunOssUtils.getUrl(content.getString("imagePath")));
                jsonObject.put("desc", content.get("description"));
                jsonObject.put("url", content.get("imageLink"));

                attachment.put("msgtype", "link");
                attachment.put("link", jsonObject);
            } else {
                String imagePath = content.getString("imagePath");
                File imageFile = AliyunOssUtils.getFile(imagePath);
                String mediaId = WxApiUtils.uploadImageToTemp(corpId, imageFile);

                jsonObject.put("media_id", mediaId);

                attachment.put("msgtype", "image");
                attachment.put("image", jsonObject);
            }

            List<JSONObject> attachments = new ArrayList<>();
            attachments.add(attachment);

            requestBody.put("attachments", attachments);
        }

        String requestBodyStr = requestBody.toJSONString();

        String result = HttpClientUtil.doPost(requestUrl, requestBodyStr);
        log.debug(" >>>>>>> 发送欢迎语: requestUrl" + requestUrl);
        log.debug(" >>>>>>> 发送欢迎语: requestBody" + requestBodyStr);
        log.debug(" >>>>>>> 发送欢迎语: requestResult" + result);
    }

}
