package com.mochat.mochat.service.wm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.em.workmessage.MsgTypeEnum;
import com.mochat.mochat.common.util.DateUtils;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.common.util.wm.WorkMsgHelper;
import com.mochat.mochat.dao.entity.WorkContactEntity;
import com.mochat.mochat.dao.entity.WorkContactRoomEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.WorkRoomEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgIndexEntity;
import com.mochat.mochat.dao.mapper.ContactMapper;
import com.mochat.mochat.dao.mapper.WorkContactRoomMapper;
import com.mochat.mochat.dao.mapper.WorkEmployeeMapper;
import com.mochat.mochat.dao.mapper.wm.WorkMsgIndexMapper;
import com.mochat.mochat.dao.mapper.wm.WorkMsgMapper;
import com.mochat.mochat.dao.mapper.workroom.WorkRoomMapper;
import com.mochat.mochat.job.WorkMsgBackUpUtil;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.model.wm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 5:45 下午
 * @description 运营-聊天记录业务实现
 */
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class ChatServiceImpl implements IChatService {

    @Autowired
    private WorkEmployeeMapper workEmployeeMapper;

    @Autowired
    private ContactMapper workContactMapper;

    @Autowired
    private WorkRoomMapper workRoomMapper;

    @Autowired
    private WorkContactRoomMapper workContactRoomMapper;

    @Autowired
    private WorkMsgIndexMapper workMsgIndexMapper;

    @Autowired
    private WorkMsgMapper workMsgMapper;

    /**
     * 会话内容存档 - 会话员工下拉
     * 获取对应公司员工简略信息
     *
     * @param name   员工名
     * @return 员工信息集合
     */
    @Override
    public List<FromUserInfoBO> getFromUserInfoList(String name) {
        int corpId = AccountService.getCorpId();

        QueryWrapper<WorkEmployeeEntity> wrapper = new QueryWrapper<>();
        wrapper.select("id", "name", "avatar");
        wrapper.eq("corp_id", corpId);
        if (null != name && !name.isEmpty()) {
            wrapper.like("name", name);
        }

        List<WorkEmployeeEntity> list = workEmployeeMapper.selectList(wrapper);
        List<FromUserInfoBO> fromUsers = new ArrayList<>();
        for (WorkEmployeeEntity e: list) {
            FromUserInfoBO fromUserInfoBO = new FromUserInfoBO();
            fromUserInfoBO.setId(e.getId());
            fromUserInfoBO.setName(e.getName());
            fromUserInfoBO.setAvatar(AliyunOssUtils.getUrl(e.getAvatar()));
            fromUsers.add(fromUserInfoBO);
        }
        return fromUsers;
    }

    /**
     * 会话内容存档 - 会话对象列表
     *
     * @return 会话对象信息集合
     */
    @Override
    public Page<ToUserInfoBO> getToUserInfoList(ReqToUsersDTO req) {
        Page<ToUserInfoBO> page = new Page<>();
        ApiRespUtils.initPage(page, req);

        int toUserType = req.getToUsertype();
        switch (toUserType) {
            case 0:
                return getToUserInfoListByEmployee(page, req);
            case 1:
                return getToUserInfoListByContact(page, req);
            case 2:
                return getToUserInfoListByRoom(page, req);
        }
        return page;
    }

    private Page<ToUserInfoBO> getToUserInfoListByEmployee(Page<ToUserInfoBO> page, ReqToUsersDTO req) {
        int corpId = AccountService.getCorpId();
        QueryWrapper<WorkMsgIndexEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("corp_id", corpId);
        wrapper.eq("from_id", req.getWorkEmployeeId());
        wrapper.eq("to_type", req.getToUsertype());

        String name = req.getName();
        if (name != null && !name.isEmpty()) {
            List<WorkEmployeeEntity> employeeEntityList = workEmployeeMapper.selectList(
                    new QueryWrapper<WorkEmployeeEntity>()
                            .select("id")
                            .eq("corp_id", corpId)
                            .like("name", name)
            );
            List<Integer> employeeIds = employeeEntityList.stream()
                    .map(WorkEmployeeEntity::getId)
                    .collect(Collectors.toList());
            if (employeeIds.isEmpty()) {
                // 没有符合的数据
                return page;
            } else {
                wrapper.in("to_id", employeeIds);
            }
        }

        Page<WorkMsgIndexEntity> pageEntity = ApiRespUtils.transPage(page);
        pageEntity = workMsgIndexMapper.selectPage(pageEntity, wrapper);
        List<WorkMsgIndexEntity> workMsgIndexEntityList = pageEntity.getRecords();

        if (workMsgIndexEntityList.isEmpty()) {
            return page;
        }

        List<Integer> toUserIds = workMsgIndexEntityList.stream()
                .map(WorkMsgIndexEntity::getToId)
                .collect(Collectors.toList());
        List<WorkEmployeeEntity> workEmployeeEntities = workEmployeeMapper.selectList(
                new QueryWrapper<WorkEmployeeEntity>()
                        .select("id", "wx_user_id", "name", "alias", "avatar")
                        .in("id", toUserIds)
        );
        if (workEmployeeEntities.isEmpty()) {
            return page;
        }

        WorkEmployeeEntity fromEmployee = workEmployeeMapper.selectById(req.getWorkEmployeeId());

        List<ToUserInfoBO> toUserInfoBOList = new ArrayList<>();
        ToUserInfoBO toUserInfo;
        WorkEmployeeEntity workEmployeeEntity;
        for (int i = 0; i < workEmployeeEntities.size(); i++) {
            workEmployeeEntity = workEmployeeEntities.get(i);
            toUserInfo = new ToUserInfoBO();
            toUserInfo.setWorkEmployeeId(req.getWorkEmployeeId());
            toUserInfo.setToUsertype(req.getToUsertype());
            toUserInfo.setToUserId(workEmployeeEntity.getId());
            toUserInfo.setName(workEmployeeEntity.getName());
            toUserInfo.setAlias(workEmployeeEntity.getAlias());
            toUserInfo.setAvatar(AliyunOssUtils.getUrl(workEmployeeEntity.getAvatar()));

            Map<String, Object> sqlMap = new HashMap<>(5);
            sqlMap.put("tableName", WorkMsgHelper.getTableName(corpId));
            sqlMap.put("corpId", corpId);
            sqlMap.put("toType", req.getToUsertype());
            sqlMap.put("from", fromEmployee.getWxUserId());
            sqlMap.put("to", workEmployeeEntity.getWxUserId());

            setContentAndMsgTimeOfToUserInfoBO(toUserInfo, sqlMap);

            toUserInfoBOList.add(toUserInfo);
        }

        page.setRecords(toUserInfoBOList);

        return page;
    }

    private Page<ToUserInfoBO> getToUserInfoListByContact(Page<ToUserInfoBO> page, ReqToUsersDTO req) {
        int corpId = AccountService.getCorpId();
        QueryWrapper<WorkMsgIndexEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("corp_id", corpId);
        wrapper.eq("from_id", req.getWorkEmployeeId());
        wrapper.eq("to_type", req.getToUsertype());

        String name = req.getName();
        if (name != null && !name.isEmpty()) {
            List<WorkContactEntity> contactEntities = workContactMapper.selectList(
                    new QueryWrapper<WorkContactEntity>()
                            .select("id")
                            .eq("corp_id", corpId)
                            .like("name", name)
            );
            List<Integer> contactIds = new ArrayList<>();
            for (int i = 0; i < contactEntities.size(); i++) {
                contactIds.add(contactEntities.get(i).getId());
            }
            if (contactIds.size() < 1) {
                // 没有符合的数据
                return page;
            } else {
                wrapper.in("to_id", contactIds);
            }
        }

        Page<WorkMsgIndexEntity> pageEntity = ApiRespUtils.transPage(page);
        pageEntity = workMsgIndexMapper.selectPage(pageEntity, wrapper);
        List<WorkMsgIndexEntity> workMsgIndexEntityList = pageEntity.getRecords();

        if (workMsgIndexEntityList.isEmpty()) {
            return page;
        }

        List<Integer> toUserIds = new ArrayList<>();
        for (int i = 0; i < workMsgIndexEntityList.size(); i++) {
            toUserIds.add(workMsgIndexEntityList.get(i).getToId());
        }
        List<WorkContactEntity> contactEntities = workContactMapper.selectList(
                new QueryWrapper<WorkContactEntity>()
                        .select("id", "wx_external_userid", "name", "avatar")
                        .in("id", toUserIds)
        );

        if (contactEntities.isEmpty()) {
            return page;
        }

        WorkEmployeeEntity fromEmployee = workEmployeeMapper.selectById(req.getWorkEmployeeId());

        List<ToUserInfoBO> toUserInfoBOList = new ArrayList<>();
        ToUserInfoBO toUserInfo;
        WorkContactEntity contactEntity;
        for (int i = 0; i < contactEntities.size(); i++) {
            contactEntity = contactEntities.get(i);
            toUserInfo = new ToUserInfoBO();
            toUserInfo.setWorkEmployeeId(req.getWorkEmployeeId());
            toUserInfo.setToUsertype(req.getToUsertype());
            toUserInfo.setToUserId(contactEntity.getId());
            toUserInfo.setName(contactEntity.getName());
            toUserInfo.setAlias("");
            toUserInfo.setAvatar(AliyunOssUtils.getUrl(contactEntity.getAvatar()));

            Map<String, Object> sqlMap = new HashMap<>(5);
            sqlMap.put("tableName", WorkMsgHelper.getTableName(corpId));
            sqlMap.put("corpId", corpId);
            sqlMap.put("toType", req.getToUsertype());
            sqlMap.put("from", fromEmployee.getWxUserId());
            sqlMap.put("to", contactEntity.getWxExternalUserid());

            setContentAndMsgTimeOfToUserInfoBO(toUserInfo, sqlMap);

            toUserInfoBOList.add(toUserInfo);
        }

        page.setRecords(toUserInfoBOList);

        return page;
    }

    private Page<ToUserInfoBO> getToUserInfoListByRoom(Page<ToUserInfoBO> page, ReqToUsersDTO req) {
        int corpId = AccountService.getCorpId();
        QueryWrapper<WorkContactRoomEntity> wrapper = new QueryWrapper<>();
        wrapper.select("room_id");
        wrapper.eq("employee_id", req.getWorkEmployeeId());
        wrapper.eq("type", 1);
        wrapper.eq("status", 1);

        String name = req.getName();
        if (name != null && !name.isEmpty()) {
            List<WorkRoomEntity> roomEntities = workRoomMapper.selectList(
                    new QueryWrapper<WorkRoomEntity>()
                            .select("id")
                            .eq("corp_id", corpId)
                            .like("name", name)
            );
            if (roomEntities.isEmpty()) {
                // 没有符合的数据
                return page;
            }

            List<Integer> roomIds = new ArrayList<>();
            for (int i = 0; i < roomEntities.size(); i++) {
                roomIds.add(roomEntities.get(i).getId());
            }
            wrapper.in("room_id", roomIds);
        }

        Page<WorkContactRoomEntity> pageEntity = ApiRespUtils.transPage(page);
        pageEntity = workContactRoomMapper.selectPage(pageEntity, wrapper);
        List<WorkContactRoomEntity> workContactRoomEntities = pageEntity.getRecords();

        if (workContactRoomEntities.isEmpty()) {
            return page;
        }

        List<Integer> toUserIds = new ArrayList<>();
        for (int i = 0; i < workContactRoomEntities.size(); i++) {
            toUserIds.add(Integer.valueOf(workContactRoomEntities.get(i).getRoomId()));
        }
        List<WorkRoomEntity> workRoomEntities = workRoomMapper.selectList(
                new QueryWrapper<WorkRoomEntity>()
                        .select("id", "wx_chat_id", "name")
                        .in("id", toUserIds)
        );

        if (workRoomEntities.isEmpty()) {
            return page;
        }

        List<ToUserInfoBO> toUserInfoBOList = new ArrayList<>();
        ToUserInfoBO toUserInfo;
        WorkRoomEntity workRoomEntity;
        for (int i = 0; i < workRoomEntities.size(); i++) {
            workRoomEntity = workRoomEntities.get(i);
            toUserInfo = new ToUserInfoBO();
            toUserInfo.setWorkEmployeeId(req.getWorkEmployeeId());
            toUserInfo.setToUsertype(req.getToUsertype());
            toUserInfo.setToUserId(workRoomEntity.getId());
            toUserInfo.setName(workRoomEntity.getName());
            toUserInfo.setAlias("");
            toUserInfo.setAvatar("");

            Map<String, Object> sqlMap = new HashMap<>(4);
            sqlMap.put("tableName", WorkMsgHelper.getTableName(corpId));
            sqlMap.put("corpId", corpId);
            sqlMap.put("toType", req.getToUsertype());
            sqlMap.put("to", workRoomEntity.getWxChatId());

            setContentAndMsgTimeOfToUserInfoBO(toUserInfo, sqlMap);

            toUserInfoBOList.add(toUserInfo);
        }

        page.setRecords(toUserInfoBOList);

        return page;
    }

    private void setContentAndMsgTimeOfToUserInfoBO(ToUserInfoBO toUserInfoBO, Map<String, Object> sqlMap) {
        WorkMsgEntity workMsgEntity = workMsgMapper.selectToUserLastMsg(sqlMap);

        String content = "";
        String msgTime = "";

        if (null != workMsgEntity) {
            content = workMsgEntity.getContent();
            msgTime = workMsgEntity.getMsgTime();
            JSONObject contentJson = JSON.parseObject(content);
            if (contentJson.containsKey("item")) {
                content = "会话记录";
            } else {
                String msgType = contentJson.getString("type");
                MsgTypeEnum type = MsgTypeEnum.valueOf(msgType.toUpperCase());
                if (type != MsgTypeEnum.TEXT) {
                    content = type.getMsg();
                } else {
                    content = contentJson.getString("content");
                }
            }

            msgTime = DateUtils.formatS1(msgTime);
        }

        toUserInfoBO.setContent(content);
        toUserInfoBO.setMsgDataTime(msgTime);
    }

    /**
     * 会话内容存档 - 列表
     *
     * @return 聊天记录集合
     */
    @Override
    public Page<IndexMsgBO> index(ReqMsgIndexDTO req) {
        int corpId = AccountService.getCorpId();
        Page<IndexMsgBO> page = new Page<>();
        ApiRespUtils.initPage(page, req);

        WorkEmployeeEntity fromEmployee = workEmployeeMapper.selectById(req.getWorkEmployeeId());

        if (null == fromEmployee) {
            // 员工不存在
            return page;
        }

        String fromWxId = fromEmployee.getWxUserId();
        String fromName = fromEmployee.getName();
        String fromAvatar = fromEmployee.getAvatar();

        String toWxId = "";
        int toUserType = req.getToUserType();
        if (toUserType == 0) {
            toWxId = workEmployeeMapper.selectById(req.getToUserId()).getWxUserId();
        }
        if (toUserType == 1) {
            toWxId = workContactMapper.selectById(req.getToUserId()).getWxExternalUserid();
        }
        if (toUserType == 2) {
            toWxId = workRoomMapper.selectById(req.getToUserId()).getWxChatId();
        }

        Map<String, Object> selectParamMap = new HashMap<>(9);
        selectParamMap.put("tableName", WorkMsgHelper.getTableName(corpId));
        selectParamMap.put("corpId", corpId);
        selectParamMap.put("from", fromWxId);
        selectParamMap.put("msgType", req.getType());
        selectParamMap.put("to", toWxId);
        selectParamMap.put("toUserType", toUserType);
        selectParamMap.put("content", req.getContent());
        selectParamMap.put("dateTimeStart", req.getDateTimeStart());
        selectParamMap.put("dateTimeEnd", req.getDateTimeEnd());
        selectParamMap.put("page", req.getPage());
        selectParamMap.put("size", req.getPerPage());

        List<WorkMsgEntity> resultList = workMsgMapper.selectToUserMsg(selectParamMap);
        int total = workMsgMapper.selectToUserMsgCount(selectParamMap);

        List<IndexMsgBO> respList = new ArrayList<>();
        WorkEmployeeEntity tempEmployee = new WorkEmployeeEntity();
        WorkContactEntity tempContact = new WorkContactEntity();

        for (int i = 0; i < resultList.size(); i++) {
            WorkMsgEntity result = resultList.get(i);
            IndexMsgBO indexMsgBO = new IndexMsgBO();

            String resultFromWxId = result.getFrom();
            int isCurrentUser = fromWxId.equals(resultFromWxId) ? 1 : 0;

            indexMsgBO.setIsCurrentUser(isCurrentUser);
            indexMsgBO.setAction(result.getAction());

            int typeCode = result.getMsgType();
            typeCode = typeCode > 7 ? 100 : typeCode;

            indexMsgBO.setType(typeCode);
            JSONObject contentJson = JSON.parseObject(result.getContent());
            replaceOssPath(contentJson);

            if (contentJson.containsKey("item")) {
                JSONArray jsonArray = contentJson.getJSONArray("item");
                for (int j = 0; j < jsonArray.size(); j++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    replaceOssPath(jsonObject1);
                }
            }
            indexMsgBO.setContent(contentJson);

            String msgTime = result.getMsgTime();
            indexMsgBO.setMsgDataTime(DateUtils.formatS1(msgTime));

            String resultFromName;
            String resultFromAvatar;
            if (isCurrentUser == 1) {
                resultFromName = fromName;
                resultFromAvatar = fromAvatar;
            } else {
                if (WorkMsgBackUpUtil.isContact(resultFromWxId)) {
                    tempContact.setWxExternalUserid(resultFromWxId);
                    WorkContactEntity tempResultContact = workContactMapper.selectOne(new QueryWrapper<>(tempContact));
                    resultFromName = tempResultContact.getName();
                    resultFromAvatar = tempResultContact.getAvatar();
                } else {
                    tempEmployee.setWxUserId(resultFromWxId);
                    WorkEmployeeEntity tempResultEmployee = workEmployeeMapper.selectOne(new QueryWrapper<>(tempEmployee));
                    resultFromName = tempResultEmployee.getName();
                    resultFromAvatar = tempResultEmployee.getAvatar();
                }
            }

            indexMsgBO.setName(resultFromName);
            indexMsgBO.setAvatar(resultFromAvatar);

            respList.add(indexMsgBO);
        }

        int pages = total/req.getPerPage();
        if (total%req.getPerPage() > 0) {
            pages ++;
        }

        page.setRecords(respList);
        page.setTotal(total);
        page.setSize(req.getPerPage());
        page.setPages(pages);
        return page;
    }

    private void replaceOssPath(JSONObject jsonObject) {
        if (jsonObject.containsKey("ossPath")) {
            jsonObject.put("ossFullPath", AliyunOssUtils.getUrl(jsonObject.getString("ossPath")));
        }
    }

}
