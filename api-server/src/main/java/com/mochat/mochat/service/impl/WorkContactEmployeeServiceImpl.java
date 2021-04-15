package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.channel.ReqStatisticsIndexEnum;
import com.mochat.mochat.common.em.workcontact.AddWayEnum;
import com.mochat.mochat.common.em.workcontact.EventEnum;
import com.mochat.mochat.common.em.workcontactemployee.Status;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.WorkContactEmployeeEntity;
import com.mochat.mochat.dao.mapper.WorkContactEmployeeMapper;
import com.mochat.mochat.model.channel.ReqChannelCodeStatisticsDTO;
import com.mochat.mochat.model.channel.ReqChannelCodeStatisticsIndexDTO;
import com.mochat.mochat.model.channel.RespChannelCodeStatisticsItemVO;
import com.mochat.mochat.model.channel.RespChannelCodeStatisticsVO;
import com.mochat.mochat.model.workcontact.LossContact;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.contact.IExternalContactService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaojinjian
 * @ClassName 客户
 * @Description
 * @createTime 2020/12/3 11:57
 */
@Service
public class WorkContactEmployeeServiceImpl extends ServiceImpl<WorkContactEmployeeMapper, WorkContactEmployeeEntity> implements IWorkContactEmployeeService {
    @Autowired
    private IExternalContactService externalContactService;

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private IContactService contactService;

    /**
     * @description 获取员工下客户
     * @author zhaojinjian
     * @createTime 2020/12/3 14:26
     */
    @Override
    public WorkContactEmployeeEntity getWorkContactEmployeeInfo(Integer corpId, Integer empId, Integer contactId) {
        WorkContactEmployeeEntity workContactEmployee = new WorkContactEmployeeEntity();
        workContactEmployee.setContactId(contactId);
        workContactEmployee.setEmployeeId(empId);
        workContactEmployee.setCorpId(corpId);
        List<WorkContactEmployeeEntity> contactEmployeeEntityList = this.baseMapper.selectList(new QueryWrapper<>(workContactEmployee));
        if (contactEmployeeEntityList.isEmpty()) {
            throw new ParamException("未查询到客户详情");
        }

        return contactEmployeeEntityList.get(0);
    }

    /**
     * @description 获取客户所有归属的员工Id
     * @author zhaojinjian
     * @createTime 2020/12/12 14:12
     */
    @Override
    public List<Integer> getBelongToEmployeeId(Integer contactId, Integer corp_id) {
        QueryWrapper<WorkContactEmployeeEntity> contactEmployeeWrapper = new QueryWrapper<>();
        contactEmployeeWrapper.select("employee_id");
        contactEmployeeWrapper.eq("contact_id", contactId);
        contactEmployeeWrapper.eq("corp_id", corp_id);
        contactEmployeeWrapper.isNull("deleted_at");
        List<WorkContactEmployeeEntity> list = this.baseMapper.selectList(contactEmployeeWrapper);
        return list.stream().map(WorkContactEmployeeEntity::getEmployeeId).collect(Collectors.toList());
    }

