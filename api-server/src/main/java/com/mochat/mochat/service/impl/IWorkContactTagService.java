package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.dao.entity.WorkContactTagEntity;
import com.mochat.mochat.model.workcontact.ContactTagDetailVO;
import com.mochat.mochat.model.workcontact.ContactTagVO;
import com.mochat.mochat.model.workcontacttag.GetContactTapModel;
import com.mochat.mochat.model.workcontacttag.GetEmployeeTagModel;

import java.util.List;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2021/3/2 3:15 下午
 * @description 客户标签服务
 */
public interface IWorkContactTagService extends IService<WorkContactTagEntity> {

    List<GetContactTapModel> getContactTapName(Integer empId, Integer contactId);

    List<GetEmployeeTagModel> getEmployeeTapName(Integer empId);

    List<GetEmployeeTagModel> getEmployeeTapName(List<Integer> empIds);

    List<String> getWxContactTagId(List<Integer> tagIds);

    Map<String, Integer> getContactTagId(List<String> wx_tagId);

    Map<String, Integer> getContactTagIds(List<Integer> tagId);

    Map<String, Integer> getContactTagId(Integer corpId);

    /**
     * 获取标签列表
     */
    Map<String, Object> getTagList(Integer tagGroupId, RequestPage requestPage);

    /**
     * 同步标签
     */
    void synContactTag(int corpId);

    /**
     * 所有标签
     */
    List<ContactTagVO> getAllTag(Integer groupTagId);

    /**
     * 获取标签详情
     */
    ContactTagDetailVO getTagDetail(Integer tagId);

    /**
     * 移动标签
     */
    void moveTags(String tagIds, Integer groupId);

    /**
     * 创建标签
     */
    void createTag(Integer groupTagId, String tagNames);

    /**
     * 更新标签
     */
    void updateTag(Integer tagId, Integer groupId, String tagName, Integer isUpdate);

    /**
     * 删除标签
     */
    void deleteTag(String tagIds);

    void deleteTagByGroupId(int corpId, int tagGroupId, boolean callWx);

    void wxBackCreateTag(int corpId, String wxTagId);

    void wxBackUpdateTag(int corpId, String wxTagId);

    void wxBackDeleteTag(int corpId, String wxTagId);

}
