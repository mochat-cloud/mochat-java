package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.WorkContactEntity;

import java.util.List;

public interface IWorkContactService extends IService<WorkContactEntity> {

    String getWXExternalUserid(int contactId);

    List<WorkContactEntity> getWorkContactsByCorpIdName(Integer corpId, String name, String clStr);

    WorkContactEntity getWorkContactsById(Integer contactId, String clStr);

    List<WorkContactEntity> getWorkContactsByCorpId(Integer corpId, String s);

    List<WorkContactEntity> getWorkContactByCorpIdWxExternalUserIds(Integer corpId, List<String> participantIdArr, String s);

    void synContactByCorpId(Integer corpId);

    boolean createAndSyncContact(int corpId, int empId, String wxEmpId, String wxContactId);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/12 5:14 下午
     * @description 增量添加客户标签
     */
    void incrementalContactTagPivot(int empId, int contactId, List<Integer> tagIdList);
}