    /**
     * @description 获取员工下所有客户Id
     * @author zhaojinjian
     * @createTime 2020/12/12 12:00
     */
    @Override
    public List<Integer> getEmployeeToContact(Integer corpId, Integer empId) {
        QueryWrapper<WorkContactEmployeeEntity> contactEmployeeWrapper = new QueryWrapper<>();
        contactEmployeeWrapper.select("contact_id");
        contactEmployeeWrapper.eq("employee_id", empId);
        contactEmployeeWrapper.eq("corp_id", corpId);
        contactEmployeeWrapper.isNull("deleted_at");
        List<WorkContactEmployeeEntity> list = this.list(contactEmployeeWrapper);
        if (list != null) {
            return list.stream().map(WorkContactEmployeeEntity::getContactId).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * @description 获取员工下所有客户Id
     * @author zhaojinjian
     * @createTime 2020/12/12 12:00
     */
    @Override
    public LossContact getEmployeeLossContactId(Integer corpId, List<Integer> empId, Integer page, Integer perPage) {
        QueryWrapper<WorkContactEmployeeEntity> contactEmployeeWrapper = new QueryWrapper<>();
        contactEmployeeWrapper.select("id,contact_id,employee_id");
        contactEmployeeWrapper.in("employee_id", empId);
        contactEmployeeWrapper.eq("corp_id", corpId);
        contactEmployeeWrapper.isNotNull("deleted_at");
        Page pages = new Page(page, perPage);
        Page<WorkContactEmployeeEntity> contactEmployeePage = this.baseMapper.selectPage(pages, contactEmployeeWrapper);
        List<WorkContactEmployeeEntity> list = contactEmployeePage.getRecords();
        if (list != null) {
            LossContact lossContact = new LossContact();
            lossContact.setPerPage(perPage);
            lossContact.setTotal(contactEmployeePage.getTotal());
            lossContact.setTotalPage(new Double(Math.ceil(contactEmployeePage.getTotal() / perPage)).longValue());
            lossContact.setEmpIdAndContactId(list.stream().collect(Collectors.toMap(h -> h.getEmployeeId() + "-" + h.getId(), h -> h.getContactId())));
            return lossContact;
        }
        return null;
    }

    /**
     * @description 修改备注和描述
     * @author zhaojinjian
     * @createTime 2020/12/6 11:27
     */
    @Override
    public boolean updateRemarkOrDescription(Integer corpId, Integer empId, Integer contactId, String remark, String description) {
        if (remark.isEmpty() && description.isEmpty()) {
            //描述和备注必须有一个存在值
            return false;
        }
        //修改企业微信客户备注信息
        externalContactService.updateRemark(empId, corpId, contactId, remark, description);

        QueryWrapper<WorkContactEmployeeEntity> workContactEmployeeWrapper = new QueryWrapper<>();
        workContactEmployeeWrapper.eq("corp_id", corpId);
        workContactEmployeeWrapper.eq("employee_id", empId);
        workContactEmployeeWrapper.eq("contact_id", contactId);
        WorkContactEmployeeEntity workContactEmployee = new WorkContactEmployeeEntity();
        String content = EventEnum.INFO.getMsg() + ": ";
        if (description != null && description.length() > 0) {
            workContactEmployee.setDescription(description);
            content += "描述";
        }
        if (remark != null && remark.length() > 0) {
            workContactEmployee.setRemark(remark);
            content += "备注";
        }
        boolean result = this.baseMapper.update(workContactEmployee, workContactEmployeeWrapper) == 1;
        if (result) {
            contactService.saveTrack(empId, contactId, EventEnum.INFO, content);
        }
        return result;
    }

    @Override
    public boolean insertAllContactEmployee(List<WorkContactEmployeeEntity> list) {
        boolean flag = false;
        List<WorkContactEmployeeEntity> contactEmployeeList = new ArrayList();
        for (WorkContactEmployeeEntity e : list) {
            int empId = e.getEmployeeId();
            int contactId = e.getContactId();
            String name = employeeService.getWorkEmployeeInfo(empId).getName();
            System.out.println("empId>>>>>>>>>>>>>>>>>>>"+empId+"contactId>>>>>>>>>>>>>"+contactId+"name>>>>>>>>>>>"+name+"客户来源>>>>>>>>>"+e.getAddWay());
            String addWay = e.getAddWay() == null ? AddWayEnum.getByCode(0) : AddWayEnum.getByCode(e.getAddWay());
            String content = String.format("客户通过%s添加企业成员【%s】", addWay, name);
            contactService.saveTrack(empId, contactId, EventEnum.CREATE, content);
        }
        for (WorkContactEmployeeEntity workContactEmployeeEntity:
                list) {
            workContactEmployeeEntity.setRemark(EmojiParser.parseToAliases(workContactEmployeeEntity.getRemark()));
            contactEmployeeList.add(workContactEmployeeEntity);
        }
        for (WorkContactEmployeeEntity workContactEmployeeEntity:
                contactEmployeeList) {
            flag = this.add(workContactEmployeeEntity);
        }
        return flag;
    }


    public boolean add(WorkContactEmployeeEntity entity) {
        boolean result = false;
        try {
            WorkContactEmployeeEntity obj;
            // 更新情况
            obj = detail(entity.getContactId(), entity.getEmployeeId());
            if(obj == null){
                return result = this.save(entity);
            }else{
                UpdateWrapper<WorkContactEmployeeEntity> workContactEmployeeEntityWrapper = new UpdateWrapper<>();
                workContactEmployeeEntityWrapper.eq("contact_id",entity.getContactId());
                workContactEmployeeEntityWrapper.eq("employee_id",entity.getEmployeeId());
                int i = this.baseMapper.update(entity,workContactEmployeeEntityWrapper);
                if(i > 0){
                    return true;
                }
            }
        } catch (Exception e) {
            throw new CommonException();
        }
        return false;
    }

    private WorkContactEmployeeEntity detail(Integer contactId, Integer employeeId) {
        try {
            return this.getOne(Wrappers.<WorkContactEmployeeEntity>lambdaQuery().eq(WorkContactEmployeeEntity::getContactId, contactId).eq(WorkContactEmployeeEntity::getEmployeeId, employeeId));
        } catch (RuntimeException e) {
            throw new CommonException();
        }
    }


    @Override
    public boolean updateContactEmployee(WorkContactEmployeeEntity contactEmployeeEntity) {
        return this.updateById(contactEmployeeEntity);
    }

    /**
     * @description 删除企业下成员的具体客户
     * @author zhaojinjian
     * @createTime 2020/12/21 11:21
     */
    @Override
    public boolean deleteContactEmployee(Integer corpId, Integer empId, Integer contactId) {
        WorkContactEmployeeEntity entity = new WorkContactEmployeeEntity();
        entity.setCorpId(corpId);
        entity.setEmployeeId(empId);
        entity.setContactId(contactId);
        QueryWrapper<WorkContactEmployeeEntity> contactEmployeeWrapper = new QueryWrapper<>(entity);
        return this.baseMapper.delete(contactEmployeeWrapper) == 1;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/2/23 4:08 下午
     * @description 获取渠道码 - 统计分页数据
     */
    @Override
    public Page<RespChannelCodeStatisticsItemVO> getStatisticsOfPage(ReqChannelCodeStatisticsIndexDTO req) {
        Page<RespChannelCodeStatisticsItemVO> voPage = ApiRespUtils.initPage(req);

        QueryWrapper<WorkContactEmployeeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("corp_id", AccountService.getCorpId());
        queryWrapper.eq("state", "channelCodeId-" + req.getChannelCodeId());

        String startTime = "";
        String endTime = "";

        int offset = (req.getPage() - 1) * req.getPerPage();

        int total = 0;
        int totalPage = 1;

        Map<String, RespChannelCodeStatisticsItemVO> map = new LinkedHashMap<>();
        if (req.getType().equals(ReqStatisticsIndexEnum.DAY)) {
            startTime = DateUtils.getDateOfDayStartByS3(req.getStartTime());
            endTime = DateUtils.getDateOfDayEndByS3(req.getEndTime());

            int days = DateUtils.daysBetween(startTime, endTime);
            long startMillis = DateUtils.getMillsByS1(startTime);

            int length = Math.min(offset + req.getPerPage(), days);

            for (int i = offset; i < length; i++) {
                long millis = startMillis + DateUtils.MILLIS_DAY * i;
                String key = DateUtils.formatS3(millis);
                map.put(key, RespChannelCodeStatisticsItemVO.builder().time(key).build());
            }

            // 计算 page 相关数据
            int count = days / req.getPerPage();
            int countP = days % req.getPerPage();
            if (countP > 0) {
                count ++;
            }
            total = days;
            totalPage = Math.max(count, 1);
        }
        if (req.getType().equals(ReqStatisticsIndexEnum.WEEK)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long endMillis = calendar.getTimeInMillis();
            long startMillis = endMillis + 1000 - DateUtils.MILLIS_DAY * 7;

            startTime = DateUtils.formatS1(startMillis);
            endTime = DateUtils.formatS1(endMillis);

            int days = 7;
            for (int i = 0; i < days; i++) {
                long millis = startMillis + DateUtils.MILLIS_DAY * i;
                String key = DateUtils.formatS3(millis);
                map.put(key, RespChannelCodeStatisticsItemVO.builder().time(key).build());
            }

            int count = 7 / req.getPerPage();
            int countP = 7 % req.getPerPage();
            if (countP > 0) {
                count ++;
            }
            total = 7;
            totalPage = Math.max(count, 1);
        }
        if (req.getType().equals(ReqStatisticsIndexEnum.MONTH)) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int lastDay = calendar.getActualMaximum(Calendar.DATE);
            calendar.set(Calendar.DATE, lastDay);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long endMillis = calendar.getTimeInMillis();

            calendar.set(Calendar.YEAR, year - 1);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long startMillis = calendar.getTimeInMillis();

            startTime = DateUtils.formatS1(startMillis);
            endTime = DateUtils.formatS1(endMillis);

            int startYear = year - 1;
            int startMonth = month + 1;
            for (int i = 0; i < 12; i++) {
                int yearC = startYear;
                int monthC = startMonth + i;
                if (monthC > 12) {
                    yearC += 1;
                    monthC -= 12;
                }
                String date = yearC + "-" + monthC;
                map.put(date, RespChannelCodeStatisticsItemVO.builder().time(date).build());
            }

            int count = 12 / req.getPerPage();
            int countP = 12 % req.getPerPage();
            if (countP > 0) {
                count ++;
            }
            total = 12;
            totalPage = Math.max(count, 1);
        }

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            queryWrapper.between("create_time", startTime, endTime);
        }

        Page<WorkContactEmployeeEntity> entityPage = ApiRespUtils.initPage(req);
        page(entityPage, queryWrapper);

        List<WorkContactEmployeeEntity> entityList = entityPage.getRecords();
        for (WorkContactEmployeeEntity e : entityList) {
            Date date = e.getCreateTime();
            String key;
            if (req.getType().equals(ReqStatisticsIndexEnum.MONTH)) {
                key = DateUtils.formatS4(date.getTime());
            } else {
                key = DateUtils.formatS3(date.getTime());

            }

            RespChannelCodeStatisticsItemVO vo = map.get(key);
            if (Status.NORMAL.getCode() == e.getStatus()) {
                vo.setAddNumRange(vo.getAddNumRange() + 1);
            }
            if (Status.REMOVE.getCode() == e.getStatus()) {
                vo.setDeleteNumRange(vo.getDeleteNumRange() + 1);
            }
            if (Status.BLACKLIST.getCode() == e.getStatus()) {
                vo.setDefriendNumRange(vo.getDefriendNumRange() + 1);
            }
            vo.setNetNumRange(vo.getAddNumRange() - vo.getDefriendNumRange() - vo.getDeleteNumRange());
        }

        List<RespChannelCodeStatisticsItemVO> voList = new ArrayList<>(map.values());
        voPage.setRecords(voList);
        voPage.setTotal(total);
        voPage.setPages(totalPage);
        return voPage;
    }

    /**
     * @param req
     * @author: yangpengwei
     * @time: 2021/2/25 11:30 上午
     * @description 渠道码 - 统计折线图
     */
    @Override
    public RespChannelCodeStatisticsVO getStatistics(ReqChannelCodeStatisticsDTO req) {
        RespChannelCodeStatisticsVO voResult = new RespChannelCodeStatisticsVO();

        QueryWrapper<WorkContactEmployeeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("corp_id", AccountService.getCorpId());
        queryWrapper.eq("state", "channelCodeId-" + req.getChannelCodeId());

        String startTime = "";
        String endTime = "";

        Map<String, RespChannelCodeStatisticsItemVO> map = new LinkedHashMap<>();
        if (req.getType().equals(ReqStatisticsIndexEnum.DAY)) {
            startTime = DateUtils.getDateOfDayStartByS3(req.getStartTime());
            endTime = DateUtils.getDateOfDayEndByS3(req.getEndTime());

            int days = DateUtils.daysBetween(startTime, endTime);
            long startMillis = DateUtils.getMillsByS1(startTime);

            for (int i = 0; i < days; i++) {
                long millis = startMillis + DateUtils.MILLIS_DAY * i;
                String key = DateUtils.formatS3(millis);
                map.put(key, RespChannelCodeStatisticsItemVO.builder().time(key).build());
            }
        }
        if (req.getType().equals(ReqStatisticsIndexEnum.WEEK)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long endMillis = calendar.getTimeInMillis();
            long startMillis = endMillis + 1000 - DateUtils.MILLIS_DAY * 7;

            startTime = DateUtils.formatS1(startMillis);
            endTime = DateUtils.formatS1(endMillis);

            int days = 7;
            for (int i = 0; i < days; i++) {
                long millis = startMillis + DateUtils.MILLIS_DAY * i;
                String key = DateUtils.formatS3(millis);
                map.put(key, RespChannelCodeStatisticsItemVO.builder().time(key).build());
            }
        }
        if (req.getType().equals(ReqStatisticsIndexEnum.MONTH)) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int lastDay = calendar.getActualMaximum(Calendar.DATE);
            calendar.set(Calendar.DATE, lastDay);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long endMillis = calendar.getTimeInMillis();

            calendar.set(Calendar.YEAR, year - 1);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long startMillis = calendar.getTimeInMillis();

            startTime = DateUtils.formatS1(startMillis);
            endTime = DateUtils.formatS1(endMillis);

            int startYear = year - 1;
            int startMonth = month + 1;
            for (int i = 0; i < 12; i++) {
                int yearC = startYear;
                int monthC = startMonth + i;
                if (monthC > 12) {
                    yearC += 1;
                    monthC -= 12;
                }
                String date = yearC + "-" + monthC;
                map.put(date, RespChannelCodeStatisticsItemVO.builder().time(date).build());
            }
        }

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            queryWrapper.between("create_time", startTime, endTime);
        }

