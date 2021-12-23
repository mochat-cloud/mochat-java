package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.em.workcontact.EventEnum;
import com.mochat.mochat.dao.entity.WorkContactEntity;
import com.mochat.mochat.model.contact.ContactDetailVO;
import com.mochat.mochat.model.contact.ContactTrackVO;
import com.mochat.mochat.model.workcontact.GetContactInfoResponse;
import com.mochat.mochat.model.workcontact.GetContactPageResponse;
import com.mochat.mochat.model.workcontact.GetContactRequest;
import com.mochat.mochat.model.workcontact.UpdateContactResponse;

import java.util.List;

public interface IContactService extends IService<WorkContactEntity> {
    /**
     * @param parem   搜索条件
     * @param empId   员工 id
     * @param corpId  企业 id
     * @param perEnum 用户权限
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/16 2:13 下午
     * @description 获取客户列表
     */
    GetContactPageResponse getContactPage(GetContactRequest parem, int empId, int corpId, ReqPerEnum perEnum);

    GetContactInfoResponse getContactInfo(Integer contactId, Integer empId, Integer corpId);

    boolean updateBusinessNo(Integer empId, Integer contactId, String businessNo);

    boolean updateContact(UpdateContactResponse parem, Integer corpId, Integer empId);

    boolean synContact(Integer corpId);

    boolean insertAllContact(List<WorkContactEntity> contacts);

    boolean insertAllContact(List<WorkContactEntity> contacts, Integer corpId);

    boolean insertContact(WorkContactEntity contact);

    JSONObject getlossContact(Integer corpId, List<Integer> empId, Integer page, Integer perPage);

    String getWxExternalUserId(int contactId);

    Integer getContactId(String wxExternalUserid);

    void addExternalContact(int corpId, String wxEmpId, String wxContactId, String welcomeCode, String state);

    void editExternalContact(String externalUserid, String userId);

    void deleteExternalContact(String externalUserid, String userId);

    void externalContactDeleteEmployee(String externalUserid, String userId);

    Integer insertWXSynContact(JSONObject contactJson, Integer corpId);

    ContactDetailVO getContactDetailByWxExternalUserId(String wxExternalUserid);

    List<ContactTrackVO> getContactTrackByContactId(Integer contactId);

    void saveTrack(Integer employeeId, Integer contactId, EventEnum eventEnum, String content);
}
