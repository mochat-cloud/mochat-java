package com.mochat.mochat.controller;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.CorpDataEntity;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;
import com.mochat.mochat.job.sync.WorkEmpServiceSyncLogic;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.corp.CorpDataVO;
import com.mochat.mochat.model.properties.ChatToolProperties;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.IWorkMsgConfigService;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.security.MessageDigest;
import java.util.*;

/**
 * @description:企业微信授权
 * @author: Huayu
 * @time: 2020/11/23 18:19
 */
@RestController
@Validated
public class CorpController {

    @Autowired
    private ChatToolProperties chatToolProperties;

    @Autowired
    private IWorkEmployeeService workEmployeeServiceImpl;

    @Autowired
    private ICorpService corpServiceImpl;

    @Autowired
    private IWorkMsgConfigService msgConfigService;

    @Autowired
    private WorkEmpServiceSyncLogic workEmpServiceSyncLogic;

    private final static Logger logger = LoggerFactory.getLogger(CorpController.class);


    /**
     * @param corpName 企业名称
     * @description: 列表
     * @author: Huayu
     * @time: 2020/11/23 19:26
     * @info 因二期权限管理需求, 本人只能查看本公司的信息, 所属其他公司信息查看需要切换公司
     */
    @GetMapping(value = "/corp/index")
    public ApiRespVO getCorpList(String corpName, RequestPage requestPage, @RequestAttribute ReqPerEnum permission) {
        return ApiRespUtils.getApiRespByPage(corpServiceImpl.getCorpPageList(corpName, requestPage, permission));
    }

    /**
     * @param corpName
     * @description: 企业下拉列表
     * @return:
     * @author: Huayu
     * @time: 2020/11/23 19:33
     */
    @GetMapping(value = "/corp/select")
    @ResponseBody
    public ApiRespVO getCorpOptions(String corpName) {
        Integer loginUserId = AccountService.getUserId();
        return ApiRespUtils.getApiRespOfOk(corpServiceImpl.listByLoginUserIdAndCorpName(loginUserId, corpName));
    }


    /**
     * @description: 创建提交
     * @return:
     * @author: Huayu
     * @time: 2020/11/23 19:33
     */
    @PostMapping(value = "/corp/store")
    @ResponseBody
    public ApiRespVO createCorp(@Validated() @RequestBody CorpEntity corpEntity, HttpServletRequest request) throws Exception {
        String contactAccess = WxApiUtils.getAccessToken(corpEntity.getWxCorpId(), corpEntity.getContactSecret());
        if (contactAccess == null) {
            throw new CommonException(100013, "外部联系人秘钥错误");
        }

        String employeeAccess = WxApiUtils.getAccessToken(corpEntity.getWxCorpId(), corpEntity.getEmployeeSecret());
        if (employeeAccess == null) {
            throw new CommonException(100013, "通讯录秘钥错误");
        }

        //事件回调地址
        String callBackUrl = chatToolProperties.getApiUrl() + File.separator + "weWork" + File.separator + "callback";

        String token = "" + System.currentTimeMillis() + new Random().nextInt(999999999);
        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] tokenMd5Byte = md.digest(token.getBytes());
        token = HexUtils.toHexString(tokenMd5Byte);

        String byteStr = Base64.encodeBase64String(UUID.randomUUID().toString().replaceAll("-", "").getBytes());
        String encodingAesKey = byteStr.replace("=", "");

        corpEntity.setToken(token);
        corpEntity.setCreatedAt(new Date());
        corpEntity.setEncodingAesKey(encodingAesKey);
        corpEntity.setEventCallback(callBackUrl);

        //企业授信
        corpServiceImpl.createCorp(corpEntity);

        // 更新企业 id
        AccountService.updateCorpId(corpEntity.getCorpId());

        corpEntity.setEventCallback(callBackUrl + "?cid=" + corpEntity.getCorpId());
        corpServiceImpl.updateCorpByCorpId(corpEntity);

