package com.mochat.mochat.service.impl.sensitiveword;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.businesslog.EventEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.BusinessLogEntity;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordEntity;
import com.mochat.mochat.dao.mapper.sensitiveword.SensitiveWordMapper;
import com.mochat.mochat.model.sensitiveword.ReqSensitiveWordIndex;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.business.IBusinessLogService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 敏感词库管理
 * @author: Huayu
 * @time: 2021/1/27 11:11
 */
@Slf4j
@Service
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWordEntity> implements ISensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Autowired
    private IBusinessLogService businessLogService;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    /**
     * @description:删除敏感词
     * @author: Huayu
     * @time: 2021/1/27 11:20
     */
    @Override
    public Integer deleteSensitiveWord(Integer id) {
        Integer i = this.baseMapper.deleteById(id);
        return i;
    }

    /**
     * @description:根据筛选条件
     * @return:
     * @author: Huayu
     * @time: 2021/1/27 16:45
     */
    @Override
    public Page<SensitiveWordEntity> getSensitiveWordList(ReqSensitiveWordIndex sensitiveWordIndex, ReqPerEnum permission) {
        //企业Id
        Integer corpId = AccountService.getCorpId();
        LambdaQueryChainWrapper<SensitiveWordEntity> wrapper = lambdaQuery();
        // 判断是否有权限
        setWrapperPermission(wrapper, permission);
        QueryWrapper<SensitiveWordEntity> queryWrapper = new QueryWrapper<SensitiveWordEntity>();
        queryWrapper.select("id", "name", "group_id", "employee_num", "contact_num", "created_at", "status");
        queryWrapper.eq("corp_id", corpId);
        //敏感词名称
        if (sensitiveWordIndex.getKeyWords() != null && sensitiveWordIndex.getKeyWords().length() != 0) {
            queryWrapper.like("name", sensitiveWordIndex.getKeyWords());
        }
        //敏感词分组id
        if (sensitiveWordIndex.getGroupId() != null && String.valueOf(sensitiveWordIndex.getGroupId()).length() != 0 && sensitiveWordIndex.getGroupId() != 0) {
            queryWrapper.eq("group_id", sensitiveWordIndex.getGroupId());
        }
        Integer pageNo = (sensitiveWordIndex.getPage() == null) ? 0 : sensitiveWordIndex.getPage() - 1;
        Integer pageCount = (sensitiveWordIndex.getPerPage() == null) ? 10 : sensitiveWordIndex.getPerPage();
        Page<SensitiveWordEntity> page = new Page<>(pageNo, pageCount);
        page = sensitiveWordMapper.selectPage(page, queryWrapper);
        return page;
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private void setWrapperPermission(LambdaQueryChainWrapper<SensitiveWordEntity> wrapper, ReqPerEnum permission) {
        if (permission == ReqPerEnum.ALL) {
            return;
        }

        LambdaQueryChainWrapper<BusinessLogEntity> logWrapper = businessLogService.lambdaQuery();
        if (permission == ReqPerEnum.DEPARTMENT) {
            // 查询员工所属的部门 id 列表
            List<Integer> idList = employeeDepartmentService.getDeptAndChildDeptEmpIdList();
            logWrapper.in(BusinessLogEntity::getOperationId, idList);
        }

        if (permission == ReqPerEnum.EMPLOYEE) {
            int empId = AccountService.getEmpId();
            logWrapper.eq(BusinessLogEntity::getOperationId, empId);
        }

        // 渠道码业务 id 列表 (渠道码 id 列表)
        List<Integer> idList = logWrapper.in(
                BusinessLogEntity::getEvent,
                Arrays.asList(EventEnum.CHANNEL_CODE_CREATE.getCode(), EventEnum.CHANNEL_CODE_UPDATE.getCode())
        ).list().stream().map(BusinessLogEntity::getBusinessId).collect(Collectors.toList());

        if (idList.isEmpty()) {
            wrapper.eq(SensitiveWordEntity::getId, -1);
        } else {
            wrapper.in(SensitiveWordEntity::getId, idList);
        }
    }

    @Override
    public List<SensitiveWordEntity> getSensitiveWordList(Integer intelligentGroupId) {
        return getSensitiveWordsByGroupId(intelligentGroupId);
    }

    @Override
    public List<SensitiveWordEntity> getSensitiveWordsByGroupId(Integer groupId) {
        QueryWrapper<SensitiveWordEntity> sensitiveWordEntityQueryWrapper = new QueryWrapper();
        sensitiveWordEntityQueryWrapper.select("id");
        sensitiveWordEntityQueryWrapper.eq("group_id", groupId);
        return this.baseMapper.selectList(sensitiveWordEntityQueryWrapper);
    }

    @Override
    public List<SensitiveWordEntity> getSensitiveWordsByCorpIdStatus(String[] corpIdArr, int i, String s) {
        QueryWrapper<SensitiveWordEntity> sensitiveWordWrapper = new QueryWrapper();
        StringBuilder sb = new StringBuilder();
        for (String str :
                corpIdArr) {
            sb.append(str).append(",");
        }
        sensitiveWordWrapper.select(s);
        sensitiveWordWrapper.eq("status", i);
        sensitiveWordWrapper.in("corp_id", sb.substring(0, sb.length() - 1));
        List<SensitiveWordEntity> sensitiveWordEntityList = sensitiveWordMapper.selectList(sensitiveWordWrapper);
        return sensitiveWordEntityList;
    }

    @Override
    public void updateSensitiveWordById(Object k, Integer employeeNum, Integer contactNum) {
        UpdateWrapper<SensitiveWordEntity> sensitiveWordWrapper = new UpdateWrapper();
        SensitiveWordEntity sensitiveWordEntity = new SensitiveWordEntity();
        sensitiveWordEntity.setEmployeeNum(employeeNum);
        sensitiveWordEntity.setContactNum(contactNum);
        sensitiveWordWrapper.eq("id", k.toString());
        int i = this.baseMapper.update(sensitiveWordEntity, sensitiveWordWrapper);
        if (i > 0) {
            log.info("会话信息监测敏感词更新成功");
        } else {
            log.info("会话信息监测敏感词更新失败");
        }
    }

    @Override
    @Transactional
    public boolean createSensitiveWords(Map<String, Object> mapData) {
        String nameStr = mapData.get("name").toString();
        String groupId = mapData.get("groupId").toString();
        String[] nameArrStr = nameStr.split(",");
        for (String name :
                nameArrStr) {
            boolean i = nameIsUnique(name);
            //创建敏感词
            createSensitiveWord(name, groupId);
        }
        return true;
    }


    @Override
    public Integer updateSensitiveWordById(Integer sensitiveWordId, String clStr, String clStrVal) {
        UpdateWrapper<SensitiveWordEntity> sensitiveWordEntityUpdateWrapper = new UpdateWrapper();
        sensitiveWordEntityUpdateWrapper.set(clStr, clStrVal);
        sensitiveWordEntityUpdateWrapper.eq("id", sensitiveWordId);
        SensitiveWordEntity sensitiveWordEntity = new SensitiveWordEntity();
        if (clStr.equals("group_id")) {
            sensitiveWordEntity.setId(sensitiveWordId);
            sensitiveWordEntity.setGroupId(Integer.valueOf(clStrVal));
        } else {
            sensitiveWordEntity.setId(sensitiveWordId);
            sensitiveWordEntity.setStatus(Integer.valueOf(clStrVal));
        }
        Integer i = this.baseMapper.update(sensitiveWordEntity, sensitiveWordEntityUpdateWrapper);

        // 记录业务日志
        businessLogService.createBusinessLog(sensitiveWordEntity.getId(), sensitiveWordEntity, EventEnum.SENSITIVE_WORD_UPDATE);
        return i;
    }


    @Override
    public boolean nameIsUnique(String name) {
        QueryWrapper<SensitiveWordEntity> sensitiveWordWrapper = new QueryWrapper();
        sensitiveWordWrapper.eq("name", name);
        sensitiveWordWrapper.eq("corp_id", AccountService.getCorpId());
        SensitiveWordEntity sensitiveWordEntity = sensitiveWordMapper.selectOne(sensitiveWordWrapper);
        if (sensitiveWordEntity != null) {
            throw new ParamException(100013, "该敏感词已存在");
        }
        return true;
    }


    @Override
    public boolean createSensitiveWord(Map<String, Object> mapData) {
        String name = mapData.get("name").toString();
        String groupId = mapData.get("groupId").toString();
        nameIsUnique(name);
        createSensitiveWord(name, groupId);
        return true;
    }

    public boolean createSensitiveWord(String name, String groupId) {
        //创建敏感词
        SensitiveWordEntity sensitiveWordEntity = new SensitiveWordEntity();
        sensitiveWordEntity.setName(name);
        sensitiveWordEntity.setGroupId(Integer.valueOf(groupId));
        sensitiveWordEntity.setCorpId(AccountService.getCorpId());
        sensitiveWordEntity.setStatus(1);
        Integer i1 = this.baseMapper.insert(sensitiveWordEntity);
        if (i1 < 1) {
            throw new ParamException(100014, "敏感词创建失败");
        }

        // 记录业务日志
        businessLogService.createBusinessLog(sensitiveWordEntity.getId(), sensitiveWordEntity, EventEnum.SENSITIVE_WORD_CREATE);
        return true;
    }
}
