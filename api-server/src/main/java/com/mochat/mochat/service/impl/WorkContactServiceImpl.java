package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.util.FileUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.emp.DownUploadQueueUtils;
import com.mochat.mochat.dao.entity.*;
import com.mochat.mochat.dao.mapper.ContactMapper;
import com.mochat.mochat.job.sync.WorkContactTagSyncLogic;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaojinjian
 * @ClassName WorkContactServiceImpl.java
 * @Description TODO
 * @createTime 2020/12/7 17:56
 */
@Service
public class WorkContactServiceImpl extends ServiceImpl<ContactMapper, WorkContactEntity> implements IWorkContactService {

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private IWorkContactEmployeeService contactEmployeeService;

    @Autowired
    private IWorkContactTagService contactTagService;

    @Autowired
    private WorkContactTagSyncLogic contactTagSyncLogic;

    @Autowired
    private IWorkContactTagPivotService contactTagPivotService;

    @Override
    public String getWXExternalUserid(int contactId) {
        WorkContactEntity workContactEntity = this.baseMapper.selectById(contactId);
        if (workContactEntity != null) {
            return workContactEntity.getWxExternalUserid();
        }
        return "";
    }

    /**
     * @description:企业外部联系人模糊匹配
     * @return:
     * @author: Huayu
     * @time: 2020/12/16 14:21
     */
    @Override
    public List<WorkContactEntity> getWorkContactsByCorpIdName(Integer corpId, String name, String clStr) {
        QueryWrapper<WorkContactEntity> workContactQueryWrapper = new QueryWrapper<WorkContactEntity>();
        workContactQueryWrapper.select(clStr);
        workContactQueryWrapper.eq("corp_id", corpId);
        workContactQueryWrapper.like("avatar", name);
        return this.baseMapper.selectList(workContactQueryWrapper);
    }

    @Override
    public WorkContactEntity getWorkContactsById(Integer contactId, String clStr) {
        QueryWrapper<WorkContactEntity> workContactQueryWrapper = new QueryWrapper<WorkContactEntity>();
        if (!clStr.equals("") || clStr != null) {
            workContactQueryWrapper.select(clStr);
        }
        return this.baseMapper.selectById(contactId);
    }

    @Override
    public List<WorkContactEntity> getWorkContactsByCorpId(Integer corpId, String clStr) {
        QueryWrapper<WorkContactEntity> workContactQueryWrapper = new QueryWrapper<WorkContactEntity>();
        workContactQueryWrapper.select(clStr);
        workContactQueryWrapper.eq("corp_id", corpId);
        return this.baseMapper.selectList(workContactQueryWrapper);
    }

    @Override
    public List<WorkContactEntity> getWorkContactByCorpIdWxExternalUserIds(Integer corpId, List<String> participantIdArr, String s) {
        StringBuilder sb = new StringBuilder();
        for (String str :
                participantIdArr) {
            sb.append(str).append(",");
        }
        String participantIdStr = sb.substring(0, sb.length() - 1);
        QueryWrapper<WorkContactEntity> workContactQueryWrapper = new QueryWrapper<WorkContactEntity>();
        workContactQueryWrapper.select(s);
        workContactQueryWrapper.in("wx_external_userid", participantIdStr);
        workContactQueryWrapper.eq("corp_id", corpId);
        return this.baseMapper.selectList(workContactQueryWrapper);
    }

    /**
     * @description 同步客户
     */
    @Override
    public void synContactByCorpId(Integer corpId) {
        // 查询所有拥有配置外部联系人权限的成员
        List<WorkEmployeeEntity> empList = employeeService.lambdaQuery()
                .select(WorkEmployeeEntity::getId, WorkEmployeeEntity::getWxUserId)
                .eq(WorkEmployeeEntity::getCorpId, corpId)
                .eq(WorkEmployeeEntity::getContactAuth, 1)
                .list();

        // 根据成员微信 id 查询出相关客户
        if (!empList.isEmpty()) {
            for (WorkEmployeeEntity entity : empList) {
                Integer id = entity.getId();
                String wxUserId = entity.getWxUserId();

                List<String> wxContactIdList = new ArrayList<>();
                String result = WxApiUtils.getExternalContactList(corpId, wxUserId);
                JSONObject jsonObject = JSON.parseObject(result);
                if (null != jsonObject && jsonObject.get("errcode").equals(0)) {
                    wxContactIdList.addAll(jsonObject.getJSONArray("external_userid").toJavaList(String.class));
                }
                syncContactLogic(corpId, id, wxUserId, wxContactIdList);
            }
        }
    }

