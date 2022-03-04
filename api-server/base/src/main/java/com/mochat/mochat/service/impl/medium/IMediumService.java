package com.mochat.mochat.service.impl.medium;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.medium.MediumEntity;
import com.mochat.mochat.model.medium.MediumIndexDto;

public interface IMediumService extends IService<MediumEntity> {

    Page<MediumEntity> getMediumList(MediumIndexDto dto);

    MediumEntity getMediumById(Integer id);
    String addFullPath(String content,Integer type);
    Integer createMedium(MediumEntity mediumEnyity);
    Integer deleteMedium(Integer id);
    boolean updateMediumById(MediumEntity mediumEnyity);

    Integer updateMediaByGroupId(Integer id, int i);
}
