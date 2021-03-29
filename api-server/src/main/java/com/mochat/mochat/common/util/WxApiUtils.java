package com.mochat.mochat.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.WorkAgentEntity;
import com.mochat.mochat.dao.mapper.WorkAgentMapper;
import com.mochat.mochat.dao.mapper.corp.CorpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

import static com.mochat.mochat.common.constant.WxApiConst.*;

/**
 * @author: yangpengwei
 * @time: 2020/12/11 4:29 下午
 * @description 微信 api 调用工具类
 */
@Slf4j
@Component
public class WxApiUtils {

    private static final String CONST_ACCESS_TOKEN_INVALID = "-1";

    private static Map<String, String> map = new HashMap<>(0);

    private static CorpMapper corpMapper;

    private static WorkAgentMapper workAgentMapper;

    @Autowired
    public void setCorpMapper(CorpMapper corpMapper) {
        WxApiUtils.corpMapper = corpMapper;
    }

    @Autowired
    public void setWorkAgentMapper(WorkAgentMapper workAgentMapper) {
        WxApiUtils.workAgentMapper = workAgentMapper;
    }

    /**
     * 获取企业信息
     *
     * @param corpId 企业 Id
     * @return 企业实体
     */
    private static CorpEntity getCorpEntity(int corpId) {
        return corpMapper.selectById(corpId);
    }

    /**
     * 获取应用信息
     *
     * @param agentId 应用 Id
     * @return 企业实体
     */
    private static WorkAgentEntity getAgentEntity(int agentId) {
        return workAgentMapper.selectById(agentId);
    }

    /**
     * 获取 accessToken
     *
     * @param wxCorpId 企业微信 id
     * @param secret   业务相关 secret
     * @return accessToken
     */
    public static String getAccessToken(String wxCorpId, String secret) {
        String accessTokenUrl = API_ACCESS_TOKEN + "?corpid=" + wxCorpId + "&corpsecret=" + secret;
        String respJson = HttpClientUtil.doGet(accessTokenUrl);

        log.error("getAccessToken: " + respJson);

        JSONObject jsonObject = JSON.parseObject(respJson);
        int errCode = jsonObject.getIntValue("errcode");
        if (errCode == 0) {
            return jsonObject.getString("access_token");
        }
        return null;
    }

    /**
     * 获取通讯录相关 accessToken, 优先从缓存中获取
     *
     * @param corpId 企业 id
     * @return accessToken
     */
    public static String getAccessTokenEmployee(int corpId) {
        String keyAccess = "emp-" + corpId;
        String accessToken = map.get(keyAccess);
        if (accessToken == null) {
            accessToken = getNewAccessTokenEmployee(corpId);
        }
        return accessToken;
    }

    /**
     * 获取通讯录相关 accessToken, 直接获取新的
     *
     * @param corpId 企业 id
     * @return accessToken
     */
    public static String getNewAccessTokenEmployee(int corpId) {
        CorpEntity corpEntity = getCorpEntity(corpId);
        String wxCorpId = corpEntity.getWxCorpId();
        String employSecret = corpEntity.getEmployeeSecret();
        String accessToken = getAccessToken(wxCorpId, employSecret);
        map.put("emp-" + corpId, accessToken);
        return accessToken;
    }

    /**
     * 获取企业微信自建应用相关 accessToken, 优先从缓存中获取
     *
     * @param agentId 应用 id
     * @return accessToken
     */
    public static String getAccessTokenAgent(int corpId, int agentId) {
        String keyAccess = "agent-" + agentId;
        String accessToken = map.get(keyAccess);
        if (accessToken == null) {
            accessToken = getNewAccessTokenAgent(corpId, agentId);
        }
        return accessToken;
    }

    /**
     * 获取通讯录相关 accessToken, 直接获取新的
     *
     * @param agentId 企业 id
     * @return accessToken
     */
    public static String getNewAccessTokenAgent(int corpId, int agentId) {
        CorpEntity corpEntity = getCorpEntity(corpId);
        String wxCorpId = corpEntity.getWxCorpId();
        WorkAgentEntity entity = getAgentEntity(agentId);
        String wxSecret = entity.getWxSecret();
        String accessToken = getAccessToken(wxCorpId, wxSecret);
        map.put("agent-" + agentId, accessToken);
        return accessToken;
    }

