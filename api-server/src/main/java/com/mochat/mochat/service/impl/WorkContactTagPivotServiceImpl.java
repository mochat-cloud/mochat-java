package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.workcontact.EventEnum;
import com.mochat.mochat.dao.entity.WorkContactTagEntity;
import com.mochat.mochat.dao.entity.WorkContactTagPivotEntity;
import com.mochat.mochat.dao.mapper.WorkContactTagMapper;
import com.mochat.mochat.dao.mapper.WorkContactTagPivotMapper;
import com.mochat.mochat.model.workcontacttag.ContactTagId;
import com.mochat.mochat.service.contact.ICorpTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaojinjian
 * @ClassName WorkContactTagPivotServiceImpl.java
 * @Description TODO
 * @createTime 2020/12/6 11:42
 */
@Service
public class WorkContactTagPivotServiceImpl extends ServiceImpl<WorkContactTagPivotMapper, WorkContactTagPivotEntity> implements IWorkContactTagPivotService {

    @Autowired
    @Lazy
    private ICorpTagService corpTagService;

    @Autowired
    private IWorkContactTagService contactTagService;

    @Autowired
    private WorkContactTagMapper workContactTagMapper;

    @Autowired
    private IContactService contactService;

    @Override
    public List<Integer> getContactTapId(Integer empId, Integer contactId) {
        QueryWrapper<WorkContactTagPivotEntity> tagPivotWrapper = new QueryWrapper();
        tagPivotWrapper.select("contact_tag_id");
        tagPivotWrapper.eq("contact_id", contactId);
        tagPivotWrapper.eq("employee_id", empId);
        List<WorkContactTagPivotEntity> list = this.baseMapper.selectList(tagPivotWrapper);
        if (list.isEmpty()) {
            return null;
        }
        return list.stream().map(WorkContactTagPivotEntity::getContactTagId).collect(Collectors.toList());
    }

    /**
     * @description 根据企业id获取成员下客户的所有标签id
     * @author zhaojinjian
     * @createTime 2020/12/21 10:46
     */
    @Override
    public Map<String, Integer> getContactTapIdOrWxTagId(Integer empId, Integer contactId) {
        List<Integer> tagIds = getContactTapId(empId, contactId);
        return contactTagService.getContactTagIds(tagIds);
    }

    /**
     * @description 获取员工下所有客户的标签id
     * @author zhaojinjian
     * @createTime 2020/12/12 15:04
     */
    @Override
    public List<ContactTagId> getContactTapId(Integer empId) {
        QueryWrapper<WorkContactTagPivotEntity> tagPivotWrapper = new QueryWrapper();
        tagPivotWrapper.eq("employee_id", empId);
        tagPivotWrapper.isNull("deleted_at");
        List<WorkContactTagPivotEntity> list = this.baseMapper.selectList(tagPivotWrapper);
        if (list.isEmpty()) {
            return null;
        }
        List<ContactTagId> contactTagIds = new ArrayList<>();
        list.forEach(item -> {
            ContactTagId model = new ContactTagId();
            model.setContactId(item.getContactId());
            model.setTagId(item.getContactTagId());
            model.setEmpId(item.getEmployeeId());
            contactTagIds.add(model);
        });
        return contactTagIds;
    }

    /**
     * @description 获取所有成员对客户打的标签id
     * @author zhaojinjian
     * @createTime 2020/12/12 15:04
     */
    @Override
    public List<ContactTagId> getContactTapId(List<Integer> empIds) {
        QueryWrapper<WorkContactTagPivotEntity> tagPivotWrapper = new QueryWrapper();
        tagPivotWrapper.in("employee_id", empIds);
        tagPivotWrapper.isNull("deleted_at");
        List<WorkContactTagPivotEntity> list = this.baseMapper.selectList(tagPivotWrapper);
        if (list.isEmpty()) {
            return null;
        }
        List<ContactTagId> contactTagIds = new ArrayList<>();
        list.forEach(item -> {
            ContactTagId model = new ContactTagId();
            model.setContactId(item.getContactId());
            model.setTagId(item.getContactTagId());
            model.setEmpId(item.getEmployeeId());
            contactTagIds.add(model);
        });
        return contactTagIds;
    }

