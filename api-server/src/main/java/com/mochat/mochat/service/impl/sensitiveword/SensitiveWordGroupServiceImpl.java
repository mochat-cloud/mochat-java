package com.mochat.mochat.service.impl.sensitiveword;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordGroupEntity;
import com.mochat.mochat.dao.mapper.sensitiveword.SensitiveWordGroupMapper;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description:敏感词库分组
 * @author: Huayu
 * @time: 2021/1/28 14:27
 */
@Service
public class SensitiveWordGroupServiceImpl  extends ServiceImpl<SensitiveWordGroupMapper, SensitiveWordGroupEntity> implements ISensitiveWordGroupService {


    /**
     *
     *
     * @description:获取当前企业的敏感词下拉列表
     * @author: Huayu
     * @time: 2021/1/28 14:31
     */
    @Override
    public List<SensitiveWordGroupEntity> getSensitiveWordGroupsByCorpId(Integer corpId) {
        QueryWrapper<SensitiveWordGroupEntity> sensitiveWordGroupEntityQueryWrapper = new QueryWrapper();
        sensitiveWordGroupEntityQueryWrapper.select("id","name");
        sensitiveWordGroupEntityQueryWrapper.eq("corp_id", AccountService.getCorpId());
        List<SensitiveWordGroupEntity> sensitiveWordGroupEntityList = this.baseMapper.selectList(sensitiveWordGroupEntityQueryWrapper);
        return sensitiveWordGroupEntityList;
    }


    /**
     *
     *
     * @description:验证分组名称是否存在
     * @author: Huayu
     * @time: 2021/1/28 16:07
     */
    @Override
    public SensitiveWordGroupEntity getSensitiveWordGroupByNameCorpId(String name, Integer id, Integer corpId) {
        QueryWrapper<SensitiveWordGroupEntity>  sensitiveWordGroupEntityQueryWrapper = new QueryWrapper();
        sensitiveWordGroupEntityQueryWrapper.select("id");
        sensitiveWordGroupEntityQueryWrapper.eq("name",name);
        sensitiveWordGroupEntityQueryWrapper.eq("id",id);
        sensitiveWordGroupEntityQueryWrapper.eq("corp_id",corpId);
        SensitiveWordGroupEntity sensitiveWordGroupEntity = this.baseMapper.selectOne(sensitiveWordGroupEntityQueryWrapper);
        return sensitiveWordGroupEntity;
    }


    /**
     *
     *
     * @description:更新敏感词分组名称
     * @author: Huayu
     * @time: 2021/1/28 16:07
     */
    @Override
    public Integer updateSensitiveWordGroupById(Integer id, String name) {
        UpdateWrapper<SensitiveWordGroupEntity> sensitiveWordGroupEntityUpdateWrapper = new UpdateWrapper();
        sensitiveWordGroupEntityUpdateWrapper.set("name",name);
        sensitiveWordGroupEntityUpdateWrapper.eq("id",id);
        SensitiveWordGroupEntity sensitiveWordGroupEntity = new SensitiveWordGroupEntity();
        sensitiveWordGroupEntity.setId(id);
        sensitiveWordGroupEntity.setName(name);
        Integer i = this.baseMapper.update(sensitiveWordGroupEntity,sensitiveWordGroupEntityUpdateWrapper);
        return i;
    }

    /**
     *
     *
     * @description:添加敏感词分组名称
     * @author: Huayu
     * @time: 2021/1/28 16:07
     */
    @Override
    public boolean createSensitiveWordGroups(Map<String, Object> mapData) {
        String nameStr = mapData.get("name").toString();
        String[] nameArrStr = nameStr.split(",");
        for (String name:
                nameArrStr) {
            boolean i = nameIsUnique(name);
            //创建敏感词
            createSensitiveWordGroup(name);
        }
        return true;
    }

    @Override
    public boolean createSensitiveWordGroup(Map<String, Object> mapData) {
        String name = mapData.get("name").toString();
        createSensitiveWordGroup(name);
        return true;
    }

    private boolean createSensitiveWordGroup(String name){
        SensitiveWordGroupEntity sensitiveWordGroupEntity = new SensitiveWordGroupEntity();
        sensitiveWordGroupEntity.setName(name);
        sensitiveWordGroupEntity.setCorpId(AccountService.getCorpId());
        sensitiveWordGroupEntity.setUserId(AccountService.getUserId());
        sensitiveWordGroupEntity.setEmployeeId(AccountService.getEmpId());
        Integer i = this.baseMapper.insert(sensitiveWordGroupEntity);
        if(i < 1){
            throw new ParamException(100014,"敏感词分组创建失败");
        }
        return true;
    }

    private boolean nameIsUnique(String name) {
        QueryWrapper<SensitiveWordGroupEntity> sensitiveWordGroupEntityQueryWrapper = new QueryWrapper();
        sensitiveWordGroupEntityQueryWrapper.eq("name",name);
        sensitiveWordGroupEntityQueryWrapper.eq("corp_id",AccountService.getCorpId());
        SensitiveWordGroupEntity sensitiveWordGroupEntity = this.baseMapper.selectOne(sensitiveWordGroupEntityQueryWrapper);
        if(sensitiveWordGroupEntity != null){
            throw new ParamException(100013,"该敏感词分组名称已存在");
        }
        return true;
    }
}
