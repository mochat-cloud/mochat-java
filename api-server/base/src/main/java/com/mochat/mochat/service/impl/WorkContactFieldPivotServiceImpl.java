package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.contactfield.TypeEnum;
import com.mochat.mochat.common.em.workcontact.EventEnum;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.dao.entity.ContactFieldEntity;
import com.mochat.mochat.dao.entity.ContactFieldPivotEntity;
import com.mochat.mochat.dao.mapper.ContactFieldMapper;
import com.mochat.mochat.dao.mapper.ContactFieldPivotMapper;
import com.mochat.mochat.model.contactfieldpivot.ContactFieldPivotVO;
import com.mochat.mochat.model.contactfieldpivot.UpdateContactFieldPivotModel;
import com.mochat.mochat.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhaojinjian
 * @ClassName WorkContactFieldPivotServiceImpl.java
 * @Description TODO
 * @createTime 2020/12/24 16:47
 */
@Service
public class WorkContactFieldPivotServiceImpl extends ServiceImpl<ContactFieldPivotMapper, ContactFieldPivotEntity> implements IWorkContactFieldPivotService {

    @Autowired
    private IContactFieldService contactFieldService;

    @Autowired
    private ContactFieldMapper contactFieldMapper;

    @Autowired
    private IContactService contactService;

    /**
     * @description 客户 - 客户详情 - 用户画像
     * @author zhaojinjian
     * @createTime 2020/12/24 18:02
     */
    @Override
    public List<ContactFieldPivotVO> getContactFieldPivotList(Integer contactId) {
        List<ContactFieldPivotVO> voList = new ArrayList<>();

        List<ContactFieldEntity> contactFieldEntityList = contactFieldMapper.selectList(
                new QueryWrapper<>(ContactFieldEntity.builder().status(1).build())
        );

        QueryWrapper<ContactFieldPivotEntity> contactFieldPivotWrapper = new QueryWrapper<>();
        contactFieldPivotWrapper.eq("contact_id", contactId);
        List<ContactFieldPivotEntity> contactFieldPivotEntityList = this.list(contactFieldPivotWrapper);
        HashMap<Integer, ContactFieldPivotEntity> contactFieldPivotEntityMap = new HashMap<>(contactFieldPivotEntityList.size());
        for (ContactFieldPivotEntity e : contactFieldPivotEntityList) {
            contactFieldPivotEntityMap.put(e.getContactFieldId(), e);
        }

        ContactFieldPivotVO vo;
        for (ContactFieldEntity e : contactFieldEntityList) {
            vo = new ContactFieldPivotVO();
            vo.setContactFieldId(e.getId());
            vo.setName(e.getLabel());
            vo.setType(e.getType());
            vo.setTypeText(TypeEnum.getTypeByCode(e.getType()));
            vo.setOptions(JSON.parseArray(e.getOptions(), String.class));
            vo.setValue("");
            ContactFieldPivotEntity contactFieldPivotEntity = contactFieldPivotEntityMap.get(e.getId());
            if (contactFieldPivotEntity != null) {
                vo.setContactFieldPivotId(contactFieldPivotEntity.getId());
                String value = contactFieldPivotEntity.getValue();
                if (value.startsWith("[")) {
                    vo.setValue(JSON.parseArray(value,String.class));
                } else {
                    vo.setValue(value);
                }
                if (TypeEnum.PICTURE.getCode().equals(e.getType())) {
                    vo.setPictureFlag(AliyunOssUtils.getUrl(contactFieldPivotEntity.getValue()));
                }
            }
            voList.add(vo);
        }
        return voList;
    }

    /**
     * @description 客户 - 客户详情 - 编辑用户画像
     * @author zhaojinjian
     * @createTime 2020/12/24 18:31
     */
    @Override
    public boolean updateContactFieldPivot(UpdateContactFieldPivotModel param) {
        QueryWrapper<ContactFieldPivotEntity> contactFieldPivotWrapper = new QueryWrapper<>();
        contactFieldPivotWrapper.eq("contact_id", param.getContactId());
        List<ContactFieldPivotEntity> oldContactFieldPivotEntityList = this.list(contactFieldPivotWrapper);
        HashMap<Integer, String> contactFieldPivotEntityMap = new HashMap<>(oldContactFieldPivotEntityList.size());
        for (ContactFieldPivotEntity e : oldContactFieldPivotEntityList) {
            contactFieldPivotEntityMap.put(e.getContactFieldId(), e.getValue());
        }

        StringBuffer stringBuffer = new StringBuffer();
        List<Integer> contactFieldIdList = new ArrayList<>();
        List<ContactFieldPivotEntity> contactFieldPivotList = new ArrayList<>();
        List<UpdateContactFieldPivotModel.UserPortrait> userPortraitList = JSON.parseArray(param.getUserPortrait(),UpdateContactFieldPivotModel.UserPortrait.class);
        userPortraitList.forEach(item -> {
            ContactFieldPivotEntity contactFieldPivotEntity = new ContactFieldPivotEntity();
            contactFieldPivotEntity.setId(item.getContactFieldPivotId());
            contactFieldPivotEntity.setContactFieldId(item.getContactFieldId());
            contactFieldPivotEntity.setContactId(param.getContactId());
            contactFieldPivotEntity.setValue(item.getValue());
            contactFieldPivotList.add(contactFieldPivotEntity);

            String oldValue = contactFieldPivotEntityMap.get(item.getContactFieldId());
            String newValue = item.getValue();
            String oldV = oldValue == null ? "" : oldValue;
            String newV = newValue == null ? "" : newValue;
            if (!oldV.equals(newV)) {
                contactFieldIdList.add(item.getContactFieldId());
            }
        });

        List<ContactFieldEntity> contactFieldEntityList = contactFieldMapper.selectList(
                new QueryWrapper<>(ContactFieldEntity.builder().status(1).build())
        );
        HashMap<Integer, ContactFieldEntity> contactFieldEntityMap = new HashMap<>(contactFieldEntityList.size());
        for (ContactFieldEntity e : contactFieldEntityList) {
            contactFieldEntityMap.put(e.getId(), e);
        }

        for (Integer id : contactFieldIdList) {
            ContactFieldEntity contactFieldEntity = contactFieldEntityMap.get(id);
            stringBuffer.append(contactFieldEntity.getLabel()+" ");
        }

        boolean result = this.saveOrUpdateBatch(contactFieldPivotList);

        if (result) {
            contactService.saveTrack(
                    AccountService.getEmpId(),
                    param.getContactId(),
                    EventEnum.USER_PORTRAIT,
                    EventEnum.USER_PORTRAIT.getMsg() + ": " + stringBuffer.toString()
            );
        }

        return result;
    }
}
