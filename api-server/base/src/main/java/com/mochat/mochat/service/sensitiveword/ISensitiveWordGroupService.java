package com.mochat.mochat.service.sensitiveword;

import com.mochat.mochat.dao.entity.sensitive.SensitiveWordGroupEntity;

import java.util.List;
import java.util.Map;


public interface ISensitiveWordGroupService {
    List<SensitiveWordGroupEntity> getSensitiveWordGroupsByCorpId(Integer corpId);

    SensitiveWordGroupEntity getSensitiveWordGroupByNameCorpId(String name, Integer id, Integer corpId);

    Integer updateSensitiveWordGroupById(Integer id, String name);

    boolean createSensitiveWordGroups(Map<String, Object> mapData);

    boolean createSensitiveWordGroup(Map<String, Object> mapData);
}