        List<WorkContactEmployeeEntity> entityList = list(queryWrapper);
        for (WorkContactEmployeeEntity e : entityList) {
            Date date = e.getCreateTime();
            String key;
            if (req.getType().equals(ReqStatisticsIndexEnum.MONTH)) {
                key = DateUtils.formatS4(date.getTime());
            } else {
                key = DateUtils.formatS3(date.getTime());

            }

            RespChannelCodeStatisticsItemVO vo = map.get(key);
            if (Status.NORMAL.getCode() == e.getStatus()) {
                vo.setAddNumRange(vo.getAddNumRange() + 1);
            }
            if (Status.REMOVE.getCode() == e.getStatus()) {
                vo.setDeleteNumRange(vo.getDeleteNumRange() + 1);
            }
            if (Status.BLACKLIST.getCode() == e.getStatus()) {
                vo.setDefriendNumRange(vo.getDefriendNumRange() + 1);
            }
            vo.setNetNumRange(vo.getAddNumRange() - vo.getDefriendNumRange() - vo.getDeleteNumRange());

            // 总数
            voResult.setAddNumLong(voResult.getAddNumLong() + vo.getAddNumRange());
            voResult.setDefriendNumLong(voResult.getDefriendNumLong() + vo.getDefriendNumRange());
            voResult.setDeleteNumLong(voResult.getDeleteNumLong() + vo.getDeleteNumRange());
            voResult.setNetNumLong(voResult.getNetNumLong() + vo.getNetNumRange());
        }


