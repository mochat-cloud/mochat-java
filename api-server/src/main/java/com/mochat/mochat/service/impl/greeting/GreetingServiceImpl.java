package com.mochat.mochat.service.impl.greeting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.businesslog.EventEnum;
import com.mochat.mochat.common.em.greeting.RangeTypeEnum;
import com.mochat.mochat.common.em.medium.TypeEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.PageModel;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.dao.entity.BusinessLogEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.greeting.GreetingEntity;
import com.mochat.mochat.dao.entity.medium.MediumEnyity;
import com.mochat.mochat.dao.mapper.greeting.GreetingMapper;
import com.mochat.mochat.dao.mapper.medium.MediumMapper;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.businesslog.IBusinessLogService;
import com.mochat.mochat.service.emp.IWorkEmployeeDepartmentService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.greeting.IGreetingService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.medium.IMediumService;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:好友欢迎语
 * @author: Huayu
 * @time: 2021/2/1 15:14
 */
@Service
public class GreetingServiceImpl extends ServiceImpl<GreetingMapper, GreetingEntity> implements IGreetingService {

    @Resource
    private GreetingMapper greetingMapper;

    @Resource
    private MediumMapper mediumMapper;

    @Autowired
    private IMediumService mediumServiceImpl;

    @Autowired
    private IWorkEmployeeService workEmployeeServiceImpl;

    @Autowired
    private IWorkEmployeeDepartmentService employeeDepartmentService;

    @Autowired
    private IBusinessLogService businessLogService;

    @Autowired
    private ICorpService corpService;


    @Override
    public Map<String, Object> getGreeting(String userId) {
        //查询客户跟进人信息[企业通讯录]
        WorkEmployeeEntity workEmployeeEntity = workEmployeeServiceImpl.getWorkEmployeeByWxUserId(userId, "id,corp_id");
        //查询员工欢迎语
        List<GreetingEntity> greetingEntityList = getGreetingsByCorpId(workEmployeeEntity.getCorpId(), "id,medium_id,words,range_type,employees");
        String employees = null;
        int count = 0;
        Map<String, Object> commonMap = new HashMap();
        ;
        Map<String, Object> contentMap = new HashMap();
        Map<String, Object> contentMapOuter = new HashMap();
        String[] empArr = null;
        for (GreetingEntity greetingEntity :
                greetingEntityList) {
            //检索通用欢迎语
            if (greetingEntity.getRangeType().equals(RangeTypeEnum.ALL.getCode())) {
                commonMap.put("text", greetingEntity.getWords());
                commonMap.put("mediumId", greetingEntity.getMediumId());
            }
            //检索指定成员欢迎语
            if (JSONObject.parseArray(greetingEntity.getEmployees()).size() > 0) {
                JSONArray jsonArray = JSONObject.parseArray(greetingEntity.getEmployees());
                if (jsonArray.size() != 0) {
                    for (Object json :
                            jsonArray) {
                        employees = json.toString() + ",";
                    }
                }
                employees = employees.substring(0, employees.length() - 1);
                empArr = employees.split(",");
            }
            if (employees != null) {
                count = employees.indexOf(workEmployeeEntity.getId());
            }
            if (empArr == null || count != -1) {
                continue;
            }
            contentMap.put("text", greetingEntity.getWords());
            contentMap.put("mediumId", greetingEntity.getMediumId());
            contentMapOuter.put("content", contentMap);
        }
        if (contentMapOuter.size() == 0 && commonMap.size() > 0) {
            contentMapOuter = (Map<String, Object>) contentMapOuter.put("content", commonMap);
            ;
        }
        if (!((Map<String, Object>) contentMapOuter.get("content")).get("mediumId").equals("")) {
            Map<String, Map<String, Object>> mediumMap = getMediumData((Integer) ((Map<String, Object>) contentMapOuter.get("content")).get("mediumId"));
            contentMapOuter.put("content", mediumMap);
        }
        return contentMapOuter;
    }


