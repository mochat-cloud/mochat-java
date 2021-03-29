package com.mochat.mochat.service.emp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.dao.entity.WorkDeptEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeDepartmentEntity;
import com.mochat.mochat.dao.mapper.WorkDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:部门表业务
 * @author: Huayu
 * @time: 2020/11/28 17:53
 */
@Service
public class WorkDeptServiceImpl extends ServiceImpl<WorkDeptMapper, WorkDeptEntity> implements IWorkDeptService {

    @Autowired
    private IWorkEmployeeService workEmployeeService;

    @Autowired
    private IWorkEmployeeDepartmentService workEmployeeDepartmentService;

    /**
     * @description:根据corpId获取部门
     * @return:
     * @author: Huayu
     * @time: 2020/11/28 18:00
     */
    @Override
    public List<WorkDeptEntity> getWorkDepartmentsByCorpId(String corpId) {
        QueryWrapper<WorkDeptEntity> entityQueryWrapper = new QueryWrapper<WorkDeptEntity>();
        entityQueryWrapper.setEntity(new WorkDeptEntity());
        entityQueryWrapper.eq("corp_id", corpId);
        return this.baseMapper.selectList(entityQueryWrapper);
    }


    @Override
    public boolean insertDeptments(List<WorkDeptEntity> workDeptEntityList,int corpId) {
        Integer i = 0;
        for (WorkDeptEntity dept :
                workDeptEntityList) {
             i = this.baseMapper.insert(dept);
         }
        if(i > 0){
            //处理父部门
            Map<String,Object> parentDeptMap = getDepartmentIds(corpId);
            List<WorkDeptEntity> workDeptEntityList1 = getDepartmentUpdateData(parentDeptMap);
            if(workDeptEntityList1 != null && workDeptEntityList1.size() > 0){
                updateWorkDepartmentByIds(workDeptEntityList1);
            }
        }
        return  true;
    }


    private void updateWorkDepartmentByIds(List<WorkDeptEntity> workDeptEntityList1) {
        for (WorkDeptEntity workDept:
        workDeptEntityList1) {
            UpdateWrapper<WorkDeptEntity> workDeptUpdateWrapper = new UpdateWrapper();
            workDeptUpdateWrapper.eq("id",workDept.getId());
            Integer i = this.baseMapper.update(workDept,workDeptUpdateWrapper);
            if(i < 1){
                log.error("WorkDeptService->handle>>>>>>>>同步部门失败");
            }
        }
    }


    private List<WorkDeptEntity> getDepartmentUpdateData(Map<String, Object> parentDeptMap) {
        List<WorkDeptEntity> workDeptEntityList = new ArrayList();
        for (String key:
        parentDeptMap.keySet()) {
            //级别
            WorkDeptEntity workDeptEntity = (WorkDeptEntity)parentDeptMap.get(key);
            Map map = getDepartmentRelation(workDeptEntity,parentDeptMap,"",0);
            WorkDeptEntity workDept = new WorkDeptEntity();
            workDept.setId(workDeptEntity.getId());
            workDept.setPath((String)map.get("path"));
            workDept.setLevel((Integer) map.get("level"));
            workDept.setParentId(parentDeptMap.get(workDeptEntity.getWxParentid().toString()) != null && !key.equals("0") ?((WorkDeptEntity)parentDeptMap.get(workDeptEntity.getWxParentid().toString())).getId() : 0);
            workDeptEntityList.add(workDept);
        }
        return workDeptEntityList;
    }


    private Map<String,Object> getDepartmentRelation(WorkDeptEntity workDeptEntity, Map<String, Object> parentDeptMap,String path,Integer level) {
        path = "#"+workDeptEntity.getId()+"#-"+path;
        if(!workDeptEntity.getWxParentid().toString().isEmpty() && !workDeptEntity.getWxParentid().toString().equals("0")){
            level = level + 1;
        }
        if(!workDeptEntity.getWxParentid().toString().isEmpty() && !workDeptEntity.getWxParentid().toString().equals("0")){
            return getDepartmentRelation((WorkDeptEntity)parentDeptMap.get(workDeptEntity.getWxParentid().toString()),parentDeptMap,path,level);
        }
        Map<String,Object> map = new HashMap();
        map.put("path",path.substring(0,path.length()-1));
        map.put("level",level);
        return map;
    }


