package com.mochat.mochat.service.contact;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.RespContactErrCodeEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.WorkContactTagGroupEntity;
import com.mochat.mochat.dao.mapper.WorkContactTagGroupMapper;
import com.mochat.mochat.model.workcontact.ContactTagGroupIndexVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.IWorkContactTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/12/14 5:32 下午
 * @description 客户标签组 service
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class WorkContactTagGroupServiceImp extends ServiceImpl<WorkContactTagGroupMapper, WorkContactTagGroupEntity> implements IWorkContactTagGroupService {

    @Autowired
    private WorkContactTagGroupMapper workContactTagGroupMapper;

    @Autowired
    private IWorkContactTagService workContactTagService;

    @Override
    public List<ContactTagGroupIndexVO> getGroupList(RequestPage req) {
        int corpId = AccountService.getCorpId();
        List<WorkContactTagGroupEntity> groupEntities = workContactTagGroupMapper.selectList(
                new QueryWrapper<WorkContactTagGroupEntity>()
                        .select("id", "group_name")
                        .eq("corp_id", corpId)
        );

        List<ContactTagGroupIndexVO> voList = new ArrayList<>();

        ContactTagGroupIndexVO vo0 = new ContactTagGroupIndexVO();
        vo0.setGroupId(0);
        vo0.setGroupName("未分组");
        voList.add(vo0);

        for (WorkContactTagGroupEntity e : groupEntities) {
            ContactTagGroupIndexVO vo = new ContactTagGroupIndexVO();
            vo.setGroupId(e.getId());
            vo.setGroupName(e.getGroupName());
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public ContactTagGroupIndexVO getGroupDetail(Integer groupTagId) {
        if (groupTagId == null) {
            throw new ParamException("groupId 不能为空");
        }

        WorkContactTagGroupEntity entity = new WorkContactTagGroupEntity();
        entity.setId(groupTagId);
        entity = workContactTagGroupMapper.selectOne(new QueryWrapper<>(entity));

        if (entity == null) {
            throw new CommonException(RespContactErrCodeEnum.CONTACT_NO_TAG_GROUP);
        }

        ContactTagGroupIndexVO vo = new ContactTagGroupIndexVO();
        vo.setGroupId(entity.getId());
        vo.setGroupName(entity.getGroupName());

        return vo;
    }

    @Override
    public void deleteGroup(Integer groupTagId) {
        if (groupTagId == null) {
            throw new ParamException("groupId 不能为空");
        }
        WorkContactTagGroupEntity entity = new WorkContactTagGroupEntity();
        entity.setId(groupTagId);
        entity = workContactTagGroupMapper.selectOne(new QueryWrapper<>(entity));
        if (entity == null) {
            throw new CommonException(RespContactErrCodeEnum.CONTACT_NO_TAG_GROUP);
        }

        String wxGroupId = entity.getWxGroupId();
        if (wxGroupId != null && !wxGroupId.isEmpty()) {
            int corpId = AccountService.getCorpId();
            WxApiUtils.requestDelGroupTag(corpId, wxGroupId);
        }

        workContactTagGroupMapper.deleteById(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void createGroup(String groupTagName) {
        int corpId = AccountService.getCorpId();
        WorkContactTagGroupEntity entity = new WorkContactTagGroupEntity();
        entity.setCorpId(corpId);
        entity.setGroupName(groupTagName);
        entity = workContactTagGroupMapper.selectOne(new QueryWrapper<>(entity));
        if (entity != null) {
            throw new CommonException(RespContactErrCodeEnum.CONTACT_TAG_GROUP_ALREADY_EXISTS);
        } else {
            entity = new WorkContactTagGroupEntity();
            entity.setCorpId(corpId);
            entity.setGroupName(groupTagName);
            workContactTagGroupMapper.insert(entity);
        }
    }

    @Override
    public void updateGroup(Integer groupTagId, String groupTagName, Integer isUpdate) {
        if (isUpdate == 1) {
            int count = workContactTagGroupMapper.selectCount(
                    new QueryWrapper<WorkContactTagGroupEntity>()
                    .eq("group_name",groupTagName)
            );
            if (count > 0) {
                throw new CommonException(groupTagName + "标签组已存在");
            }

            WorkContactTagGroupEntity entity = new WorkContactTagGroupEntity();
            entity.setId(groupTagId);
            entity = workContactTagGroupMapper.selectOne(new QueryWrapper<>(entity));
            if (entity == null) {
                throw new ParamException("标签组id不存在");
            }

            String wxGroupId = entity.getWxGroupId();
            if (wxGroupId != null && !wxGroupId.isEmpty()) {
                int corpId = AccountService.getCorpId();
                WxApiUtils.requestEditTag(corpId, wxGroupId, groupTagName);
            }

            entity.setGroupName(groupTagName);
            workContactTagGroupMapper.updateById(entity);
        }
    }

    @Override
    public void wxBackCreateTagGroup(int corpId, String wxTagGroupId) {
        workContactTagService.synContactTag(corpId);
    }

    @Override
    public void wxBackUpdateTagGroup(int corpId, String wxTagGroupId) {
        workContactTagService.synContactTag(corpId);
    }

    @Override
    public void wxBackDeleteTagGroup(int corpId, String wxTagGroupId) {
        WorkContactTagGroupEntity entity = new WorkContactTagGroupEntity();
        entity.setWxGroupId(wxTagGroupId);
        entity.setCorpId(corpId);
        entity = workContactTagGroupMapper.selectOne(new QueryWrapper<>(entity));
        if (entity == null) {
            throw new ParamException("标签组不存在");
        }
        workContactTagService.deleteTagByGroupId(corpId, entity.getId(), false);
        workContactTagGroupMapper.deleteById(entity.getId());
    }
}