    private void syncContactLogic(Integer corpId, Integer empId, String wxEmpId, List<String> wxContactIdList) {
        // 查出最新数据对应客户 id (更新数据)
        if (wxContactIdList.isEmpty()) {
            // 查询员工对应的不再此次数据源中的客户 (删除数据)
            List<WorkContactEmployeeEntity> removeContactEmpList = contactEmployeeService.lambdaQuery()
                    .select(WorkContactEmployeeEntity::getId, WorkContactEmployeeEntity::getContactId)
                    .eq(WorkContactEmployeeEntity::getEmployeeId, empId)
                    .list();
            deleteContactEmpData(empId, removeContactEmpList);
            return;
        }

        List<WorkContactEntity> cList = lambdaQuery()
                .select(WorkContactEntity::getId, WorkContactEntity::getWxExternalUserid)
                .in(WorkContactEntity::getWxExternalUserid, wxContactIdList)
                .list();

        if (!cList.isEmpty()) {
            List<Integer> cIdList = cList.stream()
                    .map(WorkContactEntity::getId)
                    .collect(Collectors.toList());

            // 更新客户数据
            for (Integer cId : cIdList) {
                updateContact(corpId, empId, wxEmpId, cId);
            }

            // 查询员工对应的不再此次数据源中的客户 (删除数据)
            List<WorkContactEmployeeEntity> removeContactEmpList = contactEmployeeService.lambdaQuery()
                    .select(WorkContactEmployeeEntity::getId, WorkContactEmployeeEntity::getContactId)
                    .eq(WorkContactEmployeeEntity::getEmployeeId, empId)
                    .notIn(WorkContactEmployeeEntity::getContactId, cIdList)
                    .list();
            deleteContactEmpData(empId, removeContactEmpList);
        }

        // 添加此次新增客户 (新增数据)
        List<String> wxCIdList = cList.stream()
                .map(WorkContactEntity::getWxExternalUserid)
                .collect(Collectors.toList());
        // 删除已存在客户
        wxContactIdList.removeAll(wxCIdList);

        // 添加此次新增客户
        for (String wxContactId : wxContactIdList) {
            createAndSyncContact(corpId, empId, wxEmpId, wxContactId);
        }
    }

    private void deleteContactEmpData(int empId, List<WorkContactEmployeeEntity> removeContactEmpList) {
        // 删除员工与客户关联数据
        List<Integer> removeCEIdList = removeContactEmpList
                .stream()
                .map(WorkContactEmployeeEntity::getId)
                .collect(Collectors.toList());
        contactEmployeeService.removeByIds(removeCEIdList);

        // 需要删除的客户 Id
        List<Integer> removeCIdList = removeContactEmpList
                .stream()
                .map(WorkContactEmployeeEntity::getContactId)
                .collect(Collectors.toList());

        if (removeCIdList.isEmpty()) {
            return;
        }

        // 客户与标签
        List<Integer> contactTagIdList = contactTagPivotService.lambdaQuery()
                .select(WorkContactTagPivotEntity::getId)
                .in(WorkContactTagPivotEntity::getContactId, removeCIdList)
                .eq(WorkContactTagPivotEntity::getEmployeeId, empId)
                .list()
                .stream()
                .map(WorkContactTagPivotEntity::getId)
                .collect(Collectors.toList());
        contactTagPivotService.removeByIds(contactTagIdList);
    }

