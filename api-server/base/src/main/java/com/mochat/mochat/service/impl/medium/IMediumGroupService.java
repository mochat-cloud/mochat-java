package com.mochat.mochat.service.impl.medium;

import com.mochat.mochat.dao.entity.medium.MediumGroupEntity;

import java.util.List;

public interface IMediumGroupService {

    List<MediumGroupEntity> getMediumGroupsById(String ids);

    List<MediumGroupEntity> getMediumGroupsByCorpId(Integer corpId);

    List<MediumGroupEntity> getMediumGroupByName(String name);

    Integer createMediumGroup(MediumGroupEntity mediumGroup);

    List<MediumGroupEntity> existMediumGroupByName(Integer id, String name);

    Integer updateMediumGroupById(Integer id, String name, Integer corpId);

    Integer deleteMediumGroup(Integer valueOf);
}
