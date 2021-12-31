package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.workupdatetime.TypeEnum;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.api.RespPageDataVO;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.WorkContactTagEntity;
import com.mochat.mochat.dao.entity.WorkContactTagGroupEntity;
import com.mochat.mochat.dao.entity.WorkContactTagPivotEntity;
import com.mochat.mochat.dao.mapper.WorkContactTagMapper;
import com.mochat.mochat.model.workcontact.ContactTagDetailVO;
import com.mochat.mochat.model.workcontact.ContactTagIndexVO;
import com.mochat.mochat.model.workcontact.ContactTagVO;
import com.mochat.mochat.model.workcontacttag.ContactTagId;
import com.mochat.mochat.model.workcontacttag.GetContactTapModel;
import com.mochat.mochat.model.workcontacttag.GetEmployeeTagModel;
import com.mochat.mochat.model.workcontacttag.WxTagDTO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.IWorkUpdateTimeService;
import com.mochat.mochat.service.contact.IWorkContactTagGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaojinjian
 * @ClassName WorkContactTagServiceImpl.java
 * @Description TODO
 * @createTime 2020/12/3 14:30
 */
@Slf4j
@Service
public class WorkContactTagServiceImpl extends ServiceImpl<WorkContactTagMapper, WorkContactTagEntity> implements IWorkContactTagService {

    @Autowired
    private IWorkContactTagPivotService contactTagPivotService;

    @Autowired
    private IWorkContactTagGroupService contactTagGroupService;

    @Autowired
    private IWorkUpdateTimeService workUpdateTimeService;

    @Override
    public List<GetContactTapModel> getContactTapName(Integer empId, Integer contactId) {
        List<GetContactTapModel> list = new ArrayList<>();
        List<Integer> contactTagId = contactTagPivotService.getContactTapId(empId, contactId);
        if (contactTagId == null || contactTagId.size() < 1) {
            return list;
        }

        QueryWrapper<WorkContactTagEntity> tagWrapper = new QueryWrapper<>();
        tagWrapper.select("id", "name");
        tagWrapper.in("id", contactTagId);
        List<WorkContactTagEntity> tags = this.list(tagWrapper);
        tags.forEach(item -> {
            GetContactTapModel model = new GetContactTapModel();
            model.setTagId(item.getId());
            model.setTagName(item.getName());
            list.add(model);
        });
        return list;
    }

    @Override
    public List<GetEmployeeTagModel> getEmployeeTapName(Integer empId) {
        List<GetEmployeeTagModel> list = new ArrayList<>();
        QueryWrapper<WorkContactTagPivotEntity> tagPivotWrapper = new QueryWrapper();
        tagPivotWrapper.eq("employee_id", empId);
        tagPivotWrapper.isNull("deleted_at");
        List<ContactTagId> tagPivot = contactTagPivotService.getContactTapId(empId);
        if (tagPivot != null && tagPivot.size() > 0) {
            List<Integer> contactTagId = tagPivot.stream().map(ContactTagId::getTagId).collect(Collectors.toList());
            QueryWrapper<WorkContactTagEntity> tagWrapper = new QueryWrapper<>();
            tagWrapper.select("id,name,`order`");
            tagWrapper.in("id", contactTagId);
            tagWrapper.isNull("deleted_at");
            List<WorkContactTagEntity> tags = this.list(tagWrapper);
            if (tags != null && tags.size() > 0) {
                tagPivot.forEach(item -> {
                    GetEmployeeTagModel model = new GetEmployeeTagModel();
                    model.setContactId(item.getContactId());
                    model.setTagId(item.getTagId());
                    Optional<WorkContactTagEntity> op = tags.stream().filter(tag -> tag.getId() == item.getTagId()).findAny();
                    model.setTagName(op.get().getName());
                    model.setEmpId(item.getEmpId());
                    list.add(model);
                });
            }
        }
        return list;
    }