        List<CorpEntity> corpEntityList = corpServiceImpl.getCorpInfoByCorpName(corpEntity.getCorpName());
        String corpId = corpEntityList.get(0).getCorpId().toString();
        // 会话存档-配置
        WorkMsgConfigEntity workMsgConfigEntity = new WorkMsgConfigEntity();
        workMsgConfigEntity.setCorpId(Integer.parseInt(corpId));
        workMsgConfigEntity.setChatApplyStatus(3);
        workMsgConfigEntity.setCreatedAt(new Date());
        msgConfigService.createWorkMessageConfig(workMsgConfigEntity);
        // 同步企业通讯录信息
        logger.info("创建企业成功>>>>>>>>>corpId" + corpId);
        workEmpServiceSyncLogic.onSyncWxEmp(Integer.parseInt(corpId));
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     * @description:更新提交
     * @return:
     * @author: Huayu
     * @time: 2020/11/23 19:38
     */
    @PutMapping(value = "/corp/update")
    public ApiRespVO updateCorp(@Validated() @RequestBody CorpEntity corpEntity) {
        CorpEntity corpInfo = corpServiceImpl.getById(corpEntity.getCorpId());
        if (corpInfo == null) {
            throw new CommonException(100013, "非法参数");
        }
        Integer i = corpServiceImpl.updateCorpByCorpId(corpEntity);
        if (i > 0) {
            logger.info("企业微信授信更新成功");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }


    /**
     * @description: 登录用户绑定企业信息
     * @return:
     * @author: Huayu
     * @time: 2020/11/23 19:48
     */
    @PostMapping(value = "/corp/bind")
    public ApiRespVO bindCorp(@RequestBody @Validated() WorkEmployeeEntity mcWorkEmployeeEntity) {
        //获取登录用户信息
        String userId = AccountService.getUserId().toString();
        //验证当前用户是否归属绑定企业
        //查询当前用户归属的公司
        List<WorkEmployeeEntity> mcWorkEmployeeList = workEmployeeServiceImpl.getWorkEmployeeByLogUserId(userId, mcWorkEmployeeEntity.getCorpId().toString());
        boolean flag = false;
        ApiRespVO apiRespCodeResp = null;
        for (int i = 0; i < mcWorkEmployeeList.size(); i++) {
            if (mcWorkEmployeeList.get(i).getCorpId().toString().equals(mcWorkEmployeeEntity.getCorpId())) {
                flag = true;
                break;
            }
        }
        if (flag = false) {
            throw new CommonException(100013, "非法参数");
        }
        //查询登录用户通讯录信息
        List<WorkEmployeeEntity> mcWorkEmployeeEntityList = workEmployeeServiceImpl.getWorkEmployeeByCorpIdLogUserId(mcWorkEmployeeEntity.getCorpId().toString(), userId);
        Integer employeeId = mcWorkEmployeeEntityList.get(0).getId();
        //存入缓存(key:mc:user.userId   value:corpId-workEmployeeId
        AccountService.updateCorpIdAndEmployeeId(Integer.parseInt(userId), mcWorkEmployeeEntity.getCorpId(), employeeId);
        return ApiRespUtils.getApiRespOfOk("");
    }


    /**
     * @param corpId
     * @description: 企业微信授权 - 详情
     * @return:
     * @author: Huayu
     * @time: 2020/11/23 19:48
     */
    @GetMapping(value = "/corp/show")
    public ApiRespVO showCorp(@NotBlank(message = "企业ID不能为空") String corpId) {
        ApiRespVO apiRespVO = null;
        CorpEntity corpEntity = corpServiceImpl.getById(Integer.valueOf(corpId));
        if (corpEntity == null) {
            apiRespVO = new ApiRespVO(RespErrCodeEnum.INVALID_PARAMS, null);
            //return JSONObject.toJSONString(apiRespCodeResp);
        }
        Map<String, String> listMap = new HashMap<String, String>();
        listMap.put("corpId", corpEntity.getCorpId().toString());
        listMap.put("corpName", corpEntity.getCorpName());
        listMap.put("wxCorpId", corpEntity.getWxCorpId());
        listMap.put("eventCallback", corpEntity.getEventCallback() + "?cid=" + corpEntity.getCorpId());
        listMap.put("employeeSecret", corpEntity.getEmployeeSecret());
        listMap.put("contactSecret", corpEntity.getContactSecret());
        listMap.put("token", corpEntity.getToken());
        listMap.put("encodingAesKey", corpEntity.getEncodingAesKey());
        listMap.put("socialCode", corpEntity.getSocialCode());
        List<Map> listPage = new ArrayList<Map>();
        logger.info("企业微信授权 - 详情<<<<<<<<<" + ApiRespUtils.getApiRespOfOk(listMap));
        return ApiRespUtils.getApiRespOfOk(listMap);
    }


    /**
     * 企业首页数据统计
     *
     * @description:
     * @return:
     * @author: Huayu
     */
    @GetMapping(value = "/corpData/index")
    public ApiRespVO index() throws Exception {
        if (AccountService.getCorpId() == null) {
            throw new CommonException("请先选择企业");
        }
        Map<String, Object> map = corpServiceImpl.handleCorpDta();
        return ApiRespUtils.getApiRespOfOk(map);
    }


    /**
     * 首页数据统计折线图
     *
     * @description:
     * @return:
     * @author: Huayu
     */
    @GetMapping(value = "/corpData/lineChat")
    public ApiRespVO lineChat() {
        if (AccountService.getCorpId() == null) {
            throw new CommonException("请先选择企业");
        }
        List<CorpDataEntity> corpDataEntityList = corpServiceImpl.handleLineChatDta();
        List<CorpDataVO> voList = new ArrayList<>();
        CorpDataVO vo;
        for (CorpDataEntity entity : corpDataEntityList) {
            vo = new CorpDataVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setDate(DateUtils.formatS1(entity.getDate().getTime()));
            voList.add(vo);
        }
        return ApiRespUtils.getApiRespOfOk(voList);
    }

}
