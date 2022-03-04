package com.mochat.mochat.service.contact;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface IExternalContactService {
    void updateRemark(Integer empId,Integer corpId,Integer contactId,String remark,String description);
    JSONArray getExternalUserId(String userId,Integer corpId);
    JSONArray getAllExternalUserId(List<String> userIds,Integer corpId);
    JSONObject getExternalContact(String externalUserId,Integer corpId);
    Map<String, JSONObject> getExternalContactMap(List<Object> array,Integer corpId);
    void uploadContactAvatar(Map<String,String> filePathMap);
}
