package com.mochat.mochat.service.wm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.util.wm.WorkMsgHelper;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgEntity;
import com.mochat.mochat.dao.mapper.corp.CorpMapper;
import com.mochat.mochat.dao.mapper.wm.WorkMsgMapper;
import com.mochat.mochat.job.WorkMsgBackUpUtil;
import com.mochat.mochat.model.wm.CorpMsgTO;
import com.mochat.mochat.service.impl.IWorkMsgConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/11/25 5:03 下午
 * @description 会话内容存档业务实现
 */
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class WorkMsgServiceImpl extends ServiceImpl<WorkMsgMapper, WorkMsgEntity> implements IWorkMsgService {

    private Logger logger = LoggerFactory.getLogger(WorkMsgServiceImpl.class);

    @Autowired
    private IWorkMsgConfigService msgConfigService;

    @Autowired
    private CorpMapper corpMapper;

    /**
     * 对所有开启会话内容存档的企业进行数据同步
     */
    @Override
    public void onAsyncMsg() {
        List<WorkMsgConfigEntity> list = msgConfigService.getAllAble();
        CorpMsgTO corpMsgTO;
        for (WorkMsgConfigEntity entity : list) {
            int corpId = entity.getCorpId();
            String wxCorpId = getWxCorpId(corpId);
            if (wxCorpId.isEmpty()) {
                return;
            }
            corpMsgTO = new CorpMsgTO();
            corpMsgTO.setCorpId(corpId);
            corpMsgTO.setWxCorpId(wxCorpId);
            corpMsgTO.setChatRsaKey(entity.getChatRsaKey());
            corpMsgTO.setChatSecret(entity.getChatSecret());
            doInsertMsg(corpMsgTO);
        }
    }

    /**
     * 同步会话内容数据
     *
     * @param corpId 企业 id
     * @return 是否同步成功
     */
    @Override
    public boolean onAsyncMsg(int corpId) {
        WorkMsgConfigEntity entity = msgConfigService.getByCorpId(corpId);
        if (null != entity && entity.getChatApplyStatus() == 4 && entity.getChatStatus() == 1) {
            String wxCorpId = getWxCorpId(corpId);
            if (wxCorpId.isEmpty()) {
                return false;
            }
            CorpMsgTO corpMsgTO = new CorpMsgTO();
            corpMsgTO.setCorpId(corpId);
            corpMsgTO.setWxCorpId(wxCorpId);
            corpMsgTO.setChatRsaKey(entity.getChatRsaKey());
            corpMsgTO.setChatSecret(entity.getChatSecret());
            doInsertMsg(corpMsgTO);
            return true;
        }
        return false;
    }

    @Override
    public List<WorkMsgEntity> getWorkMessagesByMsgId(List<WorkMsgEntity> workMsgEntityList) {
        List<WorkMsgEntity> workMsgEntities = new ArrayList<>();
        QueryWrapper<WorkMsgEntity> workMsgEntityWrapper = new QueryWrapper<>();
        for (WorkMsgEntity workMsgEntity:
                workMsgEntityList) {
            workMsgEntityWrapper.eq("msg_id", workMsgEntity.getMsgId());
            workMsgEntities.add(this.baseMapper.selectOne(workMsgEntityWrapper));
        }
        return workMsgEntities;
    }


    @Override
    public List<WorkMsgEntity> getWorkMessagesRangeByCorpIdWxRoomId(String corpId, String wxRoomId, Integer id, int i, String s) {
        if(i == 0){
            return this.baseMapper.selectList(new QueryWrapper<WorkMsgEntity>().select(s).eq("corp_id",corpId).eq("wx_room_id",wxRoomId).orderByDesc("id").last("limit 0,10"));
        }
        return this.baseMapper.selectList(new QueryWrapper<WorkMsgEntity>().select(s).eq("corp_id",corpId).eq("wx_room_id",wxRoomId).orderByAsc("id").last("limit 0,10"));
    }



    @Override
    public List<WorkMsgEntity> getWorkMessagesRangeByCorpId(String corpId, String from, String tolist, Integer id, int i, String s) {
        if(i == 0){
            return this.baseMapper.selectList(new QueryWrapper<WorkMsgEntity>().select(s).eq("from",from).eq("tolist",tolist).orderByDesc("id").last("limit 0,10"));
        }
        return this.baseMapper.selectList(new QueryWrapper<WorkMsgEntity>().select(s).eq("from",from).eq("tolist",tolist).orderByAsc("id").last("limit 0,10"));
    }

    /**
     * 获取最后一条信息的 seq
     *
     * @param corpId 企业 id
     * @return seq
     */
    private int findLastSeq(long corpId) {
        Integer seq = baseMapper.selectLastSeq(WorkMsgHelper.getTableName(corpId), corpId);
        if (null != seq) {
            return seq;
        }
        return 0;
    }

    private boolean doInsertMsg(CorpMsgTO entity) {
        try {
            boolean repeat = WorkMsgBackUpUtil.insertMsg(entity, findLastSeq(entity.getCorpId()));
            if (repeat) {
                doInsertMsg(entity);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getWxCorpId(int corpId) {
        CorpEntity corpEntity = corpMapper.selectById(corpId);
        if (corpEntity == null) {
            return "";
        }
        return corpEntity.getWxCorpId();
    }
}