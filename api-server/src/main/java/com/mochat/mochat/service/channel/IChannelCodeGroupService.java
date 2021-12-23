package com.mochat.mochat.service.channel;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.channel.ChannelCodeGroupEntity;

import java.util.List;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/2/22 4:59 下午
 * @description 渠道码服务
 */
public interface IChannelCodeGroupService extends IService<ChannelCodeGroupEntity> {

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 5:19 下午
     * @description 创建渠道码分组，通过 names
     *
     * @param nameList [分组 1，分组 2]
     */
    void saveByNames(List<String> nameList);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 5:39 下午
     * @description 更新渠道码分组名，通过渠道码分组 id
     *
     * @param groupId 渠道码分组 id
     * @param name 新渠道码分组名
     */
    void updateNameByGroupId(Integer groupId, String name);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 5:56 下午
     * @description 获取渠道码分组列表，通过企业 id
     *
     * @param corpId 企业 id
     */
    List<ChannelCodeGroupEntity> getListByCorpId(Integer corpId);
}