    /**
     * 获取客户相关 accessToken, 优先从缓存中获取
     *
     * @param corpId 企业 id
     * @return accessToken
     */
    public static String getAccessTokenContact(int corpId) {
        String keyAccess = "con-" + corpId;
        String accessToken = map.get(keyAccess);
        if (accessToken == null) {
            accessToken = getNewAccessTokenContact(corpId);
        }
        return accessToken;
    }

    /**
     * 获取通讯录相关 accessToken, 直接获取新的
     *
     * @param corpId 企业 id
     * @return accessToken
     */
    public static String getNewAccessTokenContact(int corpId) {
        CorpEntity corpEntity = getCorpEntity(corpId);
        String wxCorpId = corpEntity.getWxCorpId();
        String secret = corpEntity.getContactSecret();
        String accessToken = getAccessToken(wxCorpId, secret);
        map.put("con-" + corpId, accessToken);
        return accessToken;
    }

    /**
     * 获取部门列表
     *
     * @param corpId 企业 id
     * @return 部门列表 json
     */
    public static String requestDepartmentListApi(int corpId) {
        String url = API_DEPARTMENT_LIST + "?access_token=" + getAccessTokenEmployee(corpId);
        String key = "department";
        String respJson = doGetResult(url, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            url = API_DEPARTMENT_LIST + "?access_token=" + getNewAccessTokenEmployee(corpId);
            respJson = doGetResult(url, key);
        }
        return respJson;
    }

    /**
     * 获取部门员工列表
     *
     * @param corpId         企业 id
     * @param wxDepartmentId 企业微信部门 id
     * @return 员工列表 json
     */
    public static String requestUserListApi(int corpId, int wxDepartmentId) {
        String url = API_USER_LIST
                + "?access_token=" + getAccessTokenEmployee(corpId)
                + "&department_id=" + wxDepartmentId
                + "&fetch_child=0";
        String key = "userlist";
        String respJson = doGetResult(url, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            url = API_USER_LIST
                    + "?access_token=" + getNewAccessTokenEmployee(corpId)
                    + "&department_id=" + wxDepartmentId
                    + "&fetch_child=0";
            respJson = doGetResult(url, key);
        }
        return respJson;
    }

    /**
     * 获取员工详情
     *
     * @param agentId 应用 id
     * @param code    通过成员授权获取到的code
     * @return 员工列表 json
     */
    public static String requestWxUserIdApi(int corpId, int agentId, String code) {
        String url = API_USER_INFO
                + "?access_token=" + getAccessTokenAgent(corpId, agentId)
                + "&code=" + code;
        String key = "UserId";
        String respJson = doGetResult(url, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            url = API_USER_LIST
                    + "?access_token=" + getNewAccessTokenAgent(corpId, agentId)
                    + "&code=" + code;
            respJson = doGetResult(url, key);
        }
        return respJson;
    }

    /**
     * 获取开启外部联系人权限的微信员工 id 列表
     *
     * @param corpId 企业 id
     * @return 开启外部联系人权限的微信员工 id 列表 json
     */
    public static String requestFollowUserListApi(int corpId) {
        String url = API_FOLLOW_USER_LIST + "?access_token=" + getAccessTokenContact(corpId);
        String key = "follow_user";
        String respJson = doGetResult(url, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            url = API_FOLLOW_USER_LIST + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doGetResult(url, key);
        }
        return respJson;
    }

