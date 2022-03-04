package com.mochat.mochat.service.channel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.channel.ChannelCodeGroupEntity;
import com.mochat.mochat.dao.mapper.channel.ChannelCodeGroupMapper;
import com.mochat.mochat.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/2/22 5:06 下午
 * @description 渠道码分组服务实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class ChannelCodeServiceGroupImpl extends ServiceImpl<ChannelCodeGroupMapper, ChannelCodeGroupEntity> implements IChannelCodeGroupService {

    @Override
    public void saveByNames(List<String> nameList) {
        if (null == nameList || nameList.isEmpty()) {
            return;
        }

        int corpId = AccountService.getCorpId();
        int count = Math.toIntExact(lambdaQuery()
                .eq(ChannelCodeGroupEntity::getCorpId, corpId)
                .in(ChannelCodeGroupEntity::getName, nameList)
                .count());
        if (count > 0) {
            throw new ParamException("分组名已存在");
        }

        List<ChannelCodeGroupEntity> codeGroupEntityList = new ArrayList<>();
        for (String name : nameList) {
            codeGroupEntityList.add(ChannelCodeGroupEntity.getInstance(name, corpId));
        }
        saveBatch(codeGroupEntityList);
    }

    @Override
    public void updateNameByGroupId(Integer groupId, String name) {
        QueryWrapper<ChannelCodeGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        int count = (int) count(queryWrapper);
        if (count > 0) {
            throw new ParamException("分组名已存在");
        }

        ChannelCodeGroupEntity entity = getById(groupId);
        if (Objects.isNull(entity)) {
            throw new ParamException("修改失败，分组不存在");
        }

        entity = new ChannelCodeGroupEntity();
        entity.setId(groupId);
        entity.setName(name);
        entity.setCorpId(AccountService.getCorpId());
        entity.updateById();
    }

    @Override
    public List<ChannelCodeGroupEntity> getListByCorpId(Integer corpId) {
        QueryWrapper<ChannelCodeGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("corp_id", corpId);
        return list(queryWrapper);
    }
}
