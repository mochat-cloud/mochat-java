package com.mochat.mochat.controller;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;
import com.mochat.mochat.job.sync.WorkEmpServiceSyncLogic;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.ITenantService;
import com.mochat.mochat.service.impl.IWorkMsgConfigService;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
@RequestMapping("/gateway/mc")
public class CorpController {

    @Autowired
    private IWorkEmployeeService workEmployeeServiceImpl;

    @Autowired
    private ICorpService corpServiceImpl;

    @Autowired
    private IWorkMsgConfigService msgConfigService;

    @Autowired
    private ITenantService tenantServiceImpl;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WorkEmpServiceSyncLogic workEmpServiceSyncLogic;

    private final static Logger logger = LoggerFactory.getLogger(CorpController.class);


    /**
     * @description: 列表
     * @author: Huayu
     * @time: 2020/11/23 19:26
     *
     * @param corpName 企业名称
     *
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
        //获取当前登录用户所归属的所有企业通讯录信息
        Integer user_id = AccountService.getUserId();
        List<WorkEmployeeEntity> mcWorkEmployeeEntityList = workEmployeeServiceImpl.getWorkEmployeeByUserId(String.valueOf(user_id));
        List<CorpEntity> corpIdList = new ArrayList<CorpEntity>();
        //通过corpId找到用户的所属企业
        for (WorkEmployeeEntity workEmployeeEntity :
                mcWorkEmployeeEntityList) {
            corpIdList.add(corpServiceImpl.getCorpListById(workEmployeeEntity.getCorpId().toString()).get(0));
        }
        return ApiRespUtils.getApiRespOfOk(corpIdList);
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
        String callBackUrl = tenantServiceImpl.getTenantByStatus().get(0).get("url") + File.separator + "weWork" + File.separator + "callback";

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
        //会话存档-配置
        WorkMsgConfigEntity workMsgConfigEntity = new WorkMsgConfigEntity();
        workMsgConfigEntity.setCorpId(Integer.parseInt(corpId));
        workMsgConfigEntity.setChatApplyStatus(3);
        workMsgConfigEntity.setCreatedAt(new Date());
        msgConfigService.createWorkMessageConfig(workMsgConfigEntity);
        //绑定用户与企业的关系
        //String tokenStr = request.getHeader("Authorization");
        //redisTemplate.opsForValue().set("mc:user." + tokenStr, corpId);
        //同步企业通讯录信息
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
        CorpEntity corpInfo = corpServiceImpl.getCorpInfoById(corpEntity.getCorpId());
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
        CorpEntity corpEntity = corpServiceImpl.getCorpInfoById(Integer.valueOf(corpId));
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


    @GetMapping(value = "/corpData/lineChat")
    public ApiRespVO lineChat(){
        Map<String,Object> map = null;
        List<Map<String,Object>> mapList = new ArrayList();
        Integer id = 20;
        Integer addContactNum = 1;
        Integer addIntoRoomNum = 0;
        Integer lossContactNum = 1;
        Integer quitRoomNum = 0;
        String[] date1 = {"2021-02-23 00:00:00","2021-02-24 00:00:00","2021-02-25 00:00:00","2021-02-26 00:00:00","2021-02-27 00:00:00"};
        String[] date2 = {"2021-02-28 00:00:00","2021-03-01 00:00:00","2021-03-02 00:00:00","2021-03-03 00:00:00","2021-03-04 00:00:00"};
        String[] date3 = {"2021-03-05 00:00:00","2021-03-06 00:00:00","2021-03-07 00:00:00","2021-03-08 00:00:00","2021-03-09 00:00:00"};
        String[] date4 = {"2021-03-10 00:00:00","2021-03-12 00:00:00","2021-03-13 00:00:00","2021-03-14 00:00:00","2021-03-15 00:00:00"};
        String[] date5 = {"2021-03-16 00:00:00","2021-03-17 00:00:00","2021-03-18 00:00:00","2021-03-19 00:00:00","2021-03-20 00:00:00"};
        String[] date6 = {"2021-03-21 00:00:00","2021-03-22 00:00:00","2021-03-23 00:00:00","2021-03-24 00:00:00"};
        for (int i = 0; i < 5; i++) {
            map = new HashMap();
            map.put("id",id);
            map.put("addContactNum",addContactNum);
            map.put("addIntoRoomNum",addIntoRoomNum);
            map.put("lossContactNum",lossContactNum);
            map.put("quitRoomNum",quitRoomNum);
            map.put("date",date1[i]);
            mapList.add(map);
            id++;
        }
        for (int i = 0; i < 5; i++) {
            map = new HashMap();
            map.put("id",id);
            map.put("addContactNum",addContactNum);
            map.put("addIntoRoomNum",addIntoRoomNum);
            map.put("lossContactNum",lossContactNum);
            map.put("quitRoomNum",quitRoomNum);
            map.put("date",date2[i]);
            mapList.add(map);
            id++;
        }
        for (int i = 0; i < 5; i++) {
            map = new HashMap();
            map.put("id",id);
            map.put("addContactNum",addContactNum);
            map.put("addIntoRoomNum",addIntoRoomNum);
            map.put("lossContactNum",lossContactNum);
            map.put("quitRoomNum",quitRoomNum);
            map.put("date",date3[i]);
            mapList.add(map);
            id++;
        }
        for (int i = 0; i < 5; i++) {
            map = new HashMap();
            map.put("id",id);
            map.put("addContactNum",addContactNum);
            map.put("addIntoRoomNum",addIntoRoomNum);
            map.put("lossContactNum",lossContactNum);
            map.put("quitRoomNum",quitRoomNum);
            map.put("date",date4[i]);
            mapList.add(map);
            id++;
        }
        for (int i = 0; i < 5; i++) {
            map = new HashMap();
            map.put("id",id);
            map.put("addContactNum",addContactNum);
            map.put("addIntoRoomNum",addIntoRoomNum);
            map.put("lossContactNum",lossContactNum);
            map.put("quitRoomNum",quitRoomNum);
            map.put("date",date5[i]);
            mapList.add(map);
            id++;
        }

        for (int i = 0; i < 4; i++) {
            map = new HashMap();
            map.put("id",id);
            map.put("addContactNum",addContactNum);
            map.put("addIntoRoomNum",addIntoRoomNum);
            map.put("lossContactNum",lossContactNum);
            map.put("quitRoomNum",quitRoomNum);
            map.put("date",date6[i]);
            mapList.add(map);
            id++;
        }
        return ApiRespUtils.getApiRespOfOk(mapList);
    }



    @GetMapping(value = "/corpData/index")
    public ApiRespVO index(){
        Map<String,Object> map = new HashMap();
        map.put("weChatContactNum",38);
        map.put("weChatRoomNum",1);
        map.put("roomMemberNum",5);
        map.put("corpMemberNum",7);
        map.put("addContactNum",0);
        map.put("lastAddContactNum",0);
        map.put("addIntoRoomNum",0);
        map.put("lastAddIntoRoomNum",0);
        map.put("lossContactNum",0);
        map.put("lastLossContactNum",0);
        map.put("quitRoomNum",0);
        map.put("lastQuitRoomNum",0);
        map.put("addFriendsNum",4);
        map.put("lastAddFriendsNum",9);
        map.put("monthAddRoomNum",0);
        map.put("lastMonthAddRoomNum",0);
        map.put("monthAddRoomMemberNum",0);
        map.put("lastMonthAddRoomMemberNum",0);
        map.put("monthLossContactNum",3);
        map.put("lastMonthLossContactNum",4);
        map.put("updateTime","2021-03-26 06:50:00");
        return ApiRespUtils.getApiRespOfOk(map);
    }

    public static String getRandomString(int length) {

        //1. 定义一个字符串（A-Z，a-z，0-9）即62个数字字母；

        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

        //2. 由Random生成随机数

        Random random = new Random();

        StringBuffer sb = new StringBuffer();

        //3. 长度为几就循环几次

        for (int i = 0; i < length; ++i) {

            //从62个的数字或字母中选择

            int number = random.nextInt(62);

            //将产生的数字通过length次承载到sb中

            sb.append(str.charAt(number));

        }

        //将承载的字符转换成字符串

        return sb.toString();

    }

}
