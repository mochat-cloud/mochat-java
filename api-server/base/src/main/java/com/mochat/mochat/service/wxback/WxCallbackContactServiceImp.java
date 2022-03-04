package com.mochat.mochat.service.wxback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.service.impl.IContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class WxCallbackContactServiceImp implements IWxCallbackContactService {

    @Autowired
    private IContactService contactService;

    @Override
    public String dispatchEvent(String dataJson) {
        JSONObject jsonObject = JSON.parseObject(dataJson);
        int corpId = jsonObject.getIntValue("corpId");
        JSONObject xmlJsonObject = jsonObject.getJSONObject("xml");
        String changeType = xmlJsonObject.getString("changetype");
        String wxCorpId = xmlJsonObject.getString("tousername");

        //客户回调
        if (ADD_EXTERNAL_CONTACT.equals(changeType)) {
            contactService.addExternalContact(
                    corpId,
                    xmlJsonObject.getString("userid"),
                    xmlJsonObject.getString("externaluserid"),
                    xmlJsonObject.getString("welcomecode"),
                    xmlJsonObject.getString("state")
            );
        }

        if (EDIT_EXTERNAL_CONTACT.equals(changeType)) {
            contactService.editExternalContact(xmlJsonObject.getString("externaluserid"), xmlJsonObject.getString("userid"));
        }
        if (DEL_EXTERNAL_CONTACT.equals(changeType)) {
            contactService.deleteExternalContact(xmlJsonObject.getString("externaluserid"), xmlJsonObject.getString("userid"));
        }

        if (DEL_FOLLOW_USER.equals(changeType)) {
            contactService.externalContactDeleteEmployee(xmlJsonObject.getString("externaluserid"), xmlJsonObject.getString("userid"));
        }
        return "";
    }

}
