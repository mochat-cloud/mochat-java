package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.WorkContactEntity;

import java.util.List;

public interface IWorkContactService extends IService<WorkContactEntity> {
    String getWXExternalUserid(int contactId);
    List<WorkContactEntity> getWorkContactsByCorpIdName(Integer corpId, String name,String clStr);

    WorkContactEntity getWorkContactsById(Integer contactId, String clStr);

    List<WorkContactEntity> getWorkContactsByCorpId(Integer corpId, String s);

    List<WorkContactEntity> getWorkContactByCorpIdWxExternalUserIds(Integer corpId, List<String> participantIdArr, String s);
}
