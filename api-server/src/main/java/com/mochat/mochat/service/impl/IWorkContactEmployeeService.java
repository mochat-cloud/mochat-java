package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.dao.entity.WorkContactEmployeeEntity;
import com.mochat.mochat.model.channel.ReqChannelCodeStatisticsDTO;
import com.mochat.mochat.model.channel.ReqChannelCodeStatisticsIndexDTO;
import com.mochat.mochat.model.channel.RespChannelCodeStatisticsItemVO;
import com.mochat.mochat.model.channel.RespChannelCodeStatisticsVO;
import com.mochat.mochat.model.workcontact.LossContact;

import java.util.Date;
import java.util.List;

public interface IWorkContactEmployeeService extends IService<WorkContactEmployeeEntity> {
    WorkContactEmployeeEntity getWorkContactEmployeeInfo(Integer corpId, Integer empId, Integer contactId,Integer id);
    List<Integer> getBelongToEmployeeId(Integer contactId, Integer corp_id);
    List<Integer> getEmployeeToContact(Integer corpId,Integer empId);
    LossContact getEmployeeLossContactId(Integer corpId, List<Integer> empId, Integer page, Integer perPage);
    boolean updateRemarkOrDescription(Integer corpId,Integer empId,Integer contactId,String remark,String description);
    boolean insertAllContactEmployee(List<WorkContactEmployeeEntity> list);
    boolean updateContactEmployee(WorkContactEmployeeEntity contactEmployeeEntity);
    boolean deleteContactEmployee(Integer corpId,Integer empId,Integer contactId);

    /**
     * @author: yangpengwei
     * @time: 2021/2/23 4:08 下午
     * @description 获取渠道码 - 统计分页数据
     */
    Page<RespChannelCodeStatisticsItemVO> getStatisticsOfPage(ReqChannelCodeStatisticsIndexDTO req);
    
    /**
     * @author: yangpengwei
     * @time: 2021/2/25 11:30 上午
     * @description 渠道码 - 统计折线图
     */
    RespChannelCodeStatisticsVO getStatistics(ReqChannelCodeStatisticsDTO req);

    List<WorkContactEmployeeEntity> countWorkContactEmployeesByCorpId(Integer corpId, int code);

    Integer getCountOfContactByCorpIdStartTimeEndTime(Integer corpId, String startTime, String endTime);

    Integer getCountOfLossContactByCorpIdStartTimeEndTime(Integer corpId, String startTime, String endTime);
}
