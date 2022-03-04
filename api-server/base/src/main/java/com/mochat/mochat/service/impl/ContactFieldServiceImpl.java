package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.contactfield.TypeEnum;
import com.mochat.mochat.common.util.PinyinUtil;
import com.mochat.mochat.dao.entity.ContactFieldEntity;
import com.mochat.mochat.dao.mapper.ContactFieldMapper;
import com.mochat.mochat.model.contactfield.AddContactFieldModel;
import com.mochat.mochat.model.contactfield.BatchUpdateContactFieldModel;
import com.mochat.mochat.model.contactfield.UpdateContactFieldModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author zhaojinjian
 * @ClassName contactFieldServiceImpl.java
 * @Description TODO
 * @createTime 2020/12/15 11:41
 */
@Service
public class ContactFieldServiceImpl extends ServiceImpl<ContactFieldMapper, ContactFieldEntity> implements IContactFieldService {

    /**
     * @description 获取客户列表筛选 -- 用户画像
     * @author zhaojinjian
     * @createTime 2020/12/30 17:23
     */
    @Override
    public JSONArray getPortrait(Integer fieldId, String name) {
        QueryWrapper<ContactFieldEntity> contactFieldWrapper = new QueryWrapper<>();
        contactFieldWrapper.eq("status", 1);
        if (fieldId != null) {
            contactFieldWrapper.eq("id", fieldId);
        }
        if (name != null && !name.isEmpty()) {
            contactFieldWrapper.eq("name", name);
        }
        List<ContactFieldEntity> contactFieldList = this.list(contactFieldWrapper);
        JSONArray result = new JSONArray();

        JSONObject jsonObject0 = new JSONObject();
        jsonObject0.put("fieldId", 0);
        jsonObject0.put("name", "全部");
        jsonObject0.put("type", "0");
        jsonObject0.put("typeText", "");
        jsonObject0.put("options", Collections.emptyList());
        result.add(jsonObject0);

        if (contactFieldList != null && contactFieldList.size() > 0) {
            contactFieldList.forEach(item -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fieldId", item.getId());
                jsonObject.put("name", item.getLabel());
                jsonObject.put("type", item.getType());
                jsonObject.put("typeText", TypeEnum.getTypeByCode(item.getType()));
                jsonObject.put("options", JSONArray.parse(item.getOptions()));
                result.add(jsonObject);
            });
        }
        return result;
    }

    @Override
    public List<ContactFieldEntity> getContactFieldList(List<Integer> fieidIds) {
        QueryWrapper<ContactFieldEntity> contactFieldWrapper = new QueryWrapper<>();
        contactFieldWrapper.in("id", fieidIds);
        contactFieldWrapper.isNull("deleted_at");
        List<ContactFieldEntity> contactFieldEntityList = this.list(contactFieldWrapper);
        List<ContactFieldEntity> contactFieldEntityList1 = new ArrayList<ContactFieldEntity>();
        return contactFieldEntityList1;
    }

    /**
     * @description 获取高级属性详细
     * @author zhaojinjian
     * @createTime 2020/12/15 11:49
     */
    @Override
    public JSONObject getContactFieldInfo(Integer fieldId) {
        ContactFieldEntity contactFieldEntity = this.baseMapper.selectById(fieldId);
        if (contactFieldEntity != null) {
            JSONObject resultJson = new JSONObject();
            resultJson.put("id", contactFieldEntity.getId());
            resultJson.put("name", contactFieldEntity.getName());
            resultJson.put("label", contactFieldEntity.getLabel());
            resultJson.put("type", contactFieldEntity.getType());
            resultJson.put("typeText", TypeEnum.getTypeByCode(contactFieldEntity.getType()));
            resultJson.put("options", contactFieldEntity.getOptions());
            resultJson.put("order", contactFieldEntity.getOrder());
            resultJson.put("status", contactFieldEntity.getStatus());
            resultJson.put("isSys", contactFieldEntity.getIsSys());
            return resultJson;
        }
        return null;
    }

    /**
     * @description 获取高级属性的集合
     * @author zhaojinjian
     * @createTime 2020/12/15 16:32
     */
    @Override
    public JSONObject getContactFieldList(Integer status, Integer page, Integer perPage) {
        QueryWrapper<ContactFieldEntity> contactFieldWrapper = new QueryWrapper<>();
        contactFieldWrapper.select("id,name,label,type,options,`order`,status,is_sys");
        contactFieldWrapper.isNull("deleted_at");
        if(status != null && status < 2){
            contactFieldWrapper.eq("status", status);
        }
        Page<ContactFieldEntity> pages = new Page<>(page, perPage);
        pages = this.baseMapper.selectPage(pages, contactFieldWrapper);

        List<ContactFieldEntity> contactFieldEntities = pages.getRecords();
        if (contactFieldEntities != null) {
            JSONArray resultJsonArray = new JSONArray();
            contactFieldEntities.forEach(item -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", item.getId());
                jsonObject.put("name", item.getName());
                jsonObject.put("label", item.getLabel());
                jsonObject.put("type", item.getType());
                jsonObject.put("typeText", TypeEnum.getTypeByCode(item.getType()));
                jsonObject.put("options", JSONArray.parse(item.getOptions()));
                jsonObject.put("order", item.getOrder());
                jsonObject.put("status", item.getStatus());
                jsonObject.put("isSys", item.getIsSys());
                resultJsonArray.add(jsonObject);
            });
            JSONObject pageJson = new JSONObject();
            pageJson.put("perPage", perPage);
            pageJson.put("total", pages.getTotal());
            pageJson.put("totalPage", new Double(Math.ceil(pages.getTotal() / perPage)).longValue());
            JSONObject resultJson = new JSONObject();
            resultJson.put("page", pageJson);
            resultJson.put("list", resultJsonArray);
            return resultJson;
        }
        return null;
    }

    /**
     * @description 删除高级属性
     * @author zhaojinjian
     * @createTime 2020/12/15 16:39
     */
    @Override
    public boolean deleteContactField(Integer fieldId) {
        if (fieldId == null) {
            return false;
        }
        ContactFieldEntity contactField = new ContactFieldEntity();
        contactField.setId(fieldId);
        contactField.setDeletedAt(new Date());
        return this.updateById(contactField);
    }

    /**
     * @description 修改高级属性的状态
     * @author zhaojinjian
     * @createTime 2020/12/15 16:52
     */
    @Override
    public boolean updateStatus(Integer fieldId, Integer status) {
        ContactFieldEntity entity = new ContactFieldEntity();
        entity.setId(fieldId);
        entity.setStatus(status);
        return this.updateById(entity);
    }

    /**
     * @description 高级属性新增
     * @author zhaojinjian
     * @createTime 2020/12/16 11:40
     */
    @Override
    public boolean insertContactField(AddContactFieldModel parem) {
        ContactFieldEntity contactFieldEntity = new ContactFieldEntity();
        contactFieldEntity.setType(parem.getType());
        contactFieldEntity.setCreatedAt(new Date());
        contactFieldEntity.setIsSys(0);
        contactFieldEntity.setLabel(parem.getLabel());
        contactFieldEntity.setName(PinyinUtil.getPinyin(parem.getLabel(), ""));
        contactFieldEntity.setOptions(parem.getOptions());
        contactFieldEntity.setOrder(parem.getOrder());
        contactFieldEntity.setStatus(parem.getStatus());
        return this.baseMapper.insert(contactFieldEntity) == 1;
    }

    /**
     * @description 修改高级属性
     * @author zhaojinjian
     * @createTime 2020/12/16 11:46
     */
    @Override
    public boolean updateContactField(UpdateContactFieldModel parem) {

        ContactFieldEntity contactFieldEntity = baseMapper.selectById(parem.getId());
        if (contactFieldEntity == null) {
            //TODO 当前属性不存在
            return false;
        }
        contactFieldEntity.setId(parem.getId());
        contactFieldEntity.setType(parem.getType());
        contactFieldEntity.setUpdatedAt(new Date());
        if (contactFieldEntity.getIsSys() == 0) {
            contactFieldEntity.setLabel(parem.getLabel());
            contactFieldEntity.setName(PinyinUtil.getPinyin(parem.getLabel(), ""));
        }
        contactFieldEntity.setOptions(parem.getOptions());
        contactFieldEntity.setOrder(parem.getOrder());
        contactFieldEntity.setStatus(parem.getStatus());
        return this.updateById(contactFieldEntity);
    }

    @Override
    public boolean updateMultipleContactField(List<ContactFieldEntity> list) {
        return this.updateBatchById(list);
    }

    /**
     * @description 批量修改高级属性
     * @author zhaojinjian
     * @createTime 2020/12/16 11:56
     */
    @Override
    @Transactional
    public boolean BatchUpdateContactField(BatchUpdateContactFieldModel parem) {
        boolean result = false;
        if (parem.getUpdate() != null && parem.getUpdate().size() > 0) {
            List<Integer> fieldId = parem.getUpdate().stream().map(UpdateContactFieldModel::getId).collect(Collectors.toList());
            List<ContactFieldEntity> list = this.baseMapper.selectBatchIds(fieldId);
            list.forEach(contactFieldEntity -> {
                Optional<UpdateContactFieldModel> model = parem.getUpdate().stream().filter(c -> c.getId() == contactFieldEntity.getId()).findAny();
                UpdateContactFieldModel item = model.get();
                contactFieldEntity.setId(item.getId());
                contactFieldEntity.setType(item.getType());
                contactFieldEntity.setUpdatedAt(new Date());
                if (contactFieldEntity.getIsSys() == 0) {
                    contactFieldEntity.setLabel(item.getLabel());
                    contactFieldEntity.setName(PinyinUtil.getPinyin(item.getLabel(), ""));
                }
                contactFieldEntity.setOptions(item.getOptions());
                contactFieldEntity.setOrder(item.getOrder());
                contactFieldEntity.setStatus(item.getStatus());
            });
            result = this.updateBatchById(list);
        } else {
            //TODO 没有修改数据
        }
        if (parem.getDestroy() != null && parem.getDestroy().size() > 0) {
            List<ContactFieldEntity> deleteList = new ArrayList<>();
            parem.getDestroy().forEach(id -> {
                ContactFieldEntity deleteModel = new ContactFieldEntity();
                deleteModel.setId(id);
                deleteModel.setDeletedAt(new Date());
                deleteList.add(deleteModel);
            });
            result = this.updateBatchById(deleteList);
        }
        return result;
    }
}
