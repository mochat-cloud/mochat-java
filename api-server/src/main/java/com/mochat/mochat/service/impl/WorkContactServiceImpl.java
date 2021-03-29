package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.WorkContactEntity;
import com.mochat.mochat.dao.mapper.ContactMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaojinjian
 * @ClassName WorkContactServiceImpl.java
 * @Description TODO
 * @createTime 2020/12/7 17:56
 */
@Service
public class WorkContactServiceImpl extends ServiceImpl<ContactMapper, WorkContactEntity> implements IWorkContactService {

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
        for (String str:
        participantIdArr) {
            sb.append(str).append(",");
        }
        String participantIdStr = sb.substring(0,sb.length()-1);
        QueryWrapper<WorkContactEntity> workContactQueryWrapper = new QueryWrapper<WorkContactEntity>();
        workContactQueryWrapper.select(s);
        workContactQueryWrapper.in("wx_external_userid",participantIdStr);
        workContactQueryWrapper.eq("corp_id",corpId);
        return this.baseMapper.selectList(workContactQueryWrapper);
    }

}