    /**
     * 获取员工客户相关统计数据
     *
     * @param corpId   企业 id
     * @param wxUserId 微信员工 id
     * @return 员工客户相关统计数据 json
     */
    public static String getUserBehaviorData(int corpId, String wxUserId) {
        String reqUrl = API_USER_BEHAVIOR_DATA + "?access_token=" + getAccessTokenEmployee(corpId);

        long endTime = System.currentTimeMillis();
        long startTime = endTime - DateUtils.MILLIS_DAY;

        JSONObject reqJson = new JSONObject();
        reqJson.put("userid", new String[]{wxUserId});
        reqJson.put("start_time", startTime / 1000);
        reqJson.put("end_time", endTime / 1000);

        String paramJson = reqJson.toJSONString();
        String key = "behavior_data";
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_USER_BEHAVIOR_DATA + "?access_token=" + getNewAccessTokenEmployee(corpId);
            respJson = doPostResult(reqUrl, key, paramJson);
        }
        return respJson;
    }

    /**
     * 获取企业标签库
     *
     * @param corpId 企业 id
     * @return 获取企业客户所有标签详情 json
     */
    public static String requestGetAllTag(int corpId) {
        return requestGetTagDetail(corpId, null);
    }

    /**
     * 获取企业标签库里某个标签详情
     *
     * @param corpId  企业 id
     * @param wxTagId 微信 tagId
     * @return 获取企业客户标签详情 json
     */
    public static String requestGetTagDetail(int corpId, String wxTagId) {
        String reqUrl = API_GET_CORP_TAG_LIST + "?access_token=" + getAccessTokenContact(corpId);

        String paramJson = "";
        if (wxTagId != null) {
            JSONObject reqJson = new JSONObject();
            reqJson.put("tag_id", new String[]{wxTagId});
            paramJson = reqJson.toJSONString();
        }

        String key = "tag_group";
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_GET_CORP_TAG_LIST + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doPostResult(reqUrl, key, "");
        }
        return respJson;
    }

    /**
     * 编辑企业客户标签组
     *
     * @param corpId  企业 id
     * @param wxTagId 微信标签组 id / 微信标签 id
     * @param tagName 微信标签组名 / 微信标签名
     */
    public static void requestEditTag(int corpId, String wxTagId, String tagName) {
        String reqUrl = API_EDIT_CORP_TAG + "?access_token=" + getAccessTokenContact(corpId);

        JSONObject reqJson = new JSONObject();
        reqJson.put("id", wxTagId);
        reqJson.put("name", tagName);
        String paramJson = reqJson.toJSONString();

        String respJson = doPostResult(reqUrl, null, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_EDIT_CORP_TAG + "?access_token=" + getNewAccessTokenContact(corpId);
            doPostResult(reqUrl, null, paramJson);
        }
    }

    /**
     * 删除微信标签组
     *
     * @param corpId       企业 id
     * @param wxTagGroupId 微信标签组 id
     */
    public static void requestDelGroupTag(int corpId, String wxTagGroupId) {
        requestDelTag(corpId, wxTagGroupId, true);
    }

    /**
     * 删除微信标签
     *
     * @param corpId       企业 id
     * @param wxTagGroupId 微信标签 id
     */
    public static void requestDelTag(int corpId, String wxTagGroupId) {
        requestDelTag(corpId, wxTagGroupId, false);
    }

    public static void requestDelTag(int corpId, String wxTagId, boolean isGroup) {
        String reqUrl = API_DEL_CORP_TAG + "?access_token=" + getAccessTokenContact(corpId);

        String key = isGroup ? "group_id" : "tag_id";
        JSONObject reqJson = new JSONObject();
        reqJson.put(key, new String[]{wxTagId});
        String paramJson = reqJson.toJSONString();

        String respJson = doPostResult(reqUrl, null, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_DEL_CORP_TAG + "?access_token=" + getNewAccessTokenContact(corpId);
            doPostResult(reqUrl, null, paramJson);
        }
    }

    public static void requestDelTags(int corpId, List<String> tagIds) {
        String reqUrl = API_DEL_CORP_TAG + "?access_token=" + getAccessTokenContact(corpId);

        JSONObject reqJson = new JSONObject();
        reqJson.put("tag_id", tagIds);
        String paramJson = reqJson.toJSONString();

        String respJson = doPostResult(reqUrl, null, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_DEL_CORP_TAG + "?access_token=" + getNewAccessTokenContact(corpId);
            doPostResult(reqUrl, null, paramJson);
        }
    }

    public static String requestCreateGroupAndTag(int corpId, String tagGroupName, String tagName) {
        List<String> tagList = new ArrayList<>(1);
        tagList.add(tagName);
        return requestCreateGroupAndTags(corpId, tagGroupName, tagList);
    }

    public static String requestCreateTag(int corpId, String wxTagGroupId, String tagName) {
        List<String> tagList = new ArrayList<>(1);
        tagList.add(tagName);
        return requestCreateTags(corpId, wxTagGroupId, tagList);
    }

    public static String requestCreateGroupAndTags(int corpId, String tagGroupName, List<String> tagNameList) {
        String groupKey = "group_name";
        return requestCreateTags(corpId, groupKey, tagGroupName, tagNameList);
    }

    public static String requestCreateTags(int corpId, String wxGroupId, List<String> tagNameList) {
        String groupKey = "group_id";
        return requestCreateTags(corpId, groupKey, wxGroupId, tagNameList);
    }

    public static String requestCreateTags(int corpId, String groupKey, String groupValue, List<String> tagNameList) {
        String reqUrl = API_ADD_CORP_TAG + "?access_token=" + getAccessTokenContact(corpId);

        List<JSONObject> tagList = new ArrayList<>();
        for (String tagName : tagNameList) {
            JSONObject tagJson = new JSONObject();
            tagJson.put("name", tagName);
            tagList.add(tagJson);
        }

        JSONObject reqJson = new JSONObject();
        reqJson.put(groupKey, groupValue);
        reqJson.put("tag", tagList);
        String paramJson = reqJson.toJSONString();

        String key = "tag_group";
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_ADD_CORP_TAG + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doPostResult(reqUrl, key, paramJson);
        }
        return respJson;
    }

    public static String requestCreateContactWay(int corpId, boolean skipVerify, String state, List<String> wxUserIdList) {
        String reqUrl = API_ADD_CONTACT_WAY + "?access_token=" + getAccessTokenContact(corpId);

        JSONObject reqJson = new JSONObject();
        reqJson.put("type", "2");
        reqJson.put("scene", "2");
        reqJson.put("skip_verify", skipVerify);
        reqJson.put("state", state);
        reqJson.put("user", wxUserIdList);
        String paramJson = reqJson.toJSONString();

        log.debug("requestCreateContactWay: " + paramJson);

        String key = "";
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_ADD_CONTACT_WAY + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doPostResult(reqUrl, key, paramJson);
        }
        return respJson;
    }

    public static String requestCreateContactWay(
            int corpId,
            int type,
            boolean skipVerify,
            String state,
            List<?> wxUserIdList,
            List<?> wxDepartmentIdList
    ) {
        String reqUrl = API_ADD_CONTACT_WAY + "?access_token=" + getAccessTokenContact(corpId);

        JSONObject reqJson = new JSONObject();
        reqJson.put("type", type);
        reqJson.put("scene", "2");
        reqJson.put("skip_verify", skipVerify);
        reqJson.put("state", state);
        reqJson.put("user", wxUserIdList);
        reqJson.put("party", wxDepartmentIdList);
        String paramJson = reqJson.toJSONString();

        log.debug("requestCreateContactWay: " + paramJson);

        String key = "";
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_ADD_CONTACT_WAY + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doPostResult(reqUrl, key, paramJson);
        }
        return respJson;
    }

    public static void requestUpdateContactWay(int corpId, String wxConfigId, List<?> wxUserIdList) {
        requestUpdateContactWay(corpId, wxConfigId, wxUserIdList, Collections.emptyList());
    }

    public static void requestUpdateContactWay(int corpId, String wxConfigId, List<?> wxUserIdList, List<?> wxDepartmentIdList) {
        String reqUrl = API_UPDATE_CONTACT_WAY + "?access_token=" + getAccessTokenContact(corpId);

        JSONObject reqJson = new JSONObject();
        reqJson.put("config_id", wxConfigId);
        reqJson.put("user", wxUserIdList);
        reqJson.put("party", wxDepartmentIdList);
        String paramJson = reqJson.toJSONString();

        log.debug("requestUpdateContactWay: " + paramJson);

        String key = null;
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_UPDATE_CONTACT_WAY + "?access_token=" + getNewAccessTokenContact(corpId);
            doPostResult(reqUrl, key, paramJson);
        }
    }

    /**
     * @description:同步客户群列表
     * @return:
     * @author: Huayu
     * @time: 2020/12/17 17:53
     */
    public static String getWorkRoomIndexData(CorpEntity corpEntity, String ownerId) {
        String reqUrl = API_GET_WORKROOM_LIST + "?access_token=" + getAccessTokenContact(corpEntity.getCorpId());
        List<JSONObject> workRoomList = new ArrayList<>();
        JSONObject workRoomJson = new JSONObject();
        workRoomJson.put("userid_list", ownerId);
        workRoomJson.put("partyid_list", null);
        workRoomList.add(workRoomJson);
        JSONObject reqJson = new JSONObject();
        reqJson.put("status_filter", 0);
        //reqJson.put("owner_filter", workRoomList);
        //reqJson.put("offset", 0);
        reqJson.put("limit", 10);
        String key = "group_chat_list";
        String paramJson = reqJson.toJSONString();
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_GET_WORKROOM_LIST + "?access_token=" + getNewAccessTokenContact(corpEntity.getCorpId());
            respJson = doPostResult(reqUrl, key, paramJson);
        }
        return respJson;
    }

    /**
     * @description:同步客户群详情
     * @return:
     * @author: Huayu
     * @time: 2020/12/17 17:53
     */
    public static String getWorkRoomInfoData(CorpEntity corpEntity, String chatId) {
        String reqUrl = API_GET_WORKROOM_INFO + "?access_token=" + getAccessTokenContact(corpEntity.getCorpId());
        List<JSONObject> workRoomList = new ArrayList<>();
        JSONObject reqJson = new JSONObject();
        reqJson.put("chat_id", chatId);
        String key = "group_chat";
        String paramJson = reqJson.toJSONString();
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_GET_WORKROOM_INFO + "?access_token=" + getNewAccessTokenContact(corpEntity.getCorpId());
            respJson = doPostResult(reqUrl, key, paramJson);
        }
        return respJson;

    }

    /**
     * 修改客户备注信息
     *
     * @param corpId
     * @param map
     * @return
     */
    public static String updateExternalContact(Integer corpId, Map<String, Object> map) {
        String reqUrl = API_EDIT_EXTERNAL_REMARK + "?access_token=" + getAccessTokenContact(corpId);
        JSONObject reqJson = new JSONObject();
        reqJson.put("userid", map.get("userid"));
        reqJson.put("external_userid", map.get("external_userid"));
        reqJson.put("remark", map.get("remark"));
        reqJson.put("description", map.get("description"));
        String key = null;
        String paramJson = reqJson.toJSONString();
        String respJson = doPostResult(reqUrl, key, paramJson);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_GET_WORKROOM_INFO + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doPostResult(reqUrl, key, paramJson);
        }
        return respJson;

    }

    /**
     * 获取客户列表
     *
     * @param corpId
     * @return
     */
    public static String getExternalContactList(Integer corpId, String userId) {
        String reqUrl = API_GET_EXTERNAL_LIST + "?access_token=" + getAccessTokenContact(corpId) + "&userid=" + userId;
        String key = "";
        String respJson = doGetResult(reqUrl, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_GET_WORKROOM_INFO + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doGetResult(reqUrl, key);
        }
        return respJson;

    }

    /**
     * 修改客户详情
     *
     * @param corpId
     * @return
     */
    public static String getExternalContactInfo(Integer corpId, String externalUserid) {
        String reqUrl = API_GET_EXTERNAL_INFO + "?access_token=" + getAccessTokenContact(corpId) + "&external_userid=" + externalUserid;
        String key = "";
        String respJson = doGetResult(reqUrl, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            reqUrl = API_GET_WORKROOM_INFO + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doGetResult(reqUrl, key);
        }
        return respJson;

    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/23 4:44 下午
     * @description 获取指定的应用详情
     */
    public static String getAgentInfo(String wxCorpId, String wxAgentId, String wxAgentSecret) {
        String accessToken = getAccessToken(wxCorpId, wxAgentSecret);
        String paramUrl = "?access_token=" + accessToken + "&agentid=" + wxAgentId;
        String url = API_GET_AGENT_INFO + paramUrl;
        String key = "";
        String respJson = doGetResult(url, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            accessToken = getAccessToken(wxCorpId, wxAgentSecret);
            paramUrl = "?access_token=" + accessToken + "&agentid=" + wxAgentId;
            url = API_GET_AGENT_INFO + paramUrl;
            respJson = doGetResult(url, key);
        }
        return respJson;
    }

    /**
     * 上传临时素材
     *
     * @param corpId 企业 id
     * @param file   素材文件
     * @return 素材文件 id
     */
    public static String uploadImageToTemp(Integer corpId, File file) {
        String url = API_UPLOAD_FILE_TO_TEMP + "?type=image&access_token=" + getAccessTokenEmployee(corpId);
        String key = "media_id";
        String respJson = doPostResult(url, key, file);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            url = API_UPLOAD_FILE_TO_TEMP + "?type=image&access_token=" + getNewAccessTokenEmployee(corpId);
            respJson = doPostResult(url, key, file);
        }
        return respJson;
    }

    public static String getJsapiTicketOfCorp(int corpId) {
        String ticketKey = "corp-" + corpId + "-ticket";
        String ticket = map.get(ticketKey);
        if (ticket == null) {
            ticket = getNewJsapiTicketOfCorp(corpId);
        }
        return ticket;
    }

    public static String getNewJsapiTicketOfCorp(int corpId) {
        String url = API_GET_JSAPI_TICKET_CORP + "?access_token=" + getAccessTokenContact(corpId);
        String key = "ticket";
        String respJson = doGetResult(url, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            url = API_GET_JSAPI_TICKET_CORP + "?access_token=" + getNewAccessTokenContact(corpId);
            respJson = doGetResult(url, key);
        }
        return respJson;
    }

    public static String getJsapiTicketOfApp(int corpId, int agentId) {
        String ticketKey = "corp-" + corpId + "-ticket-" + agentId + "-app";
        String ticket = map.get(ticketKey);
        if (ticket == null) {
            ticket = getNewJsapiTicketOfApp(corpId, agentId);
        }
        return ticket;
    }

    public static String getNewJsapiTicketOfApp(int corpId, int agentId) {
        String url = API_GET_JSAPI_TICKET_APP + "?access_token=" + getAccessTokenAgent(corpId, agentId) + "&type=agent_config";
        String key = "ticket";
        String respJson = doGetResult(url, key);
        if (CONST_ACCESS_TOKEN_INVALID.equals(respJson)) {
            url = API_GET_JSAPI_TICKET_APP + "?access_token=" + getNewAccessTokenAgent(corpId, agentId) + "&type=agent_config";
            respJson = doGetResult(url, key);
        }
        return respJson;
    }

    private static String doGetResult(String url, String key) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        log.debug("url: " + url);
        String respJson = HttpClientUtil.doGet(url);
        log.debug("respJson: " + respJson);
        log.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
        return getResultData(respJson, key);
    }

    private static String doPostResult(String url, String key, String requestBody) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        log.debug("url: " + url);
        log.debug("requestBody: " + requestBody);
        String respJson = HttpClientUtil.doPost(url, requestBody);
        log.debug("respJson: " + respJson);
        log.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
        return getResultData(respJson, key);
    }

    private static String doPostResult(String url, String key, File requestBody) {
        String respJson = HttpClientUtil.doPost(url, requestBody);
        return getResultData(respJson, key);
    }

    private static String getResultData(String result, String key) {
        log.debug(key + " : " + result);

        JSONObject jsonObject = JSON.parseObject(result);
        int errCode = jsonObject.getIntValue("errcode");
        if (errCode == 42001) {
            return CONST_ACCESS_TOKEN_INVALID;
        }
        if (errCode == 0) {
            if (key == null) {
                return "";
            } else if (key.isEmpty()) {
                return result;
            } else {
                return jsonObject.getString(key);
            }
        }
        return null;
    }

    /**
     * 清空所有缓存的 accessToken
     */
    public static void clear() {
        map.clear();
    }

}