    /**
     * @description 修改客户下的标签个数
     * @author zhaojinjian
     * @createTime 2020/12/6 17:24
     */
    @Override
    @Transactional
    public boolean updateContactTapPivot(Integer empId, Integer contactId, List<Integer> tagIds) {
        QueryWrapper<WorkContactTagPivotEntity> contactTagPivotWrapper = new QueryWrapper<>();
        contactTagPivotWrapper.select("contact_tag_id");
        contactTagPivotWrapper.eq("employee_id", empId);
        contactTagPivotWrapper.eq("contact_id", contactId);
        List<WorkContactTagPivotEntity> contactTagPivots = this.baseMapper.selectList(contactTagPivotWrapper);
        if (!contactTagPivots.isEmpty()) {
            List<Integer> db_tagIds = contactTagPivots.stream().map(WorkContactTagPivotEntity::getContactTagId).collect(Collectors.toList());
            //如果tagIds的集合为空，删除数据库当前客户的标签
            if (tagIds.isEmpty()) {
                return deleteMultipleTagPivot(db_tagIds, empId, contactId);
            } else {
                //复制一个新tagIds集合，用于取差集
                List<Integer> compare_tagIds = new ArrayList<>();
                compare_tagIds.addAll(tagIds);

                //要新增的标签id
                compare_tagIds.removeAll(db_tagIds);
                if (compare_tagIds.size() > 0) {
                    insertMultipleTagPivot(compare_tagIds, 2, empId, contactId);
                }
                //要删除的标签id
                db_tagIds.removeAll(tagIds);
                if (db_tagIds.size() > 0) {
                    deleteMultipleTagPivot(db_tagIds, empId, contactId);
                }
            }
        } else {
            insertMultipleTagPivot(tagIds, 2, empId, contactId);
        }

        return true;
    }

    @Override
    public boolean updateContactTapPivot(List<WorkContactTagPivotEntity> list) {
        return this.updateBatchById(list);
    }

    /**
     * @description 批量添加客户下标签的个数
     * @author zhaojinjian
     * @createTime 2020/12/6 17:24
     */
    @Override
    @Transactional
    public boolean insertMultipleTagPivot(List<Integer> tagIds, Integer type, Integer empId, Integer contactId) {
        List<WorkContactTagPivotEntity> list = new ArrayList<>();
        tagIds.forEach(item -> {
            WorkContactTagPivotEntity model = new WorkContactTagPivotEntity();
            model.setContactTagId(item);
            model.setContactId(contactId);
            model.setCreatedAt(new Date());
            model.setType(type);
            model.setEmployeeId(empId);
            list.add(model);

        });
        boolean result = this.saveBatch(list);
        if (result) {
            // 调用异步企业微信修改标签
            corpTagService.wxUpdateTag(empId, contactId, tagIds);
            StringBuilder stringBuffer = new StringBuilder();
            List<WorkContactTagEntity> tagEntityList = workContactTagMapper.selectBatchIds(tagIds);
            for (WorkContactTagEntity e : tagEntityList) {
                stringBuffer.append("、【").append(e.getName()).append("】");
            }
            contactService.saveTrack(empId, contactId, EventEnum.TAG, "系统对该客户打标签" + stringBuffer.substring(1));
        }
        return result;
    }

    /**
     * @description 批量添加客户标签中间表数据
     * @author zhaojinjian
     * @createTime 2020/12/9 18:20
     */
    @Override
    @Transactional
    public boolean insertAllTagPivot(List<WorkContactTagPivotEntity> contactTagPivots) {

        return this.saveBatch(contactTagPivots);
    }