    private void updateContact(int corpId, int empId, String wxEmpId, int contactId) {
        WorkContactEntity entity = getById(contactId);
        updateContactLogic(corpId, empId, wxEmpId, entity.getWxExternalUserid(), entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateContactLogic(int corpId, int empId, String wxEmpId, String wxContactId, WorkContactEntity contactEntity) {
        String result = WxApiUtils.getExternalContactInfo(corpId, wxContactId);
        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && jsonObject.get("errcode").equals(0)) {
            JSONObject contactJson = jsonObject.getJSONObject("external_contact");

            String avatarFileName = FileUtils.getContactAvatarPath();
            String avatarUrl = contactJson.getString("avatar");
            DownUploadQueueUtils.uploadFileByUrl(avatarFileName, avatarUrl);

            contactEntity.setCorpId(corpId);
            contactEntity.setWxExternalUserid(wxContactId);
            contactEntity.setName(contactJson.getString("name"));
            contactEntity.setNickName("");
            contactEntity.setAvatar(avatarFileName);
            contactEntity.setFollowUpStatus(contactEntity.getFollowUpStatus());
            contactEntity.setType(contactJson.getInteger("type"));
            contactEntity.setGender(contactJson.getInteger("gender"));
            contactEntity.setUnionid(contactJson.getString("unionid"));
            contactEntity.setPosition(contactJson.getString("position"));
            contactEntity.setCorpName(contactJson.getString("corp_name"));
            contactEntity.setCorpFullName(contactJson.getString("corp_full_name"));
            contactEntity.setExternalProfile(contactJson.getString("external_profile"));
            contactEntity.setBusinessNo(contactEntity.getBusinessNo());
            saveOrUpdate(contactEntity);

            JSONArray followUser = jsonObject.getJSONArray("follow_user");
            if (followUser.size() > 0) {
                WorkContactEmployeeEntity contactEmployeeEntity = contactEmployeeService.lambdaQuery()
                        .eq(WorkContactEmployeeEntity::getEmployeeId, empId)
                        .eq(WorkContactEmployeeEntity::getContactId, contactEntity.getId())
                        .one();
                if (contactEmployeeEntity == null) {
                    contactEmployeeEntity = new WorkContactEmployeeEntity();
                }

                for (int i = 0; i < followUser.size(); i++) {
                    JSONObject followUserItem = followUser.getJSONObject(i);
                    String wxUserId = followUserItem.getString("userid");
                    if (wxEmpId.equals(wxUserId)) {
                        contactEmployeeEntity.setRemark(followUserItem.getString("remark"));
                        contactEmployeeEntity.setDescription(followUserItem.getString("description"));
                        contactEmployeeEntity.setCreateTime(new Date(followUserItem.getLong("createtime")));
                        contactEmployeeEntity.setRemarkCorpName(followUserItem.getString("remark_corp_name"));
                        contactEmployeeEntity.setRemarkMobiles(followUserItem.getString("remark_mobiles"));
                        contactEmployeeEntity.setAddWay(followUserItem.getInteger("add_way"));
                        contactEmployeeEntity.setOperUserid(followUserItem.getString("oper_userid"));
                        contactEmployeeEntity.setState(followUserItem.getString("state"));
                        contactEmployeeEntity.setEmployeeId(empId);
                        contactEmployeeEntity.setContactId(contactEntity.getId());
                        contactEmployeeEntity.setCorpId(corpId);
                        contactEmployeeEntity.setStatus(contactEmployeeEntity.getStatus());
                        contactEmployeeService.saveOrUpdate(contactEmployeeEntity);

                        JSONArray tags = followUserItem.getJSONArray("tags");
                        List<String> wxTagIdList = new ArrayList<>();
                        for (int l = 0; l < tags.size(); l++) {
                            JSONObject tag = tags.getJSONObject(l);
                            String wxTagId = tag.getString("tag_id");
                            if (wxTagId == null || wxTagId.isEmpty()) {
                                continue;
                            }
                            wxTagIdList.add(wxTagId);
                        }
                        if (wxTagIdList.size() > 0) {
                            // 数据库中存在的标签 id
                            List<Integer> tagIdList = contactTagService.lambdaQuery()
                                    .select(WorkContactTagEntity::getId)
                                    .in(WorkContactTagEntity::getWxContactTagId, wxTagIdList)
                                    .list()
                                    .stream()
                                    .map(WorkContactTagEntity::getId)
                                    .collect(Collectors.toList());

                            // 标签对应的客户与员工关联 id
                            List<WorkContactTagPivotEntity> contactTagPivotEntityList = contactTagPivotService.lambdaQuery()
                                    .select(WorkContactTagPivotEntity::getId, WorkContactTagPivotEntity::getContactTagId)
                                    .eq(WorkContactTagPivotEntity::getContactId, contactEntity.getId())
                                    .eq(WorkContactTagPivotEntity::getEmployeeId, empId)
                                    .list();

                            // 标签对应的客户与员工关联 id
                            List<Integer> contactEmpAllTagId = contactTagPivotEntityList.stream()
                                    .map(WorkContactTagPivotEntity::getContactTagId)
                                    .collect(Collectors.toList());
                            List<Integer> contactEmpAllTagId2 = new ArrayList<>(contactEmpAllTagId);

                            // 需要删除的
                            contactEmpAllTagId.removeAll(tagIdList);
                            List<Integer> removeContactTagPivotIdList = new ArrayList<>();
                            for (WorkContactTagPivotEntity workContactTagPivotEntity : contactTagPivotEntityList) {
                                if (contactEmpAllTagId.contains(workContactTagPivotEntity.getContactTagId())) {
                                    removeContactTagPivotIdList.add(workContactTagPivotEntity.getId());
                                }
                            }
                            contactTagPivotService.removeByIds(removeContactTagPivotIdList);

                            // 需要添加的
                            tagIdList.removeAll(contactEmpAllTagId2);
                            List<WorkContactTagPivotEntity> addContactTagPivotEntityList = new ArrayList<>();
                            for (Integer tagId : tagIdList) {
                                WorkContactTagPivotEntity contactTagPivotEntity = new WorkContactTagPivotEntity();
                                contactTagPivotEntity.setContactId(contactEntity.getId());
                                contactTagPivotEntity.setEmployeeId(empId);
                                contactTagPivotEntity.setContactTagId(tagId);
                                contactTagPivotEntity.setType(1);
                                addContactTagPivotEntityList.add(contactTagPivotEntity);
                            }
                            contactTagPivotService.saveBatch(addContactTagPivotEntityList);
                        } else {
                            List<Integer> contactTagPivotIdList = contactTagPivotService.lambdaQuery()
                                    .select(WorkContactTagPivotEntity::getId)
                                    .eq(WorkContactTagPivotEntity::getEmployeeId, empId)
                                    .eq(WorkContactTagPivotEntity::getContactId, contactEntity.getId())
                                    .list()
                                    .stream()
                                    .map(WorkContactTagPivotEntity::getId)
                                    .collect(Collectors.toList());
                            contactTagPivotService.removeByIds(contactTagPivotIdList);
                        }
                    }
                }
            }
        }
    }

    /**
     * @author: yangpengwei
     * @time: 2021/5/12 5:14 下午
     * @description 增量添加客户标签
     */
    @Override
    public void incrementalContactTagPivot(int empId, int contactId, List<Integer> tagIdList) {
        if (tagIdList.size() > 0) {
            List<WorkContactTagPivotEntity> contactTagPivotEntityList = contactTagPivotService.lambdaQuery()
                    .select(WorkContactTagPivotEntity::getId, WorkContactTagPivotEntity::getContactTagId)
                    .eq(WorkContactTagPivotEntity::getContactId, contactId)
                    .eq(WorkContactTagPivotEntity::getEmployeeId, empId)
                    .list();

            List<Integer> contactTagPivotAllTagId = contactTagPivotEntityList.stream()
                    .map(WorkContactTagPivotEntity::getContactTagId)
                    .collect(Collectors.toList());

            // 需要添加的
            tagIdList.removeAll(contactTagPivotAllTagId);
            List<WorkContactTagPivotEntity> addContactTagPivotEntityList = new ArrayList<>();
            for (Integer tagId : tagIdList) {
                WorkContactTagPivotEntity contactTagPivotEntity = new WorkContactTagPivotEntity();
                contactTagPivotEntity.setContactId(contactId);
                contactTagPivotEntity.setEmployeeId(empId);
                contactTagPivotEntity.setContactTagId(tagId);
                contactTagPivotEntity.setType(1);
                addContactTagPivotEntityList.add(contactTagPivotEntity);
            }
            contactTagPivotService.saveBatch(addContactTagPivotEntityList);

            // 调用企业微信接口添加标签
            contactTagSyncLogic.contactAddWxTag(empId, contactId, tagIdList);
        }
    }

    @Override
    public boolean createAndSyncContact(int corpId, int empId, String wxEmpId, String wxContactId) {
        WorkContactEntity entity = lambdaQuery()
                .eq(WorkContactEntity::getWxExternalUserid, wxContactId)
                .one();
        if (null == entity) {
            entity = new WorkContactEntity();
        }
        updateContactLogic(corpId, empId, wxEmpId, wxContactId, entity);
        return true;
    }

}