    /**
     * @description: 发送欢迎语
     * @author: Huayu
     * @time: 2021/3/30 18:36
     */
    @Override
    public void applyWxSendContactMessage(int corpId, String welcomeCode, Map<String, JSONObject> contactInfo, Map<String, Object> content) {
        Map<String, Object> sendWelcomeDataMap = new HashMap();
        //微信消息体 - 文本
        if (content.get("text") != null) {
            String contentStr = content.get("text").toString();
            String name = null;
            for (Map.Entry<String, JSONObject> mapEntry : contactInfo.entrySet()) {
                JSONObject json = mapEntry.getValue();
                if (json.getInteger("errcode") == 0) {
                    JSONObject contactJson = json.getJSONObject("external_contact");
                    name = contactJson.getString("name");
                }
            }
            contentStr = contentStr.replace("##客户名称##", name);
            sendWelcomeDataMap.put("text", new HashMap<String, Object>().put("content", contentStr));
        }
        //微信消息体 - 媒体文件
        if (content.get("medium") != null) {
            Integer type = (Integer) (((Map<String, Object>) content.get("medium")).get("mediumType"));
            switch (type) {
                case 2:
                    String jsonStr = (String) ((Map<String, Object>) content.get("medium")).get("mediumContent");
                    String imagePath = JSON.parseObject(jsonStr).getString("imagePath");
                    File imageFile = AliyunOssUtils.getFile(imagePath);
                    String mediaId = WxApiUtils.uploadImageToTemp(AccountService.getCorpId(), imageFile);
                    sendWelcomeDataMap.put("image", new HashMap<String, Object>().put("media_id", mediaId));
                    break;
                case 3:
                    Map<String, Object> linkMap = new HashMap();
                    JSONObject jsonObject = (JSONObject) ((Map<String, Object>) content.get("medium")).get("mediumContent");
                    linkMap.put("title", jsonObject.get("title"));
                    linkMap.put("picurl", mediumServiceImpl.addFullPath((String) jsonObject.get("imagePath"), 3));
                    linkMap.put("desc", jsonObject.get("title"));
                    linkMap.put("url", jsonObject.get("imageLink"));
                    sendWelcomeDataMap.put("link", linkMap);
                    break;
                case 6:
                    Map<String, Object> miniMap = new HashMap();
                    JSONObject jsonMiniObject = (JSONObject) ((Map<String, Object>) content.get("medium")).get("mediumContent");
                    String imagePathMini = jsonMiniObject.getString("imagePath");
                    File imageFileMini = AliyunOssUtils.getFile(imagePathMini);
                    String mediaIdMini = WxApiUtils.uploadImageToTemp(AccountService.getCorpId(), imageFileMini);
                    miniMap.put("title", jsonMiniObject.getString("title"));
                    miniMap.put("pic_media_id", mediaIdMini);
                    miniMap.put("appid", jsonMiniObject.getString("appid"));
                    miniMap.put("page", jsonMiniObject.getString("page"));
                    sendWelcomeDataMap.put("miniprogram", miniMap);
            }
        }
        //发送欢迎语
        String respStr = WxApiUtils.sendWelcomeCode(corpId, sendWelcomeDataMap);
        if (respStr == null) {
            System.out.println("请求微信上推送新增客户信息失败>>>>>>>");
        }
    }

    private Map<String, Map<String, Object>> getMediumData(Integer mediumId) {
        Map<String, Object> map = null;
        Map<String, Map<String, Object>> mapData = new HashMap();
        MediumEnyity mediumEntity = mediumServiceImpl.getMediumById(mediumId);
        if (mediumEntity != null) {
            map = new HashMap();
            map.put("mediumType", mediumEntity.getType());
            map.put("mediumContent", mediumEntity.getContent());
        }
        mapData.put("medium", map);
        return mapData;
    }

    @Override
    public Map<String, Object> handle(RequestPage page, ReqPerEnum permission) {
        //处理请求参数
        return handleParams(page, permission);
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 11:19 上午
     * @description 权限管理查询条件配置
     */
    private void setWrapperPermission(LambdaQueryChainWrapper<GreetingEntity> wrapper, ReqPerEnum permission) {
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
            wrapper.eq(GreetingEntity::getId, -1);
        } else {
            wrapper.in(GreetingEntity::getId, idList);
        }
    }


    /**
     * @description:创建欢迎语
     * @author: Huayu
     * @time: 2021/2/2 16:27
     */
    @Override
    public Integer createGreeting(GreetingEntity greetingEntity) {
        int result = this.baseMapper.insert(greetingEntity);

        // 记录业务日志
        businessLogService.createBusinessLog(greetingEntity.getId(),greetingEntity,EventEnum.GREETING_CREATE);
        return result;
    }