    @Override
    public List<GetEmployeeTagModel> getEmployeeTapName(List<Integer> empIds) {
        List<ContactTagId> tagPivot = contactTagPivotService.getContactTapId(empIds);
        if (Objects.isNull(tagPivot) || tagPivot.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> contactTagId = tagPivot.stream().map(ContactTagId::getTagId).collect(Collectors.toList());
        QueryWrapper<WorkContactTagEntity> tagWrapper = new QueryWrapper<>();
        tagWrapper.select("id,name,`order`");
        tagWrapper.in("id", contactTagId);
        tagWrapper.isNull("deleted_at");
        List<WorkContactTagEntity> tags = this.list(tagWrapper);
        List<GetEmployeeTagModel> list = new ArrayList<>();
        tagPivot.forEach(item -> {
            GetEmployeeTagModel model = new GetEmployeeTagModel();
            model.setContactId(item.getContactId());
            model.setTagId(item.getTagId());
            Optional<WorkContactTagEntity> op = tags.stream().filter(tag -> tag.getId() == item.getTagId()).findAny();
            model.setTagName(op.get().getName());
            model.setEmpId(item.getEmpId());
            list.add(model);
        });
        return list;
    }

    @Override
    public List<String> getWxContactTagId(List<Integer> tagIds) {
        List<WorkContactTagEntity> list = this.baseMapper.selectBatchIds(tagIds);
        if (list.isEmpty()) {
            return null;
        }
        return list.stream().map(WorkContactTagEntity::getWxContactTagId).collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getContactTagIds(List<Integer> tagId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.select("id,wx_contact_tag_id");
        wrapper.in("id", tagId);
        List<WorkContactTagEntity> contactTag = this.list(wrapper);
        if (contactTag == null || contactTag.isEmpty() || contactTag.size() <= 0) {
            return new HashMap<>();
        }
        return contactTag.stream().collect(Collectors.toMap(entry -> entry.getWxContactTagId(), entry -> entry.getId()));
    }

    @Override
    public Map<String, Integer> getContactTagId(List<String> wx_tagId) {
        if (wx_tagId.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.select("id,wx_contact_tag_id");
        wrapper.in("wx_contact_tag_id", wx_tagId);
        List<WorkContactTagEntity> contactTag = this.list(wrapper);
        if (contactTag == null || contactTag.isEmpty() || contactTag.size() <= 0) {
            return new HashMap<>();
        }
        return contactTag.stream().collect(Collectors.toMap(entry -> entry.getWxContactTagId(), entry -> entry.getId()));
    }

    @Override
    public Map<String, Integer> getContactTagId(Integer corpId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.select("id,wx_contact_tag_id");
        wrapper.eq("corp_id", corpId);
        wrapper.isNull("deleted_at");
        List<WorkContactTagEntity> contactTag = this.list(wrapper);
        if (contactTag == null || contactTag.isEmpty() || contactTag.size() <= 0) {
            return new HashMap<>();
        }
        return contactTag.stream().collect(Collectors.toMap(entry -> entry.getWxContactTagId(), entry -> entry.getId()));
    }

    /**
     * 客户标签管理 - 列表
     */
    @Override
    public Map<String, Object> getTagList(Integer tagGroupId, ReqPageDto reqPageDto) {
        int corpId = AccountService.getCorpId();
        tagGroupId = tagGroupId == null ? 0 : tagGroupId;

        Page<WorkContactTagEntity> page = ApiRespUtils.initPage(reqPageDto);
        lambdaQuery().select(WorkContactTagEntity::getId, WorkContactTagEntity::getName)
                .eq(WorkContactTagEntity::getCorpId, corpId)
                .eq(WorkContactTagEntity::getContactTagGroupId, tagGroupId)
                .page(page);

        List<WorkContactTagEntity> tagEntityList = page.getRecords();
        List<ContactTagIndexVO> voList = new ArrayList<>();
        for (WorkContactTagEntity entity : tagEntityList) {
            ContactTagIndexVO vo = new ContactTagIndexVO();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            Integer count = Math.toIntExact(contactTagPivotService.lambdaQuery()
                    .eq(WorkContactTagPivotEntity::getContactTagId, entity.getId())
                    .count());
            vo.setContactNum(count == null ? 0 : count);
            voList.add(vo);
        }

        String syncTagTime = workUpdateTimeService.getLastUpdateTime(TypeEnum.TAG);

        RespPageDataVO respPageDataVO = RespPageDataVO.getInstance(page);
        Map<String, Object> map = new HashMap<>(3);
        map.put("page", respPageDataVO.getPage());
        map.put("list", voList);
        map.put("syncTagTime", syncTagTime);

        return map;
    }

    /**
     * 删除标签
     */
    @Override
    public void deleteTag(String tagIds) {
        int corpId = AccountService.getCorpId();

        String[] ids = tagIds.split(",");
        List<WorkContactTagEntity> tagEntityList = this.list(
                new QueryWrapper<WorkContactTagEntity>()
                        .select("id", "wx_contact_tag_id")
                        .eq("corp_id", corpId)
                        .in("id", Arrays.asList(ids))
        );

        List<String> wxTagIds = new ArrayList<>();
        for (WorkContactTagEntity tagEntity : tagEntityList) {
            wxTagIds.add(tagEntity.getWxContactTagId());
            this.baseMapper.deleteById(tagEntity.getId());

            // 删除客户标签关联表
            List<Integer> tagPivotIdList = contactTagPivotService.lambdaQuery()
                    .select(WorkContactTagPivotEntity::getId)
                    .eq(WorkContactTagPivotEntity::getContactTagId, tagEntity.getId())
                    .list()
                    .stream()
                    .map(WorkContactTagPivotEntity::getId)
                    .collect(Collectors.toList());

            contactTagPivotService.removeByIds(tagPivotIdList);

        }

        // 删除微信标签
        WxApiUtils.requestDelTags(corpId, wxTagIds);

    }

    @Override
    public void deleteTagByGroupId(int corpId, int tagGroupId, boolean callWx) {
        List<WorkContactTagEntity> tagEntityList = this.list(
                new QueryWrapper<WorkContactTagEntity>()
                        .select("id")
                        .eq("corp_id", corpId)
                        .eq("contact_tag_group_id", tagGroupId)
        );

        List<Integer> tagIdList = new ArrayList<>();
        for (WorkContactTagEntity tagEntity : tagEntityList) {
            tagIdList.add(tagEntity.getId());
        }

        this.baseMapper.deleteBatchIds(tagIdList);

        // 删除客户标签关联表
        List<Integer> contactTagPivotIdList = contactTagPivotService.lambdaQuery()
                .select(WorkContactTagPivotEntity::getId)
                .in(WorkContactTagPivotEntity::getContactTagId, tagIdList)
                .list().stream().map(WorkContactTagPivotEntity::getId)
                .collect(Collectors.toList());
        contactTagPivotService.removeByIds(contactTagPivotIdList);
    }

    /**
     * 同步客户标签
     */
    @Override
    public void synContactTag(int corpId) {
        String resultJson = WxApiUtils.requestGetAllTag(corpId);
        List<WxTagDTO> dtos = JSON.parseArray(resultJson, WxTagDTO.class);
        Map<String, WxTagDTO> wxGroupTagMap = new HashMap<>(0);
        for (WxTagDTO dto : dtos) {
            wxGroupTagMap.put(dto.getGroupId(), dto);
        }

        List<WorkContactTagGroupEntity> tagGroupEntities = contactTagGroupService.lambdaQuery()
                .eq(WorkContactTagGroupEntity::getCorpId, corpId)
                .list();

        for (WorkContactTagGroupEntity tagGroupEntity : tagGroupEntities) {
            String wxGroupId = tagGroupEntity.getWxGroupId();
            if (wxGroupTagMap.containsKey(wxGroupId)) {
                // 更新标签组和标签组下面的标签
                updateGroupTagAndTag(wxGroupTagMap.get(wxGroupId));
            } else {
                // 删除标签组
                contactTagGroupService.removeById(tagGroupEntity.getId());
                // 删除标签组下面的标签
                List<Integer> contactTagIdList = lambdaQuery()
                        .select(WorkContactTagEntity::getId)
                        .eq(WorkContactTagEntity::getCorpId, corpId)
                        .eq(WorkContactTagEntity::getContactTagGroupId, tagGroupEntity.getId())
                        .list()
                        .stream().map(WorkContactTagEntity::getId)
                        .collect(Collectors.toList());

                for (Integer contactTagId : contactTagIdList) {
                    deleteTagAndPivotByTagId(contactTagId);
                }
            }
            wxGroupTagMap.remove(wxGroupId);
        }

        // 新增标签组
        for (WxTagDTO dto : wxGroupTagMap.values()) {
            WorkContactTagGroupEntity entity = new WorkContactTagGroupEntity();
            entity.setWxGroupId(dto.getGroupId());
            entity.setCorpId(corpId);
            entity.setGroupName(dto.getGroupName());
            entity.setOrder(dto.getOrder());

            // 事务隔离 创建标签组
            createTagGroup(entity);

            // 创建标签组下面的标签
            createTag(entity, dto);
        }

        // 更新 mc_work_update_time
        workUpdateTimeService.updateSynTime(corpId, TypeEnum.TAG);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    void createTagGroup(WorkContactTagGroupEntity groupEntity) {
        contactTagGroupService.save(groupEntity);
    }

    private void createTag(WorkContactTagGroupEntity entity, WxTagDTO dto) {
        List<WxTagDTO.TagDTO> tagDTOList = dto.getTag();
        for (WxTagDTO.TagDTO tagDto : tagDTOList) {
            createTag(entity, tagDto);
        }
    }

    private void updateGroupTagAndTag(WxTagDTO dto) {
        String wxGroupId = dto.getGroupId();
        WorkContactTagGroupEntity entity = new WorkContactTagGroupEntity();
        entity.setWxGroupId(wxGroupId);
        entity = contactTagGroupService.lambdaQuery()
                .eq(WorkContactTagGroupEntity::getWxGroupId, wxGroupId)
                .one();

        // 更新标签组
        entity.setGroupName(dto.getGroupName());
        entity.setOrder(dto.getOrder());
        contactTagGroupService.updateById(entity);

        List<WxTagDTO.TagDTO> tagDTOList = dto.getTag();
        List<WorkContactTagEntity> tagEntityList = this.list(
                new QueryWrapper<WorkContactTagEntity>()
                        .eq("corp_id", entity.getCorpId())
                        .eq("contact_tag_group_id", entity.getId())
        );

        Map<String, WxTagDTO.TagDTO> tagDTOMap = new HashMap<>(0);
        for (WxTagDTO.TagDTO tagDto : tagDTOList) {
            tagDTOMap.put(tagDto.getId(), tagDto);
        }

        for (WorkContactTagEntity tagEntity : tagEntityList) {
            String wxContactTagId = tagEntity.getWxContactTagId();
            if (tagDTOMap.containsKey(wxContactTagId)) {
                // 更新标签
                WxTagDTO.TagDTO tagDto = tagDTOMap.get(wxContactTagId);
                tagEntity.setName(tagDto.getName());
                tagEntity.setOrder(tagDto.getOrder());
                this.updateById(tagEntity);
            } else {
                // 删除标签
                deleteTagAndPivotByTagId(tagEntity.getId());
            }
            tagDTOMap.remove(wxContactTagId);
        }

        // 新增标签
        for (WxTagDTO.TagDTO tagDto : tagDTOMap.values()) {
            createTag(entity, tagDto);
        }
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    void createTag(WorkContactTagGroupEntity entity, WxTagDTO.TagDTO tagDto) {
        WorkContactTagEntity tagEntity = new WorkContactTagEntity();
        tagEntity.setWxContactTagId(tagDto.getId());
        tagEntity.setCorpId(entity.getCorpId());
        tagEntity.setName(tagDto.getName());
        tagEntity.setOrder(tagDto.getOrder());
        tagEntity.setContactTagGroupId(entity.getId());
        this.save(tagEntity);
    }

    @Override
    public List<ContactTagVO> getAllTag(Integer tagGroupId) {
        int corpId = AccountService.getCorpId();

        QueryWrapper<WorkContactTagEntity> wrapper = new QueryWrapper<>();
        wrapper.select("id", "name");
        wrapper.eq("corp_id", corpId);
        if (tagGroupId != null) {
            wrapper.eq("contact_tag_group_id", tagGroupId);
        }

        List<WorkContactTagEntity> tagEntityList = this.list(wrapper);

        List<ContactTagVO> voList = new ArrayList<>();
        for (WorkContactTagEntity entity : tagEntityList) {
            ContactTagVO vo = new ContactTagVO();
            vo.setId(entity.getId());
            vo.setName(entity.getName());

            voList.add(vo);
        }

        return voList;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void createTag(Integer groupTagId, String tagNames) {
        int corpId = AccountService.getCorpId();

        WorkContactTagGroupEntity groupEntity = contactTagGroupService.getById(groupTagId);
        if (groupEntity == null) {
            throw new ParamException("标签组不存在");
        }

        JSONArray tags = JSON.parseArray(tagNames);
        List<String> tagNameList = tags.toJavaList(String.class);

        // 判断标签是否存在
        int count = Math.toIntExact(lambdaQuery().eq(WorkContactTagEntity::getCorpId, corpId)
                .eq(WorkContactTagEntity::getContactTagGroupId, groupEntity.getId())
                .in(WorkContactTagEntity::getName, tagNameList)
                .count());
        if (count > 0) {
            throw new ParamException("标签添加重复");
        }

        // 标签添加到微信
        String wxGroupId = groupEntity.getWxGroupId();
        String resultJson;
        // 创建微信标签
        if (wxGroupId == null || wxGroupId.isEmpty()) {
            String tagGroupName = groupEntity.getGroupName();
            resultJson = WxApiUtils.requestCreateGroupAndTags(corpId, tagGroupName, tagNameList);
        } else {
            resultJson = WxApiUtils.requestCreateTags(corpId, wxGroupId, tagNameList);
        }

        // 更新标签组
        WxTagDTO dto = JSON.parseObject(resultJson, WxTagDTO.class);
        if (wxGroupId == null || wxGroupId.isEmpty()) {
            wxGroupId = dto.getGroupId();
            groupEntity.setWxGroupId(wxGroupId);
            contactTagGroupService.updateById(groupEntity);
        }

        // 微信标签添加到数据库
        createTag(groupEntity, dto);
    }

    @Override
    public ContactTagDetailVO getTagDetail(Integer tagId) {
        if (tagId == null) {
            throw new ParamException("标签 id 无效");
        }

        WorkContactTagEntity entity = this.baseMapper.selectById(tagId);
        if (entity == null) {
            throw new ParamException("标签不存在");
        }

        ContactTagDetailVO vo = new ContactTagDetailVO();
        vo.setTagId(entity.getId());
        vo.setGroupId(entity.getContactTagGroupId());
        vo.setTagName(entity.getName());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void moveTags(String tagIds, Integer groupId) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new ParamException("标签 id 无效");
        }

        List<WorkContactTagEntity> tagEntityList = this.baseMapper.selectBatchIds(Arrays.asList(tagIds.split(",")));
        Map<String, WorkContactTagEntity> tagMap = new HashMap<>(0);
        for (WorkContactTagEntity e : tagEntityList) {
            tagMap.put(e.getName(), e);
        }

        List<WorkContactTagEntity> exTagEntityList = this.list(
                new QueryWrapper<WorkContactTagEntity>()
                        .eq("corp_id", "")
                        .eq("contact_tag_group_id", groupId)
                        .in("name", tagMap.keySet())
        );

        if (!exTagEntityList.isEmpty()) {
            throw new ParamException("移动到标签组失败, 标签重复");
        }

        int corpId = AccountService.getCorpId();

        // 删除微信标签
        List<String> wxTagIds = new ArrayList<>();
        for (WorkContactTagEntity e : tagEntityList) {
            wxTagIds.add(e.getWxContactTagId());
        }
        WxApiUtils.requestDelTags(corpId, wxTagIds);

        WorkContactTagGroupEntity groupEntity = contactTagGroupService.getById(groupId);

        // 添加微信标签
        String resultJson = WxApiUtils.requestCreateTags(corpId, groupEntity.getWxGroupId(), new ArrayList<>(tagMap.keySet()));
        WxTagDTO dto = JSON.parseObject(resultJson, WxTagDTO.class);
        List<WxTagDTO.TagDTO> tagDTOList = dto.getTag();
        for (WxTagDTO.TagDTO tagDTO : tagDTOList) {
            WorkContactTagEntity tagEntity = tagMap.get(tagDTO.getName());
            tagEntity.setWxContactTagId(tagDTO.getId());
            tagEntity.setContactTagGroupId(groupId);
            this.updateById(tagEntity);
        }
    }

    @Override
    public void updateTag(Integer tagId, Integer groupId, String tagName, Integer isUpdate) {
        if (isUpdate == 1) {
            WorkContactTagEntity entity = this.baseMapper.selectById(tagId);
            if (entity != null) {
                entity.setContactTagGroupId(groupId);
                entity.setName(tagName);
                this.updateById(entity);
                WxApiUtils.requestEditTag(entity.getCorpId(), entity.getWxContactTagId(), tagName);
            } else {
                throw new ParamException("标签不存在");
            }
        }
    }

    @Override
    public void wxBackCreateTag(int corpId, String wxTagId) {
        String resultJson = WxApiUtils.requestGetTagDetail(corpId, wxTagId);
        WxTagDTO dto = JSON.parseArray(resultJson, WxTagDTO.class).get(0);
        WorkContactTagGroupEntity tagGroupEntity = getContactTagGroupEntity(dto.getGroupId());

        if (tagGroupEntity == null) {
            // 原因: 创建了一个新的标签组和标签, 标签的回调早于标签组
            // 处理: 可忽略
            log.error(">>> 微信回调 -> 创建标签: 标签组不存在");
            return;
        }

        WxTagDTO.TagDTO tagDTO = dto.getTag().get(0);
        WorkContactTagEntity tagEntity = new WorkContactTagEntity();
        tagEntity.setWxContactTagId(tagDTO.getId());
        tagEntity.setCorpId(corpId);
        tagEntity.setName(tagDTO.getName());
        tagEntity.setOrder(tagDTO.getOrder());
        tagEntity.setContactTagGroupId(tagGroupEntity.getId());
        this.save(tagEntity);
    }

    private WorkContactTagGroupEntity getContactTagGroupEntity(String wxTagGroupId) {
        return contactTagGroupService.lambdaQuery()
                .eq(WorkContactTagGroupEntity::getWxGroupId, wxTagGroupId)
                .one();
    }

    @Override
    public void wxBackUpdateTag(int corpId, String wxTagId) {
        String resultJson = WxApiUtils.requestGetTagDetail(corpId, wxTagId);
        WxTagDTO dto = JSON.parseArray(resultJson, WxTagDTO.class).get(0);
        WxTagDTO.TagDTO tagDTO = dto.getTag().get(0);

        WorkContactTagEntity tagEntity = this.list(
                new QueryWrapper<WorkContactTagEntity>()
                        .eq("wx_contact_tag_id", tagDTO.getId())
        ).get(0);

        WorkContactTagGroupEntity tagGroupEntity = getContactTagGroupEntity(dto.getGroupId());

        tagEntity.setName(tagDTO.getName());
        tagEntity.setOrder(tagDTO.getOrder());
        tagEntity.setContactTagGroupId(tagGroupEntity.getId());
        this.updateById(tagEntity);
    }

    @Override
    public void wxBackDeleteTag(int corpId, String wxTagId) {
        List<WorkContactTagEntity> tagEntityList = this.list(
                new QueryWrapper<WorkContactTagEntity>()
                        .eq("wx_contact_tag_id", wxTagId)
        );

        for (WorkContactTagEntity tagEntity : tagEntityList) {
            deleteTagAndPivotByTagId(tagEntity.getId());
        }
    }

    /**
     * 删除客户标签并删除客户标签与客户关联数据
     */
    public void deleteTagAndPivotByTagId(int tagId) {
        List<Integer> contactTagPivotIdList = contactTagPivotService.lambdaQuery()
                .select(WorkContactTagPivotEntity::getId)
                .eq(WorkContactTagPivotEntity::getContactTagId, tagId)
                .list()
                .stream()
                .map(WorkContactTagPivotEntity::getId)
                .collect(Collectors.toList());
        contactTagPivotService.removeByIds(contactTagPivotIdList);

        removeById(tagId);
    }
}
