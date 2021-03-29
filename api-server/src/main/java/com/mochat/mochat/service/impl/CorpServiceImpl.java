package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;
import com.mochat.mochat.dao.mapper.corp.CorpMapper;
import com.mochat.mochat.model.corp.CorpPageItemVO;
import com.mochat.mochat.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/11/23 20:19
 */
@Service
@Transactional
public class CorpServiceImpl extends ServiceImpl<CorpMapper, CorpEntity> implements ICorpService {

    @Autowired
    private IWorkMsgConfigService msgConfigService;

    /**
     * @description: 根据corpId找对象
     * @return:
     * @author: Huayu
     * @time: 2020/11/25 9:25
     */
    @Override
    public List<CorpEntity> getCorpInfoByIdName(String corpIds, String name) {
        List<String> cIds = Arrays.asList(corpIds.split(","));
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("id as corpId", "name as corpName", "wx_corpid as wxCorpId", "created_at as createdAt");
        QueryWrapper.in("id", cIds);
        if (!name.equals("")) {
            QueryWrapper.eq("name", name);
        }
        return this.baseMapper.selectList(QueryWrapper);
    }

    @Override
    public CorpEntity getCorpInfoById(Integer corpId) {
        return this.baseMapper.selectById(corpId);
    }

    @Override
    public List<CorpEntity> getCorpListById(String userId) {
        // Page<CorpEntity> indexDataPage = new Page<CorpEntity>;
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("id as corpId", "name as corpName");
        //QueryWrapper.setEntity(new CorpEntity());
        QueryWrapper.in("id", userId);
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList;
    }

    @Override
    @Transactional
    public boolean createCorp(CorpEntity corpEntity) {
        boolean flag = this.save(corpEntity);
        return flag;
    }

    @Override
    public List<CorpEntity> getCorpInfoByCorpName(String corpName) {
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("id as corpId");
        //QueryWrapper.setEntity(new CorpEntity());
        QueryWrapper.eq("name", corpName);
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList;
    }

    @Override
    @Transactional
    public Integer updateCorpByCorpId(CorpEntity corpEntity) {
        Integer i = this.baseMapper.updateById(corpEntity);
        return i;
    }

    @Override
    public CorpEntity getCorpsByWxCorpId(String wxCorpId, String id) {
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select(id);
        //QueryWrapper.setEntity(new CorpEntity());
        QueryWrapper.eq("wx_corpid", wxCorpId);
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList.get(0);
    }

    @Override
    public List<CorpEntity> getCorpIds(String clStr){
        QueryWrapper<CorpEntity> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select(clStr+ " as corpId");
        List<CorpEntity> corpList = this.baseMapper.selectList(QueryWrapper);
        return corpList;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/22 4:14 下午
     * @description
     * @info 因二期权限管理需求, 本人只能查看本公司的信息, 所属其他公司信息查看需要切换公司
     */
    @Override
    public Page<CorpPageItemVO> getCorpPageList(String corpName, RequestPage requestPage, ReqPerEnum permission) {
        Page<CorpEntity> corpPage = ApiRespUtils.initPage(requestPage);
        LambdaQueryChainWrapper<CorpEntity> wrapper = lambdaQuery()
                .eq(CorpEntity::getCorpId, AccountService.getCorpId());
        if (Objects.nonNull(corpName) && !corpName.isEmpty()) {
            wrapper.eq(CorpEntity::getCorpName, corpName);
        }
        wrapper.page(corpPage);

        List<CorpPageItemVO> voList = new ArrayList<>();
        for (CorpEntity corpEntity : corpPage.getRecords()) {
            WorkMsgConfigEntity workMsgConfigEntity = msgConfigService.getByCorpId(corpEntity.getCorpId());
            CorpPageItemVO vo = new CorpPageItemVO();
            vo.setCorpId(corpEntity.getCorpId());
            vo.setCorpName(corpEntity.getCorpName());
            vo.setWxCorpId(corpEntity.getWxCorpId());
            vo.setCreatedAt(DateUtils.formatS1(corpEntity.getCreatedAt().getTime()));
            vo.setChatApplyStatus(workMsgConfigEntity.getChatApplyStatus());
            vo.setChatStatus(workMsgConfigEntity.getChatStatus());
            vo.setMessageCreatedAt(DateUtils.formatS1(workMsgConfigEntity.getCreatedAt().getTime()));
            voList.add(vo);
        }
        return ApiRespUtils.transPage(corpPage, voList);
    }
}
