package com.mochat.mochat.controller.greeting;

/**
 * @description:好友欢迎语
 * @author: Huayu
 * @time: 2021/2/1 14:54
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.em.businesslog.EventEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.greeting.GreetingEntity;
import com.mochat.mochat.dao.entity.medium.MediumEnyity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.businessLog.IBusinessLogService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.greeting.IGreetingService;
import com.mochat.mochat.service.impl.medium.IMediumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/greeting")
public class GreetingController {


    @Autowired
    private IGreetingService greetingServiceImpl;

    @Autowired
    private IBusinessLogService businessLogServiceImpl;

    @Autowired
    private IWorkEmployeeService workEmployeeServiceServiceImpl;

    @Autowired
    private IMediumService mediumServiceImpl;

    /**
     * @description:欢迎语列表
     * @author: Huayu
     * @time: 2021/2/2 15:19
     */
    @GetMapping("/index")
    public ApiRespVO greetingIndex(RequestPage page, @RequestAttribute ReqPerEnum permission) {
        Map<String, Object> mapData = greetingServiceImpl.handle(page, permission);
        return ApiRespUtils.getApiRespOfOk(mapData);
    }


    @PostMapping("/store")
    public ApiRespVO greetingStore(@RequestBody GreetingEntity greetingEntity) {
        //判断用户绑定企业信息
        if (AccountService.getCorpId() == null) {
            throw new ParamException(100013, "未选择登录企业，不可操作");
        }
        //欢迎语类型
        String typeStr = greetingEntity.getType();
        if (typeStr.length() == 1) {
            typeStr = "-" + typeStr + "-";
        } else {
            String[] typeArr = typeStr.split(",");
            StringBuilder sb = new StringBuilder();
            for (String arr :
                    typeArr) {
                arr = "-" + arr;
                sb.append(arr);
            }
            typeStr = sb.toString() + "-";
        }
        String employees = greetingEntity.getEmployees();
        JSONArray jsonArray = new JSONArray();
        if(employees != null && !employees.equals("")){
            jsonArray.add(employees);
            employees = jsonArray.toJSONString();
            greetingEntity.setEmployees(employees);
        }else{
            greetingEntity.setEmployees(jsonArray.toJSONString());
        }
        greetingEntity.setType(typeStr);
        greetingEntity.setCorpId(AccountService.getCorpId());
        greetingEntity.setCreatedAt(new Date());
        greetingServiceImpl.createGreeting(greetingEntity);
        //记录业务日志
        businessLogServiceImpl.createBusinessLog(
                greetingEntity.getId(),
                JSON.toJSONString(greetingEntity),
                EventEnum.GREETING_CREATE
        );
        return ApiRespUtils.getApiRespOfOk("");
    }


    @PutMapping("/update")
    public ApiRespVO greetingUpdate(@RequestBody Map<String, Object> mapData) {
        if (mapData.get("greetingId") == null || mapData.get("greetingId").equals("")) {
            throw new ParamException(100013, "欢迎语ID不能为空");
        }
        if (mapData.get("rangeType") == null || mapData.get("rangeType").equals("")) {
            throw new ParamException(100013, "适用成员类型不能为空");
        }
        if (mapData.get("type") == null || mapData.get("type").equals("")) {
            throw new ParamException(100013, "欢迎语类型不能为空");
        }
        //欢迎语类型
        String typeStr = mapData.get("type").toString();
        String[] typeArr = typeStr.split(",");
        StringBuilder sb = new StringBuilder();
        for (String arr :
                typeArr) {
            arr = "-" + arr;
            sb.append(arr);
        }
        typeStr = sb.toString() + "-";
        //适用成员
        String employees = "";
        if (mapData.get("employees") != null && !mapData.get("employees").equals("")) {
            employees = mapData.get("employees").toString();
            String[]  employeesAttr = employees.split(",");
            StringBuilder sb1 = new StringBuilder();
            for (String string:
            employeesAttr) {
                sb1.append(string).append(",");
            }
            employees = sb1.substring(0,sb1.length()-1);
        }
        mapData.put("employees",employees);
        mapData.put("type",typeStr);
        //创建数据
        Integer i = greetingServiceImpl.updateGreetingById(mapData.get("greetingId").toString(), mapData);
        if (i < 1) {
            throw new CommonException(100014, "系统错误,欢迎语更新失败");
        }

        //记录业务日志
        boolean result = businessLogServiceImpl.createBusinessLog(
                Integer.valueOf(mapData.get("greetingId").toString()),
                JSON.toJSONString(mapData),
                EventEnum.GREETING_UPDATE
        );

        if (!result) {
            throw new CommonException(100014, "系统错误,欢迎语更新失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }


    @DeleteMapping("/destroy")
    public ApiRespVO greetingDelete(@RequestBody Map<String, Object> mapData) {
        Integer greetingId = Integer.valueOf(mapData.get("greetingId").toString());
        //获取欢迎语信息
        String clStr = "id,corp_id";
        GreetingEntity greetingEntity = greetingServiceImpl.getGreetingById(greetingId, clStr);
        if (greetingEntity == null) {
            throw new ParamException(100013, "此欢迎语不存在,不可操作");
        }
        //判断欢迎语归属企业
        if (!(AccountService.getCorpId().equals(greetingEntity.getCorpId()))) {
            throw new ParamException(100013, "此欢迎语不归属当前企业,不可操作");
        }
        //数据操作
        Integer i = greetingServiceImpl.deleteGreeting(greetingId);
        if (i < 1) {
            throw new CommonException(100014, "系统错误,欢迎语删除失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }


    /**
     * @description:欢迎语 - 详情
     * @author: Huayu
     * @time: 2021/2/3 16:31
     */
    @GetMapping("/show")
    public ApiRespVO greetingShow(@RequestParam(value="greetingId") Integer greetingId){
        //获取欢迎语详情
        GreetingEntity greetingEntity = greetingServiceImpl.getGreetingById(greetingId,null);
        if(greetingEntity == null){
            throw new ParamException(100013,"此欢迎语不存在,不可操作");
        }
        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("greetingId", greetingEntity.getId());
        mapData.put("rangeType", greetingEntity.getRangeType());
        mapData.put("employees", greetingEntity.getEmployees().isEmpty() ? "" : getEmployees(greetingEntity.getEmployees()));
        mapData.put("words", greetingEntity.getWords());
        mapData.put("mediumId", greetingEntity.getMediumId());
        mapData.put("mediumContent", getMediumContent(greetingEntity.getMediumId()));
        return ApiRespUtils.getApiRespOfOk(mapData);
    }

    private JSONObject getMediumContent(Integer mediumId) {
        MediumEnyity mediumEnyity = mediumServiceImpl.getMediumById(mediumId);
        if (mediumEnyity == null) {
            return new JSONObject();
        }
        String str = mediumServiceImpl.addFullPath(mediumEnyity.getContent(), mediumEnyity.getType());
        JSONObject json = (JSONObject)JSON.parse(str);
        return json;
    }

    private Object getEmployees(String employees) {
        JSONArray jsonArray = JSONObject.parseArray(employees);
        if(jsonArray.size() != 0){
            for (Object json:
                    jsonArray) {
                employees = json.toString() + ",";
            }
        }
        employees = employees.substring(0,employees.length() - 1);
        List<WorkEmployeeEntity> workEmployeeEntityList = workEmployeeServiceServiceImpl.getWorkEmployeesById(employees);
        List<Map<String, Object>> workEmployeeNewEntityList = new ArrayList<>();
        if (workEmployeeEntityList.size() > 0) {
            for (WorkEmployeeEntity workEmployeeEntity :
                    workEmployeeEntityList) {
                Map<String, Object> map = new HashMap<>();
                map.put("employeeId", workEmployeeEntity.getId());
                map.put("employeeName", workEmployeeEntity.getName());
                workEmployeeNewEntityList.add(map);
            }
            return workEmployeeNewEntityList;
        } else {
            return workEmployeeNewEntityList;
        }
    }


}
