package com.mochat.mochat.service.impl.medium;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.medium.MediumGroupEntity;
import com.mochat.mochat.dao.mapper.medium.MediumGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/12/4 18:54
 */
@Service
public class MediumGroupServiceImpl extends ServiceImpl<MediumGroupMapper, MediumGroupEntity> implements IMediumGroupService{

    @Autowired
    private IMediumService mediumpServiceImpl;


    @Override
    public List<MediumGroupEntity> getMediumGroupsById(String ids) {
        QueryWrapper<MediumGroupEntity> mediumGroupEntity = new QueryWrapper<MediumGroupEntity>();
        mediumGroupEntity.select("id","name");
        //QueryWrapper.setEntity(new CorpEntity());
        mediumGroupEntity.in("id",ids);
        List<MediumGroupEntity>  mediumGroupList = this.baseMapper.selectList(mediumGroupEntity);
        return mediumGroupList;
    }

    @Override
    public List<MediumGroupEntity> getMediumGroupsByCorpId(Integer corpId) {
        QueryWrapper<MediumGroupEntity> mediumGroupEntity = new QueryWrapper<MediumGroupEntity>();
        mediumGroupEntity.select("id","name");
        mediumGroupEntity.eq("corp_id",corpId);
        List<MediumGroupEntity>  mediumGroupList = this.baseMapper.selectList(mediumGroupEntity);
        return mediumGroupList;
    }

    @Override
    public List<MediumGroupEntity> getMediumGroupByName(String name) {
        QueryWrapper<MediumGroupEntity> mediumGroupEntity = new QueryWrapper<MediumGroupEntity>();
        mediumGroupEntity.select("id");
        mediumGroupEntity.eq("name",name);
        List<MediumGroupEntity>  mediumGroupList = this.baseMapper.selectList(mediumGroupEntity);
        return mediumGroupList;

    }

    @Override
    @Transactional
    public Integer createMediumGroup(MediumGroupEntity mediumGroup) {
        Integer i = this.baseMapper.insert(mediumGroup);
        return i;
    }

    @Override
    public List<MediumGroupEntity> existMediumGroupByName(Integer id, String name) {
        QueryWrapper<MediumGroupEntity> mediumGroupEntity = new QueryWrapper<MediumGroupEntity>();
        mediumGroupEntity.select("id");
        mediumGroupEntity.eq("name",name);
        mediumGroupEntity.eq("id",id);
        return this.baseMapper.selectList(mediumGroupEntity);
    }

    @Override
    public Integer updateMediumGroupById(Integer id, String name, Integer corpId) {
        MediumGroupEntity mediumGroupEntity = new MediumGroupEntity();
        mediumGroupEntity.setName(name);
        mediumGroupEntity.setId(id);
        mediumGroupEntity.setCorp_id(corpId);
        return this.baseMapper.updateById(mediumGroupEntity);
    }

    @Override
    @Transactional
    public Integer deleteMediumGroup(Integer id) {
        Integer i =this.baseMapper.deleteById(id);
        Integer i1 = mediumpServiceImpl.updateMediaByGroupId(Integer.valueOf(id),0);
        return i+i1;
    }

}