    /**
     * @description:更新欢迎语
     * @author: Huayu
     * @time: 2021/2/3 15:30
     */
    @Override
    public Integer updateGreetingById(String greetingId, Map<String, Object> mapData) {
        UpdateWrapper<GreetingEntity> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", greetingId);
        updateWrapper.set("range_type", Integer.valueOf(mapData.get("rangeType").toString()));
        JSONArray jsonArray = new JSONArray();
        String employees = mapData.get("employees").toString();
        if (employees != null && !employees.equals("")) {
            String[] employeesArr = employees.split(",");
            for (String employee :
                    employeesArr) {
                jsonArray.add(employee);
            }
            employees = jsonArray.toJSONString();
            updateWrapper.set("employees", employees);
        }
        updateWrapper.set("type", mapData.get("type").toString());
        updateWrapper.set("words", mapData.get("words").toString());
        updateWrapper.set("medium_id", Integer.valueOf(mapData.get("mediumId").toString()));
        updateWrapper.set("corp_id", AccountService.getCorpId());
        GreetingEntity greetingEntity = new GreetingEntity();
        greetingEntity.setRangeType(Integer.valueOf(mapData.get("rangeType").toString()));
        greetingEntity.setEmployees(mapData.get("employees").equals("") ? new JSONArray().toJSONString() : JSONObject.toJSONString(mapData.get("employees")));
        greetingEntity.setType(mapData.get("type").toString());
        greetingEntity.setWords(mapData.get("words").toString());
        greetingEntity.setMediumId(Integer.valueOf(mapData.get("mediumId").toString()));
        greetingEntity.setCorpId(AccountService.getCorpId());
        Integer i = this.baseMapper.update(greetingEntity, updateWrapper);

        // 记录业务日志
        businessLogService.createBusinessLog(Integer.parseInt(greetingId), greetingEntity,EventEnum.GREETING_UPDATE);
        return i;
    }


    /**
     * @description:获取欢迎语信息
     * @author: Huayu
     * @time: 2021/2/3 16:10
     */
    @Override
    public GreetingEntity getGreetingById(Integer greetingId, String clStr) {
        QueryWrapper<GreetingEntity> greetingEntityQueryWrapper = new QueryWrapper();
        if (clStr == null) {
            greetingEntityQueryWrapper.getSqlSelect();
        } else {
            greetingEntityQueryWrapper.select(clStr);
        }
        greetingEntityQueryWrapper.eq("id", greetingId);
        return this.baseMapper.selectOne(greetingEntityQueryWrapper);
    }

    /**
     * @description:删除欢迎语
     * @author: Huayu
     * @time: 2021/2/3 16:21
     */
    @Override
    public Integer deleteGreeting(Integer greetingId) {
        return this.baseMapper.deleteById(greetingId);
    }

    @Override
    public List<GreetingEntity> getGreetingsByCorpId(Integer corpId, String s) {
        QueryWrapper<GreetingEntity> greetingEntityQueryWrapper = new QueryWrapper();
        greetingEntityQueryWrapper.select(s);
        greetingEntityQueryWrapper.eq("corp_id", corpId);
        return this.baseMapper.selectList(greetingEntityQueryWrapper);
    }