    private Map<String, Object> getDepartmentIds(Integer corpId) {
        QueryWrapper<WorkDeptEntity> WorkDeptEntityWrapper = new QueryWrapper();
        WorkDeptEntityWrapper.getSqlSelect();
        WorkDeptEntityWrapper.eq("corp_id",corpId);
        Map<String,Object> map = new HashMap<String,Object>();
        List<WorkDeptEntity> parentDeptEntityList = this.baseMapper.selectList(WorkDeptEntityWrapper);
        for (WorkDeptEntity deptEntity:
        parentDeptEntityList) {
            map.put(deptEntity.getWxDepartmentId().toString(),deptEntity);
        }
        return map;
    }

    @Transactional
    @Override
    public Integer updDeptments(List<WorkDeptEntity> workDeptEntityList) {
        Integer i = 0;
        for (WorkDeptEntity dept :
                workDeptEntityList) {
            i = this.baseMapper.updateById(dept);
            i++;
        }
        return i;

    }

    /**
     * @description 部门管理 - 部门列表
     * @author zhaojinjian
     * @createTime 2021/1/7 12:54
     */
    @Override
    public JSONObject getWorkDepartments(String searchKeyWords, Integer corpId) {
        JSONObject resultJson = new JSONObject();
        QueryWrapper<WorkDeptEntity> deptWrapper = new QueryWrapper<>();
        if (searchKeyWords != null && !searchKeyWords.isEmpty()) {
            deptWrapper.like("name", searchKeyWords);
        }
        deptWrapper.eq("corp_id", corpId);
        deptWrapper.isNull("deleted_at");
        List<WorkDeptEntity> deptList = this.list(deptWrapper);
        resultJson.put("department", this.getRecursionDeptList(deptList, 0));
        resultJson.put("employee", workEmployeeService.getCorpEmployeeName(corpId, searchKeyWords));
        return resultJson;
    }

