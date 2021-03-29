package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.dao.entity.ContactFieldEntity;
import com.mochat.mochat.model.contactfield.AddContactFieldModel;
import com.mochat.mochat.model.contactfield.BatchUpdateContactFieldModel;
import com.mochat.mochat.model.contactfield.UpdateContactFieldModel;

import java.util.List;

public interface IContactFieldService {
    JSONArray getPortrait(Integer fieldId, String name);
    List<ContactFieldEntity> getContactFieldList(List<Integer> fieidIds);
    JSONObject getContactFieldInfo(Integer fieldId);
    JSONObject getContactFieldList(Integer status,Integer page,Integer perPage);
    boolean deleteContactField(Integer fieldId);
    boolean updateStatus(Integer fieldId,Integer status);
    boolean BatchUpdateContactField(BatchUpdateContactFieldModel parem);
    boolean updateContactField(UpdateContactFieldModel parem);
    boolean updateMultipleContactField(List<ContactFieldEntity> list);
    boolean insertContactField(AddContactFieldModel parem);
}
