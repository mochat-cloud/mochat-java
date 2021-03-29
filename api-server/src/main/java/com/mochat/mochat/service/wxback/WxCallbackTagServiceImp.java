package com.mochat.mochat.service.wxback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.service.contact.IWorkContactTagGroupService;
import com.mochat.mochat.service.impl.IWorkContactTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WxCallbackTagServiceImp implements IWxCallbackTagService {

    @Autowired
    private IWorkContactTagService workContactTagService;
    @Autowired
    private IWorkContactTagGroupService workContactTagGroupService;

    @Override
    public String dispatchEvent(String dataJson) {
        JSONObject jsonObject = JSON.parseObject(dataJson);
        int corpId = jsonObject.getIntValue("corpId");
        JSONObject xmlJsonObject = jsonObject.getJSONObject("xml");
        String changeType = xmlJsonObject.getString("changetype");
        String tagType = xmlJsonObject.getString("tagtype");
        String wxId = xmlJsonObject.getString("id");

        if (CHANGE_TYPE_CONTACT_TAG_CREATE.equals(changeType)) {
            if ("tag".equals(tagType)) {
                workContactTagService.wxBackCreateTag(corpId, wxId);
            } else {
                workContactTagGroupService.wxBackCreateTagGroup(corpId, wxId);
            }
            return "";
        }
        if (CHANGE_TYPE_CONTACT_TAG_UPDATE.equals(changeType)) {
            if ("tag".equals(tagType)) {
                workContactTagService.wxBackUpdateTag(corpId, wxId);
            } else {
                workContactTagGroupService.wxBackUpdateTagGroup(corpId, wxId);
            }
            return "";
        }
        if (CHANGE_TYPE_CONTACT_TAG_DELETE.equals(changeType)) {
            if ("tag".equals(tagType)) {
                workContactTagService.wxBackDeleteTag(corpId, wxId);
            } else {
                workContactTagGroupService.wxBackDeleteTagGroup(corpId, wxId);
            }
            return "";
        }

        return "";
    }
}