    /**
     * @description 递归获取部门及子部门
     * @author zhaojinjian
     * @createTime 2021/1/7 12:55
     */
    private JSONArray getRecursionDeptList(List<WorkDeptEntity> list, Integer pId) {
        JSONArray resultArray = new JSONArray();
        List<WorkDeptEntity> childList = list.stream().filter(c -> c.getWxParentid() == pId).collect(Collectors.toList());
        childList.forEach(item -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", item.getId());
            jsonObject.put("name", item.getName());
            jsonObject.put("parentId", item.getParentId());
            jsonObject.put("son", this.getRecursionDeptList(list, item.getWxDepartmentId()));
            resultArray.add(jsonObject);
        });
        return resultArray;

    }

    /**
     * @description 获取部门下的成员列表
     * @author zhaojinjian
     * @createTime 2021/1/7 13:32
     */
    @Override
    public JSONArray getDeptMemberList(List<Integer> deptIds, Integer corpId) {
        //region 获取成员和成员部门中间表信息

        List<WorkEmployeeDepartmentEntity> employeeDepartmentList = workEmployeeDepartmentService.getDeptEmployeeList(deptIds);
        List<Integer> empIds = employeeDepartmentList.stream().map(WorkEmployeeDepartmentEntity::getEmployeeId).collect(Collectors.toList());
        //endregion

        //region 获取部门信息
        QueryWrapper<WorkDeptEntity> workDeptWrapper = new QueryWrapper<>();
        workDeptWrapper.select("id,name");
        workDeptWrapper.in("id", deptIds);
        workDeptWrapper.isNull("deleted_at");
        List<WorkDeptEntity> deptList = this.list(workDeptWrapper);
        //endregion

        //region 获取成员信息
        Map<Integer, String> empNameMap = workEmployeeService.getCorpEmployeeName(corpId, empIds);
        //endregion
        JSONArray resultArray = new JSONArray();
        employeeDepartmentList.forEach(item -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("departmentId", item.getDepartmentId());
            Optional<WorkDeptEntity> deptOp = deptList.stream().filter(c -> c.getId().equals(item.getDepartmentId())).findAny();
            jsonObject.put("departmentName", deptOp.get().getName());
            jsonObject.put("employeeId", item.getEmployeeId());
            jsonObject.put("employeeName", empNameMap.get(item.getEmployeeId()));
            resultArray.add(jsonObject);
        });
        return resultArray;
    }

    @Override
    public List<WorkDeptEntity> getWorkDepartmentsById(String ids, String clStr) {
        QueryWrapper<WorkDeptEntity> workDeptEntityQueryWrapper = new QueryWrapper();
        workDeptEntityQueryWrapper.select(clStr);
        workDeptEntityQueryWrapper.in("id",ids);
        return this.baseMapper.selectList(workDeptEntityQueryWrapper);
    }

    @Override
    public List<WorkDeptEntity> getWorkDepartmentsByCorpIdName(Integer corpId, String name, String clStr) {
        QueryWrapper<WorkDeptEntity> workDeptEntityQueryWrapper = new QueryWrapper();
        workDeptEntityQueryWrapper.select(clStr);
        workDeptEntityQueryWrapper.eq("corp_id",corpId);
        if(name != null && !name.equals("")){
            workDeptEntityQueryWrapper.eq("name",name);
        }
        return this.baseMapper.selectList(workDeptEntityQueryWrapper);
    }

    @Override
    public List<WorkDeptEntity> getWorkDepartmentsByCorpIdPath(Integer corpId, String path, String id) {
        QueryWrapper<WorkDeptEntity> workDeptEntityQueryWrapper = new QueryWrapper();
        workDeptEntityQueryWrapper.select("id");
        workDeptEntityQueryWrapper.like("path",path);
        workDeptEntityQueryWrapper.eq("corp_id",corpId);
        return this.baseMapper.selectList(workDeptEntityQueryWrapper);
    }

    @Override
    public List<WorkDeptEntity> getWorkDepartmentsBySearch(String substring, String clStr) {
        List<WorkDeptEntity> workDeptEntityList = new ArrayList();
        String[] strArr = substring.split(",");
        for (String str:
        strArr) {
            QueryWrapper<WorkDeptEntity> workDeptEntityQueryWrapper = new QueryWrapper();
            workDeptEntityQueryWrapper.select(clStr);
            workDeptEntityQueryWrapper.eq("id",str);
            WorkDeptEntity workDeptEntity = this.baseMapper.selectOne(workDeptEntityQueryWrapper);
            workDeptEntityList.add(workDeptEntity);
        }
        return workDeptEntityList;
    }

    @Override
    public List<WorkDeptEntity> getWorkDepartmentsByPath(int level, String path) {
        QueryWrapper<WorkDeptEntity> workDeptEntityQueryWrapper = new QueryWrapper();
        workDeptEntityQueryWrapper.likeRight("path",path);
        //workDeptEntityQueryWrapper.eq("level",level);
        return this.baseMapper.selectList(workDeptEntityQueryWrapper);
    }

    /**
     * 根据部门 id 列表获取部门企业 id 列表
     *
     * @param deptIdList
     */
    @Override
    public List<Integer> getWxDeptIdListByDeptIdList(List<Integer> deptIdList) {
        if (Objects.isNull(deptIdList) || deptIdList.isEmpty()) {
            return Collections.emptyList();
        }

        return listByIds(deptIdList).stream().map(WorkDeptEntity::getWxDepartmentId).collect(Collectors.toList());
    }

    @Override
    public List<WorkDeptEntity> getNameById(String departmentIds, String name) {
        QueryWrapper<WorkDeptEntity> workDeptEntityQueryWrapper = new QueryWrapper();
        workDeptEntityQueryWrapper.select(name);
        workDeptEntityQueryWrapper.in("id",departmentIds);
        return this.baseMapper.selectList(workDeptEntityQueryWrapper);
    }
}