        List<RespChannelCodeStatisticsItemVO> voList = new ArrayList<>(map.values());
        voResult.setList(voList);

        // 当天统计数据
        setCurrentStatistics(voResult, req);

        return voResult;
    }

    @Override
    public List<WorkContactEmployeeEntity> countWorkContactEmployeesByCorpId(Integer corpId, int code) {
        QueryWrapper<WorkContactEmployeeEntity> workContactEmployeeEntityQueryWrapper = new QueryWrapper();
        workContactEmployeeEntityQueryWrapper.eq("corp_id",corpId);
        workContactEmployeeEntityQueryWrapper.eq("status",code);
        return this.baseMapper.selectList(workContactEmployeeEntityQueryWrapper);
    }

    @Override
    public List<WorkContactEmployeeEntity> countWorkContactEmployeesByCorpIdTime(Integer corpId, Date startTime, Date endTime) {
        QueryWrapper<WorkContactEmployeeEntity> workContactEmployeeEntityQueryWrapper = new QueryWrapper();
        workContactEmployeeEntityQueryWrapper.eq("corp_id",corpId);
        workContactEmployeeEntityQueryWrapper.ge("create_time",startTime);
        workContactEmployeeEntityQueryWrapper.lt("create_time",endTime);
        return this.baseMapper.selectList(workContactEmployeeEntityQueryWrapper);

    }

    @Override
    public List<WorkContactEmployeeEntity> countLossWorkContactEmployeesByCorpIdTime(Integer corpId, Date startTime, Date endTime) {
        QueryWrapper<WorkContactEmployeeEntity> workContactEmployeeEntityQueryWrapper = new QueryWrapper();
        workContactEmployeeEntityQueryWrapper.eq("corp_id",corpId);
        workContactEmployeeEntityQueryWrapper.ge("create_time",startTime);
        workContactEmployeeEntityQueryWrapper.lt("create_time",endTime);
        return this.baseMapper.selectList(workContactEmployeeEntityQueryWrapper);
    }

    private void setCurrentStatistics(RespChannelCodeStatisticsVO voResult, ReqChannelCodeStatisticsDTO req) {
        QueryWrapper<WorkContactEmployeeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("corp_id", AccountService.getCorpId());
        queryWrapper.eq("state", "channelCodeId-" + req.getChannelCodeId());

        long currentMillis = System.currentTimeMillis();
        String currentDate = DateUtils.formatS3(currentMillis);
        String startTime = DateUtils.getDateOfDayStartByS3(currentDate);
        String endTime = DateUtils.getDateOfDayEndByS3(currentDate);
        queryWrapper.between("create_time", startTime, endTime);

        List<WorkContactEmployeeEntity> entityList = list(queryWrapper);
        for (WorkContactEmployeeEntity e : entityList) {
            if (Status.NORMAL.getCode() == e.getStatus()) {
                voResult.setAddNum(voResult.getAddNum() + 1);
            }
            if (Status.REMOVE.getCode() == e.getStatus()) {
                voResult.setDeleteNum(voResult.getDeleteNum() + 1);
            }
            if (Status.BLACKLIST.getCode() == e.getStatus()) {
                voResult.setDefriendNum(voResult.getDefriendNum() + 1);
            }
        }
        voResult.setNetNum(voResult.getAddNum() - voResult.getDefriendNum() - voResult.getDefriendNum());
    }
}
