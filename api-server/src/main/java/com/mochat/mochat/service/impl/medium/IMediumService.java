package com.mochat.mochat.service.impl.medium;

import com.mochat.mochat.dao.entity.medium.MediumEnyity;

import java.util.List;

public interface IMediumService {

    MediumEnyity getMediumById(Integer id);
    List<MediumEnyity> getMediumList(String mediumGroupId,String searchStr,Integer type,Integer page,Integer pageNo);
    String addFullPath(String content,Integer type);
    Integer createMedium(MediumEnyity mediumEnyity);
    Integer deleteMedium(Integer id);
    boolean updateMediumById(MediumEnyity mediumEnyity);

    Integer updateMediaByGroupId(Integer id, int i);
}
