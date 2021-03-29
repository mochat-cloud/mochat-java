package com.mochat.mochat.dao.mapper.medium;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;;
import com.mochat.mochat.dao.entity.medium.MediumEnyity;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/12/4 18:57
 */
public interface MediumMapper extends BaseMapper<MediumEnyity> {
    List<MediumEnyity> getMediumList(Map<String,Object> map);

    Integer updateMediaByGroupId(MediumEnyity mediumEnyity);

    List<MediumEnyity> getAllMediumList(Map<String, Object> map);
}
