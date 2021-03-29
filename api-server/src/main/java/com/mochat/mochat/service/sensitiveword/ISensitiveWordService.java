package com.mochat.mochat.service.sensitiveword;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordEntity;
import com.mochat.mochat.model.sensitiveword.ReqSensitiveWordIndex;

import java.util.List;
import java.util.Map;

/**
 * @author: Huayu
 * @time: 2021/01/27 11:09 上午
 * @description 敏感词库管理
 */
public interface ISensitiveWordService {

    Integer deleteSensitiveWord(Integer id);

    Page<SensitiveWordEntity> getSensitiveWordList(ReqSensitiveWordIndex sensitiveWordIndex, ReqPerEnum permission);

    List<SensitiveWordEntity> getSensitiveWordList(Integer intelligentGroupId);

    boolean createSensitiveWords(Map<String, Object> mapData);

    boolean createSensitiveWord(Map<String, Object> mapData);

    boolean nameIsUnique(String name);

    Integer updateSensitiveWordById(Integer sensitiveWordId, String clStr, String clStrVal);

    List<SensitiveWordEntity> getSensitiveWordsByGroupId(Integer groupId);

    List<SensitiveWordEntity> getSensitiveWordsByCorpIdStatus(String[] corpIdArr, int i, String s);

    void updateSensitiveWordById(Object k, Integer employeeNum, Integer contactNum);
}
