package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.dao.entity.CorpDataEntity;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.model.corp.CorpPageItemVO;

import java.util.List;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2021/3/17 2:08 下午
 * @description 企业服务类
 */
public interface ICorpService extends IService<CorpEntity> {

    List<CorpEntity> getCorpInfoByIdName(String corpIds,String corpName);

    CorpEntity getCorpInfoById(Integer corpId);

    List<CorpEntity> getCorpListById(String userId);

    boolean createCorp(CorpEntity corpEntity);

    List<CorpEntity> getCorpInfoByCorpName(String corpName);

    Integer updateCorpByCorpId(CorpEntity corpEntity);

    CorpEntity getCorpsByWxCorpId(String wxCorpId, String id);

    List<CorpEntity> getCorpIds(String clStr);

    Page<CorpPageItemVO> getCorpPageList(String corpName, RequestPage requestPage, ReqPerEnum permission);

    Map<String,Object> handleCorpDta() throws Exception;

    List<CorpEntity> getCorps(String id);

    List<CorpDataEntity> handleLineChatDta();
}
