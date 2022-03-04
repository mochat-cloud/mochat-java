package com.mochat.mochat.service.wxback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mochat.mochat.common.util.FileUtils;
import com.mochat.mochat.common.util.emp.DownUploadQueueUtils;
import com.mochat.mochat.dao.entity.UserEntity;
import com.mochat.mochat.dao.entity.WorkDeptEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.mapper.SubSystemMapper;
import com.mochat.mochat.dao.mapper.WorkDeptMapper;
import com.mochat.mochat.dao.mapper.WorkEmployeeMapper;
import com.mochat.mochat.model.emp.WXEmployeeDTO;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class WxCallbackEmployeeServiceImp implements IWxCallbackEmployeeService {

    @Autowired
    private WorkDeptMapper workDeptMapper;

    @Autowired
    private WorkEmployeeMapper workEmployeeMapper;

    @Autowired
    private SubSystemMapper subSystemMapper;

    @Autowired
    private IWorkEmployeeService employeeService;

    @Override
    public String dispatchEvent(String dataJson) {
        JSONObject jsonObject = JSON.parseObject(dataJson);
        int corpId = jsonObject.getIntValue("corpId");
        JSONObject xmlJsonObject = jsonObject.getJSONObject("xml");
        String changeType = xmlJsonObject.getString("changetype");

        // 部门回调
        if (CHANGE_TYPE_DEPARTMENT_CREATE.equals(changeType)) {
            return createDepartment(corpId, xmlJsonObject);
        }
        if (CHANGE_TYPE_DEPARTMENT_UPDATE.equals(changeType)) {
            return updateDepartment(corpId, xmlJsonObject);
        }
        if (CHANGE_TYPE_DEPARTMENT_DELETE.equals(changeType)) {
            return deleteDepartment(corpId, xmlJsonObject);
        }

        // 员工回调
        if (CHANGE_TYPE_EMPLOYEE_CREATE.equals(changeType)) {
            return createEmployee(corpId, xmlJsonObject);
        }
        if (CHANGE_TYPE_EMPLOYEE_UPDATE.equals(changeType)) {
            return updateEmployee(corpId, xmlJsonObject);
        }
        if (CHANGE_TYPE_EMPLOYEE_DELETE.equals(changeType)) {
            return deleteEmployee(corpId, xmlJsonObject);
        }

        return "";
    }

    public String createDepartment(int corpId, JSONObject jsonObject) {
        int wxDepartmentId = jsonObject.getIntValue("id");
        String name = jsonObject.getString("name");
        int wxParentId = jsonObject.getIntValue("parentid");
        int order = jsonObject.getIntValue("order");

        WorkDeptEntity entity = new WorkDeptEntity();
        entity.setWxDepartmentId(wxDepartmentId);
        entity.setCorpId(corpId);
        entity.setName(name);
        entity.setParentId(getMainDepartmentId(corpId, wxParentId));
        entity.setWxParentid(wxParentId);
        entity.setOrder(order);
        workDeptMapper.insert(entity);
        return "success";
    }

    public String updateDepartment(int corpId, JSONObject jsonObject) {
        int wxDepartmentId = jsonObject.getIntValue("id");
        String name = jsonObject.getString("name");
        Integer wxParentId = jsonObject.getInteger("parentid");

        WorkDeptEntity entity = new WorkDeptEntity();
        entity.setWxDepartmentId(wxDepartmentId);
        entity.setCorpId(corpId);

        entity = workDeptMapper.selectOne(new QueryWrapper<>(entity));
        if (name != null) {
            entity.setName(name);
        }
        if (wxParentId != null) {
            entity.setParentId(getMainDepartmentId(corpId, wxParentId));
            entity.setWxParentid(wxParentId);
        }
        workDeptMapper.updateById(entity);
        return "success";
    }

    public String deleteDepartment(int corpId, JSONObject jsonObject) {
        int wxDepartmentId = jsonObject.getIntValue("id");
        WorkDeptEntity entity = new WorkDeptEntity();
        entity.setWxDepartmentId(wxDepartmentId);
        entity.setCorpId(corpId);

        entity = workDeptMapper.selectOne(new QueryWrapper<>(entity));
        workDeptMapper.deleteById(entity.getId());
        return "success";
    }

    public String createEmployee(int corpId, JSONObject jsonObject) {
        List<Integer> integers = new ArrayList<>();

        WXEmployeeDTO dto = new WXEmployeeDTO();
        dto.setUserid(jsonObject.getString("userid"));
        dto.setName(jsonObject.getString("name"));

        String[] depts = jsonObject.getString("department").split(",");
        for (String s : depts) {
            integers.add(Integer.parseInt(s));
        }
        dto.setDepartment(integers);

        dto.setPosition(jsonObject.getString("position"));
        dto.setMobile(jsonObject.getString("mobile"));
        dto.setGender(jsonObject.getIntValue("gender"));
        dto.setEmail(jsonObject.getString("email"));
        dto.setAvatar(jsonObject.getString("avatar"));
        dto.setStatus(jsonObject.getIntValue("status"));
        dto.setIsleader(jsonObject.getIntValue("isleader"));

        JSONArray extattrArray = jsonObject.getJSONArray("extattr");
        List<String> attrList = new ArrayList<>();
        if (extattrArray != null && !extattrArray.isEmpty()) {
            for (int i = 0; i < extattrArray.size(); i++) {
                attrList.add(extattrArray.getString(i));
            }
        }
        JSONObject extattrJson = new JSONObject();
        extattrJson.put("attrs", attrList);
        dto.setExtattr(extattrJson.toJSONString());

        dto.setTelephone(jsonObject.getString("telephone"));

        integers.clear();
        dto.setOrder(integers);

        dto.setMainDepartment(jsonObject.getIntValue("maindepartment"));
        dto.setQrCode(jsonObject.getString("qrcode"));
        dto.setAlias(jsonObject.getString("alias"));

        integers.clear();
        String[] isleaderindepts = jsonObject.getString("isleaderindept").split(",");
        for (String s : isleaderindepts) {
            integers.add(Integer.parseInt(s));
        }
        dto.setIsLeaderInDept(integers);

        dto.setAddress(jsonObject.getString("address"));

        employeeService.insertEmployee(corpId, dto);
        return "success";
    }

    public String updateEmployee(int corpId, JSONObject jsonObject) {
        List<Integer> integers = new ArrayList<>();

        String wxUserId = jsonObject.getString("userid");
        WorkEmployeeEntity entity = new WorkEmployeeEntity();
        entity.setCorpId(corpId);
        entity.setWxUserId(wxUserId);
        entity = workEmployeeMapper.selectOne(new QueryWrapper<>(entity));
        if (entity == null) {
            log.error(" >>> 微信回调: 更新成员时, 成员不存在." + jsonObject.toJSONString());
            return "success";
        }

        String newWxUserId = jsonObject.getString("newuserid");
        if (newWxUserId != null) {
            wxUserId = newWxUserId;
            entity.setWxUserId(wxUserId);
        }

        String name = jsonObject.getString("name");
        if (name != null) {
            entity.setName(name);
        }

        String mobile = jsonObject.getString("mobile");
        if (mobile != null) {
            entity.setMobile(mobile);
            // 关联子账户
            List<UserEntity> userEntityList = subSystemMapper.selectList(
                    new QueryWrapper<UserEntity>()
                            .select("id")
                            .eq("phone", mobile)
            );
            if (!userEntityList.isEmpty()) {
                entity.setLogUserId(userEntityList.get(0).getId());
            }
        }

        String status = jsonObject.getString("status");
        if (status != null) {
            entity.setStatus(Integer.parseInt(status));
        }

        String avatar = jsonObject.getString("avatar");
        if (avatar != null) {
            String thumbAvatar = getThumbAvatar(avatar);

            String avatarKey = FileUtils.getFileNameOfEmpAvatar();
            String thumbAvatarKey = FileUtils.getFileNameOfEmpThumbAvatar();

            entity.setAvatar(avatarKey);
            entity.setThumbAvatar(thumbAvatarKey);

            DownUploadQueueUtils.uploadFileByUrl(avatarKey, avatar);
            DownUploadQueueUtils.uploadFileByUrl(thumbAvatarKey, thumbAvatar);
        }

        String alias = jsonObject.getString("alias");
        if (alias != null) {
            entity.setAlias(alias);
        }

        String telephone = jsonObject.getString("telephone");
        if (telephone != null) {
            entity.setTelephone(telephone);
        }

        String address = jsonObject.getString("address");
        if (address != null) {
            entity.setAddress(address);
        }

        JSONArray extattrArray = jsonObject.getJSONArray("extattr");
        if (extattrArray != null) {
            List<String> attrList = new ArrayList<>();
            for (int i = 0; i < extattrArray.size(); i++) {
                attrList.add(extattrArray.getString(i));
            }
            JSONObject extattrJson = new JSONObject();
            extattrJson.put("attrs", attrList);
            entity.setExtattr(extattrJson.toJSONString());
        }

        WXEmployeeDTO dto = new WXEmployeeDTO();
        String department = jsonObject.getString("department");
        integers.clear();
        if (department != null) {
            String[] depts = department.split(",");
            for (String s : depts) {
                integers.add(Integer.parseInt(s));
            }
        }
        dto.setDepartment(integers);

        String isleaderindept = jsonObject.getString("isleaderindept");
        integers.clear();
        if (isleaderindept != null) {
            String[] isleaderindepts = jsonObject.getString("isleaderindept").split(",");
            for (String s : isleaderindepts) {
                integers.add(Integer.parseInt(s));
            }
        }
        dto.setIsLeaderInDept(integers);

        String order = jsonObject.getString("order");
        integers.clear();
        if (order != null) {
            String[] orders = jsonObject.getString("order").split(",");
            for (String s : orders) {
                integers.add(Integer.parseInt(s));
            }
        }
        dto.setOrder(integers);

        workEmployeeMapper.updateById(entity);
        if (department != null) {
            dto.setUserid(wxUserId);
            employeeService.updateEmpDeptIndex(corpId, dto);
        }

        return "success";
    }

    public String deleteEmployee(int corpId, JSONObject jsonObject) {
        String userId = jsonObject.getString("userid");
        WorkEmployeeEntity entity = new WorkEmployeeEntity();
        entity.setCorpId(corpId);
        entity.setWxUserId(userId);
        entity = workEmployeeMapper.selectOne(new QueryWrapper<>(entity));
        workEmployeeMapper.deleteById(entity.getId());
        return "success";
    }

    private int getMainDepartmentId(int corpId, int wxDepartmentId) {
        if (wxDepartmentId == 0) {
            return 0;
        }
        WorkDeptEntity workDeptEntity = new WorkDeptEntity();
        workDeptEntity.setCorpId(corpId);
        workDeptEntity.setWxDepartmentId(wxDepartmentId);
        return workDeptMapper.selectOne(new QueryWrapper<>(workDeptEntity)).getId();
    }

    public String getThumbAvatar(String avatar) {
        if (avatar != null) {
            int index = avatar.lastIndexOf("/");
            return avatar.substring(0, index) + "/100";
        }
        return "";
    }

}
