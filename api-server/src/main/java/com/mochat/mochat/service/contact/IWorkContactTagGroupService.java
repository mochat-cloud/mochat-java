package com.mochat.mochat.service.contact;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.dao.entity.WorkContactTagGroupEntity;
import com.mochat.mochat.model.workcontact.ContactTagGroupIndexVO;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/12/14 5:32 下午
 * @description 客户标签组 service
 */
public interface IWorkContactTagGroupService extends IService<WorkContactTagGroupEntity> {

    /**
     * 获取标签组列表
     */
    List<ContactTagGroupIndexVO> getGroupList(RequestPage req);

    /**
     * 获取标签组详情
     */
    ContactTagGroupIndexVO getGroupDetail(Integer groupTagId);

    /**
     * 删除标签组
     */
    void deleteGroup(Integer groupTagId);

    /**
     * 创建标签组
     */
    void createGroup(String groupTagName);

    /**
     * 更新标签组
     */
    void updateGroup(Integer groupTagId, String groupTagName, Integer isUpdate);

    void wxBackCreateTagGroup(int corpId, String wxTagGroupId);
    void wxBackUpdateTagGroup(int corpId, String wxTagGroupId);
    void wxBackDeleteTagGroup(int corpId, String wxTagGroupId);

}
