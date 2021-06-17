package com.mochat.mochat.service.impl.medium;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.medium.MediumEntity;

import java.util.List;

public interface IMediumService extends IService<MediumEntity> {

    MediumEntity getMediumById(Integer id);
    List<MediumEntity> getMediumList(String mediumGroupId, String searchStr, Integer type, Integer page, Integer pageNo);
    String addFullPath(String content,Integer type);
    Integer createMedium(MediumEntity mediumEnyity);
    Integer deleteMedium(Integer id);
    boolean updateMediumById(MediumEntity mediumEnyity);

    Integer updateMediaByGroupId(Integer id, int i);
}