    @Override
    @Transactional
    public boolean insertTagPivotOfWX_TagId(JSONArray tags, JSONArray appendTags, Integer empId, Integer contactId) {
        List<Integer> tagIds = new ArrayList<>();
        List<Integer> resultTagIds = new ArrayList<>();
        tagIds = getContactTapId(empId, contactId);
        if (tagIds != null) {
            resultTagIds.addAll(tagIds);
        }
        List<String> wx_tagIds = new ArrayList<>();
        Map<String, WorkContactTagPivotEntity> contactTagPivotMap = new HashMap<>();
        for (int l = 0; l < tags.size(); l++) {
            JSONObject tag = tags.getJSONObject(l);
            String tagId = tag.getString("tag_id");
            if (tagId == null || tagId.isEmpty()) {
                continue;
            }
            WorkContactTagPivotEntity contactTagPivot = new WorkContactTagPivotEntity();
            contactTagPivot.setCreatedAt(new Date());
            contactTagPivot.setType(tag.getInteger("type"));
            contactTagPivot.setEmployeeId(empId);
            contactTagPivot.setContactId(contactId);
            contactTagPivotMap.put(tag.getString("tag_id"), contactTagPivot);
            wx_tagIds.add(tag.getString("tag_id"));
        }
        List<Integer> deleteTagIds = new ArrayList<>();
        List<WorkContactTagPivotEntity> finalTagId = new ArrayList<>();
        if (contactTagPivotMap.size() > 0) {
            Map<String, Integer> newTagIds = contactTagService.getContactTagId(wx_tagIds);
            if (newTagIds.size() <= 0) {
                return true;
            }

            deleteTagIds = resultTagIds.stream().filter(t -> !newTagIds.containsValue(t)).collect(Collectors.toList());
            newTagIds.forEach((key, value) -> {
                if (!resultTagIds.contains(value)) {
                    WorkContactTagPivotEntity model = contactTagPivotMap.get(key);
                    model.setContactTagId(value);
                    finalTagId.add(model);
                }
            });
        }

        if (appendTags != null && appendTags.size() > 0) {
            for (int i = 0; i < appendTags.size(); i++) {
                JSONObject appendTag = tags.getJSONObject(i);
                if (finalTagId != null && finalTagId.size() > 0) {
                    Optional<WorkContactTagPivotEntity> contactTagPivotOp = finalTagId.stream().filter(c -> c.getId() == appendTag.getInteger("tagId")).findAny();
                    if (contactTagPivotOp.get() != null) {
                        continue;
                    }
                }
                WorkContactTagPivotEntity contactTagPivot = new WorkContactTagPivotEntity();
                contactTagPivot.setCreatedAt(new Date());
                contactTagPivot.setType(1);
                contactTagPivot.setEmployeeId(empId);
                contactTagPivot.setContactId(contactId);
                contactTagPivot.setId(appendTag.getInteger("tagId"));
                finalTagId.add(contactTagPivot);
            }

        }

        if (finalTagId.size() > 0) {
            this.saveBatch(finalTagId);
        }
        if (deleteTagIds.size() > 0) {
            QueryWrapper deleteWrapper = new QueryWrapper();
            deleteWrapper.in("contact_tag_id", deleteTagIds);
            deleteWrapper.eq("employee_id", empId);
            deleteWrapper.eq("contact_id", contactId);
            this.baseMapper.delete(deleteWrapper);
        }
        return true;
    }

    /**
     * @description 彻底删除客户下的标签
     * @author zhaojinjian
     * @createTime 2020/12/6 17:25
     */
    @Override
    @Transactional
    public boolean deleteMultipleTagPivot(List<Integer> tagIds, Integer empId, Integer contactId) {
        this.baseMapper.deleteBatchIds(tagIds);
        // 调用异步企业微信修改标签
        corpTagService.wxUpdateTag(empId, contactId, tagIds);
        return true;
    }

    @Override
    public boolean deleteContactTagPivot(Integer empId, Integer contactId) {
        WorkContactTagPivotEntity entity = new WorkContactTagPivotEntity();
        entity.setEmployeeId(empId);
        entity.setContactId(contactId);
        this.baseMapper.delete(new QueryWrapper<>(entity));
        return true;
    }

}
