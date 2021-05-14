package com.mochat.mochat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.WorkContactTagPivotEntity;
import com.mochat.mochat.model.workcontacttag.ContactTagId;

import java.util.List;
import java.util.Map;

public interface IWorkContactTagPivotService extends IService<WorkContactTagPivotEntity> {
    List<Integer> getContactTapId(Integer empId, Integer contactId);

    List<ContactTagId> getContactTapId(Integer empId);

    List<ContactTagId> getContactTapId(List<Integer> contactIds);

    Map<String, Integer> getContactTapIdOrWxTagId(Integer empId, Integer contactId);

    boolean updateContactTagPivot(Integer empId, Integer contactId, List<Integer> tagIds);

    public boolean updateContactTagPivot(List<WorkContactTagPivotEntity> list);

    boolean insertMultipleTagPivot(List<Integer> tagIds, Integer type, Integer empId, Integer contactId);

    boolean deleteMultipleTagPivot(List<Integer> tagIds, Integer empId, Integer contactId);

    boolean deleteContactTagPivot(Integer empId, Integer contactId);

    boolean insertAllTagPivot(List<WorkContactTagPivotEntity> contactTagPivots);

    boolean insertTagPivotOfWX_TagId(JSONArray tags, JSONArray appendTags, Integer empId, Integer contactId);
}
