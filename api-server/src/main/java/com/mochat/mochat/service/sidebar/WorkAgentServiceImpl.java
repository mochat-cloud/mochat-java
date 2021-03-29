package com.mochat.mochat.service.sidebar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.ChatToolEntity;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.WorkAgentEntity;
import com.mochat.mochat.dao.mapper.WorkAgentMapper;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.IChatToolService;
import com.mochat.mochat.service.impl.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WorkAgentServiceImpl extends ServiceImpl<WorkAgentMapper, WorkAgentEntity> implements IWorkAgentService {

    @Autowired
    private ICorpService corpService;

    @Autowired
    private IChatToolService chatToolService;

    @Override
    public CorpEntity getCorp(Integer agentId) {
        WorkAgentEntity workAgentEntity = getById(agentId);
        if (workAgentEntity == null) {
            throw new CommonException("应用不存在");
        }
        return corpService.getById(workAgentEntity.getCorpId());
    }

    @Override
    public int getFirstAgentId(Integer corpId) {
        List<WorkAgentEntity> workAgentEntityList = lambdaQuery()
                .select(WorkAgentEntity::getId)
                .eq(WorkAgentEntity::getCorpId, corpId)
                .list();

        if (workAgentEntityList.isEmpty()) {
            throw new CommonException("当前企业未添加企业应用");
        }
        return workAgentEntityList.get(0).getId();
    }

    @Override
    public String getWxAgentById(Integer agentId) {
        WorkAgentEntity workAgentEntity = getById(agentId);
        if (workAgentEntity != null) {
            return workAgentEntity.getWxAgentId();
        }
        return null;
    }

    @Override
    public void storeAgent(String wxAgentId, String wxSecret, int type) {
        CorpEntity corpEntity = corpService.getById(AccountService.getCorpId());
        String wxCorpId = corpEntity.getWxCorpId();
        String resultJson = WxApiUtils.getAgentInfo(wxCorpId, wxAgentId, wxSecret);
        JSONObject jsonObject = JSON.parseObject(resultJson);
        int errcode = (int) jsonObject.getOrDefault("errcode", -1);
        if (errcode != 0) {
            log.error("企业微信获取应用信息失败: " + resultJson);
            throw new CommonException("应用添加失败");
        }

        WorkAgentEntity agentEntity = new WorkAgentEntity();
        agentEntity.setCorpId(corpEntity.getCorpId());
        agentEntity.setWxAgentId(wxAgentId);
        agentEntity.setWxSecret(wxSecret);
        agentEntity.setName(jsonObject.getString("name"));
        agentEntity.setSquareLogoUrl(jsonObject.getString("square_logo_url"));
        agentEntity.setDescription(jsonObject.getString("description"));
        agentEntity.setClose(jsonObject.getIntValue("close"));
        agentEntity.setRedirectDomain(jsonObject.getString("redirect_domain"));
        agentEntity.setReportLocationFlag(jsonObject.getIntValue("report_location_flag"));
        agentEntity.setIsReportenter(jsonObject.getIntValue("isreportenter"));
        agentEntity.setHomeUrl(jsonObject.getString("home_url"));
        agentEntity.setType(type);
        agentEntity.insert();
    }

    @Override
    public List<Map<String, Object>> getChatTools() {
        List<ChatToolEntity> chatToolEntityList = chatToolService.lambdaQuery()
                .eq(ChatToolEntity::getPageFlag, "customer")
                .or()
                .eq(ChatToolEntity::getPageFlag, "mediumGroup")
                .list();

        List<WorkAgentEntity> agentEntityList = lambdaQuery()
                .eq(WorkAgentEntity::getCorpId, AccountService.getCorpId())
                .eq(WorkAgentEntity::getClose, 0)
                .list();

        List<Map<String, Object>> voList = new ArrayList<>();
        for (WorkAgentEntity entity : agentEntityList) {
            Map<String, Object> vo = new HashMap<>();
            vo.put("id", entity.getId());
            vo.put("name", entity.getName());
            vo.put("squareLogoUrl", entity.getSquareLogoUrl());
            vo.put("chatTools", chatToolEntityList);
            voList.add(vo);
        }

        return voList;
    }
}