    private Map<String, Object> handleParams(RequestPage req, ReqPerEnum permission) {
        // 列表查询条件
        Integer corpId = AccountService.getCorpId();
        // 查询数据
        LambdaQueryChainWrapper<GreetingEntity> wrapper = lambdaQuery();
        // 权限管理数据权限
        setWrapperPermission(wrapper, permission);

        Page<GreetingEntity> page = ApiRespUtils.initPage(req);
        wrapper.orderByDesc(GreetingEntity::getCreatedAt);
        wrapper.page(page);

        List<Map<String, Object>> listMapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("page", new PageModel(req.getPerPage(), (int) page.getTotal(), (int) page.getSize()));
        //查询已存在的数据
        mapData = hadGreetings(page.getRecords(), mapData);
        //欢迎语素材handle
        List<GreetingEntity> greetingEntityList = page.getRecords();
        Integer mediumId = null;
        String mediumContent = "";
        //StringBuilder sb = new StringBuilder();
        Map<String, Object> listMap = null;
        for (GreetingEntity greetingEntity :
                greetingEntityList) {
            listMap = new HashMap<String, Object>();
            mediumId = greetingEntity.getMediumId();
            //sb.append(mediumId).append(",");
            MediumEnyity medium = getMedium(mediumId);
            if (medium != null && medium.getMediaId() != null) {
                mediumContent = mediumServiceImpl.addFullPath(medium.getContent(), medium.getType());
            }
            //欢迎语类型
            String typeTextArr = greetingEntity.getType();
            //typeTextArr = typeTextArr.substring(0,typeTextArr.length()-1);
            String[] typeTextList = StringUtils.split(typeTextArr, "-");
            String typeText = null;
            if (typeTextList.length > 1) {
                for (String arr :
                        typeTextList) {
                    typeText = TypeEnum.getTypeByCode(Integer.valueOf(arr));
                    typeText = typeText + "+";
                }
                typeText = typeText.substring(0, typeText.length() - 1);
            } else {
                typeText = TypeEnum.getTypeByCode(Integer.valueOf(typeTextList[0]));
            }
            listMap.put("greetingId", greetingEntity.getId());
            listMap.put("typeText", typeText);
            listMap.put("rangeType", greetingEntity.getRangeType());
            listMap.put("rangeTypeText", RangeTypeEnum.getTypeByCode(greetingEntity.getRangeType()));
            listMap.put("employees", getEmployees(greetingEntity.getRangeType(), greetingEntity.getEmployees()));
            listMap.put("words", greetingEntity.getWords());
            listMap.put("mediumId", greetingEntity.getMediumId());
            listMap.put("mediumContent", mediumContent);
            listMap.put("createdAt", DateUtils.formatS1(greetingEntity.getCreatedAt().getTime()));
            listMapList.add(listMap);
        }
        mapData.put("list", listMapList);
        return mapData;
    }

    private List<String> getEmployees(Integer rangeType, String employees) {
        List<String> listStr = new ArrayList<String>();
        if (rangeType.equals(1)) {
            String msg = RangeTypeEnum.getTypeByCode(rangeType);
            listStr.add(msg);
            return listStr;
        } else {
            JSONArray jsonArray = JSONObject.parseArray(employees);
            if (jsonArray.size() != 0) {
                for (Object json :
                        jsonArray) {
                    employees = json.toString() + ",";
                }
            }
            employees = employees.substring(0, employees.length() - 1);
            List<WorkEmployeeEntity> workEmployeeEntityList = workEmployeeServiceImpl.getWorkEmployeesById(employees);
            String name = null;
            for (WorkEmployeeEntity workEmployeeEntity :
                    workEmployeeEntityList) {
                if (workEmployeeEntity != null) {
                    name = workEmployeeEntity.getName();
                    name = name + ",";
                }
            }
            name = name.substring(0, name.length() - 1);
            listStr.add(name);
            return listStr;
        }
    }

    private MediumEnyity getMedium(Integer mediumId) {
        QueryWrapper<MediumEnyity> mediumQueryWrapper = new QueryWrapper();
        mediumQueryWrapper.select("id", "type", "content");
        mediumQueryWrapper.eq("id", mediumId);
        return this.mediumMapper.selectOne(mediumQueryWrapper);
    }

    private Map<String, Object> hadGreetings(List<GreetingEntity> greetingEntityList, Map<String, Object> mapData) {
        Integer flag = 0;
        if (greetingEntityList != null || greetingEntityList.size() != 0) {
            String employeesArr = null;
            for (GreetingEntity greetingEntity :
                    greetingEntityList) {
                if (greetingEntity.getRangeType().equals(RangeTypeEnum.ALL.getCode())) {
                    flag = 1;
                }
                employeesArr = greetingEntity.getEmployees();
                JSONArray jsonArray = JSONObject.parseArray(employeesArr);
                if (jsonArray.size() != 0) {
                    for (Object json :
                            jsonArray) {
                        employeesArr = json.toString() + ",";
                    }
                }
            }
            if (flag.equals(1)) {
                mapData.put("hadGeneral", 1);
            } else {
                mapData.put("hadGeneral", 0);
            }
            if (employeesArr != null && employeesArr.length() > 0) {
                employeesArr = employeesArr.substring(0, employeesArr.length() - 1);
                mapData.put("hadEmployees", employeesArr.split(","));
            } else {
                mapData.put("hadEmployees", JSONObject.parseArray(employeesArr));
            }
        }
        return mapData;
    }
}
