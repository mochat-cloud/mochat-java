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

    /**
     * @author: yangpengwei
     * @time: 2021/5/19 4:05 下午
     * @description 根据登录用户 id 获取用户关联企业列表
     */
    List<CorpEntity> listByLoginUserId(Integer loginUserId);

    /**
     * @author: yangpengwei
     * @time: 2021/5/19 4:05 下午
     * @description 根据登录用户 id 和企业名获取用户关联企业列表
     */
    List<CorpEntity> listByLoginUserIdAndCorpName(Integer loginUserId, String corpName);

    /**
     * @author: yangpengwei
     * @time: 2021/5/19 4:42 下午
     * @description 根据企业 id 获取企业微信 id
     */
    String getWxCorpIdById(Integer corpId);

    boolean createCorp(CorpEntity corpEntity);

    List<CorpEntity> getCorpInfoByCorpName(String corpName);

    Integer updateCorpByCorpId(CorpEntity corpEntity);

    CorpEntity getCorpsByWxCorpId(String wxCorpId, String id);

    List<CorpEntity> getCorpIds(String clStr);

    Page<CorpPageItemVO> getCorpPageList(String corpName, RequestPage requestPage, ReqPerEnum permission);

    Map<String, Object> handleCorpDta() throws Exception;

    List<CorpEntity> getCorps(String id);

    List<CorpDataEntity> handleLineChatDta();
}
