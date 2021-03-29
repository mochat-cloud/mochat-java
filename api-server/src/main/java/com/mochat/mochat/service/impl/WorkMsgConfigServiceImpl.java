package com.mochat.mochat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.em.sensitivewordsmonitor.ReceiverTypeEnum;
import com.mochat.mochat.common.em.sensitivewordsmonitor.SourceEnum;
import com.mochat.mochat.common.em.workmessage.MsgTypeEnum;
import com.mochat.mochat.dao.entity.WorkContactEntity;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.WorkRoomEntity;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgEntity;
import com.mochat.mochat.dao.mapper.wm.WorkMsgConfigMapper;
import com.mochat.mochat.dao.mapper.wm.WorkMsgMapper;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordsMonitorService;
import com.mochat.mochat.service.wm.IWorkMsgService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:会话内容配置实现类
 * @author: Huayu
 * @time: 2020/11/25 11:43
 */
@Service
public class WorkMsgConfigServiceImpl extends ServiceImpl<WorkMsgConfigMapper, WorkMsgConfigEntity> implements IWorkMsgConfigService {

    @Resource
    private WorkMsgMapper workMsgMapper;

    @Autowired
    private ISensitiveWordService sensitiveWordServiceImpl;

    @Autowired
    private IWorkEmployeeService workEmployeeServiceImpl;

    @Autowired
    private IWorkContactService workContactServiceImpl;

    @Autowired
    private IWorkRoomService workRoomServiceImpl;

    @Autowired
    private IWorkMsgService workMsgServiceImpl;

    @Autowired
    private ISensitiveWordsMonitorService sensitiveWordsMonitorServiceImpl;

    @Override
    public WorkMsgConfigEntity getByCorpId(Integer corpId) {
        return lambdaQuery()
                .eq(WorkMsgConfigEntity::getCorpId, corpId)
                .one();
    }

    @Override
    public List<WorkMsgConfigEntity> getAllAble() {
        return baseMapper.selectAll();
    }

    @Override
    public boolean createWorkMessageConfig(WorkMsgConfigEntity workMsgConfigEntity) {
        return this.save(workMsgConfigEntity);
    }

    @Override
    public void getWorkMsgByCorpId(String corpIds, String s) {
        String[] corpIdArr = corpIds.split(",");
        Map<Integer, List<WorkMsgEntity>> map = null;
        for (String string:
                corpIdArr) {
            int corpId = Integer.valueOf(string) % 10;
            List<WorkMsgEntity> workMsgConfigEntityList = workMsgMapper.selectMsgByCorpId("mc_work_message_"+corpId,String.valueOf(corpId));
            //根据corpId分组
            map = workMsgConfigEntityList.stream().collect(Collectors.groupingBy(WorkMsgEntity::getCorpId));
        }
        //查询企业全部敏感词
        List<SensitiveWordEntity> sensitiveWordEntityList = sensitiveWordServiceImpl.getSensitiveWordsByCorpIdStatus(corpIdArr,1,"id,corp_id,name,employee_num,contact_num");
        //对敏感词按企业分类
        Map<Integer,List<SensitiveWordEntity>> corpMap = sensitiveWordEntityList.stream().collect(Collectors.groupingBy(SensitiveWordEntity::getCorpId));
        //敏感词触发次数统计
        Integer updateWords = 0;
        //敏感词触发记录
        Integer createMonitors = 0;
        //查询消息数据列表-根据企业ID
        Map map2 = null;
        List<Map> map2List = new ArrayList();
        for (Map.Entry<Integer, List<WorkMsgEntity>> map1:
                map.entrySet()) {
            Integer key = map1.getKey();
            List<SensitiveWordEntity> sensitiveWordEntityList1 = null;
            if(corpMap.get(key) != null){
                sensitiveWordEntityList1 = (List<SensitiveWordEntity>)corpMap.get(key);
            }else{
                continue;
            }
            List<WorkMsgEntity> workMsgEntityList = workMsgServiceImpl.getWorkMessagesByMsgId(map1.getValue());
            if(workMsgEntityList.isEmpty()){
                continue;
            }
            map2 = new HashMap();
            map2 = matchWords(workMsgEntityList,sensitiveWordEntityList1,key);
            map2List.add(map2);
        }
        //将数据入表
        insertData(map2List);
    }


    /**
     *
     *
     * @description:敏感词统计数据
     * @return:
     * @author: Huayu
     * @time: 2021/3/11 8:41
     */
    private void insertData(List<Map> map2List) {
        for (Map map:
                map2List) {
            if(map.get("updateWords") != null){
                for (Object k: map.keySet())
                {
                    sensitiveWordServiceImpl.updateSensitiveWordById(k,((SensitiveWordEntity)map.get(k)).getEmployeeNum(),((SensitiveWordEntity)map.get(k)).getContactNum());
                }
            }
            if(map.get("createMonitors") != null){
                //创建触发敏感词记录
                sensitiveWordsMonitorServiceImpl.createSensitiveWordMonitors(map);
            }
        }

    }


    /**
     *
     *
     * @description: 企业设置敏感词列表
     * @return:
     * @author: Huayu
     * @time: 2021/3/9 18:15
     */
    private Map matchWords(List<WorkMsgEntity> workMsgEntityList, List<SensitiveWordEntity> sensitiveWordEntityList1, Integer corpId) {
        //敏感词触发次数统计
        Map updateWords = new HashMap();
        Map createMonitors = null;
        List<Map> updateWordsMap = new ArrayList();
        //敏感词触发记录
        Map<String,Object> matchWordsMap = new HashMap();
        //获得信息发送方|接收方信息
        List<String> participantIdArr = new ArrayList();
        List<String> wxRoomIdArr = new ArrayList();;
        for (WorkMsgEntity workMsgEntity:
                workMsgEntityList) {
            participantIdArr.add(workMsgEntity.getFrom());
            participantIdArr.add(workMsgEntity.getTolist());
            if(workMsgEntity.getWxRoomId() != null){
                wxRoomIdArr.add(workMsgEntity.getWxRoomId());
            }
        }
        Map userListMap = getEmployeeList(corpId,participantIdArr);
        Map contactListMap = getContactList(corpId,participantIdArr);
        //触发场景群聊
        Map roomListMap = getRoomList(wxRoomIdArr);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map> createMonitorsMap = new ArrayList();
        for (WorkMsgEntity message:
                workMsgEntityList) {
            //过滤非文本信息
            if(!message.getMsgType().equals(MsgTypeEnum.TEXT.getCode())){
                continue;
            }
            //过滤-非本公司员工|客户触发的信息
            if((userListMap.get(message.getFrom()) == null) && (contactListMap.get(message.getFrom()) == null)){
                continue;
            }
            //过滤内部群信息
            if(!message.getWxRoomId().isEmpty() && (roomListMap.get(message.getWxRoomId()) == null)){
                continue;
            }
            if(message.getContent() == null || message.getContent().isEmpty()){
                continue;
            }
            //触发来源
            WorkEmployeeEntity triggerInfo = null;
            WorkContactEntity triggerInfo1 = null;
            if(userListMap.get(message.getFrom()) != null){
                triggerInfo = (WorkEmployeeEntity)userListMap.get(message.getFrom());
            }else{
                triggerInfo1 = (WorkContactEntity)contactListMap.get(message.getFrom());
            }
            //接收者类型
            Integer receiverType = null;
            Integer receiverId = null;
            String receiverName = null;
            if(!message.getWxRoomId().isEmpty()){
                receiverType = ReceiverTypeEnum.ROOM.getCode();
                receiverId = ((WorkRoomEntity)roomListMap.get(message.getWxRoomId())).getId();
                receiverName = ((WorkRoomEntity)roomListMap.get(message.getWxRoomId())).getName();
            }else if(userListMap.get(message.getTolist()) != null){
                receiverType = ReceiverTypeEnum.EMPLOYEE.getCode();
                receiverId = ((WorkEmployeeEntity)userListMap.get(message.getTolist())).getId();
                receiverName = ((WorkEmployeeEntity)userListMap.get(message.getTolist())).getName();
            }else{
                receiverType = ReceiverTypeEnum.CONTACT.getCode();
                receiverId = ((WorkContactEntity)userListMap.get(message.getTolist())).getId();
                receiverName = ((WorkContactEntity)userListMap.get(message.getTolist())).getName();
            }
            Map<String,Object> baseMonitorMap = new HashMap();
            baseMonitorMap.put("corp_id",String.valueOf(corpId));
            baseMonitorMap.put("source",(userListMap.get(message.getFrom()) != null) ? SourceEnum.EMPLOYEE.getCode() :SourceEnum.CONTACT.getCode());
            Integer id = null;
            String name = null;
            if(triggerInfo != null){
                id = triggerInfo.getId();
                name = triggerInfo.getName();
            }else{
                id = triggerInfo1.getId();
                name = triggerInfo1.getName();
            }
            baseMonitorMap.put("trigger_id",id);
            baseMonitorMap.put("trigger_name",name);
            baseMonitorMap.put("receiver_type",receiverType);
            baseMonitorMap.put("receiver_id",receiverId);
            baseMonitorMap.put("receiver_name",receiverName);
            baseMonitorMap.put("trigger_time",formatter.format(new Date(Long.parseLong(String.valueOf(message.getMsgTime())))));
            baseMonitorMap.put("work_message_id",message.getId());
            baseMonitorMap.put("chat_content",getChatContent(String.valueOf(corpId),message));
            baseMonitorMap.put("created_at",formatter.format(System.currentTimeMillis()));
            SensitiveWordEntity sensitiveWord = null;
            for (SensitiveWordEntity sensitiveWordEntity:
                    sensitiveWordEntityList1) {
                //监测-是否触发敏感词
                if(!message.getContent().contains(sensitiveWordEntity.getName())){
                    continue;
                }

                sensitiveWord = new SensitiveWordEntity();
                sensitiveWord.setId(sensitiveWordEntity.getId());
                sensitiveWord.setEmployeeNum(sensitiveWordEntity.getEmployeeNum());
                sensitiveWord.setContactNum(sensitiveWordEntity.getContactNum());
                updateWords.put(sensitiveWordEntity.getId(),sensitiveWord);
                //触发来源
                if(userListMap.get(message.getFrom()) != null){
                    Integer i = ((SensitiveWordEntity)updateWords.get(sensitiveWordEntity.getId())).getEmployeeNum();
                    ((SensitiveWordEntity)updateWords.get(sensitiveWordEntity.getId())).setEmployeeNum(i++);
                }else{
                    Integer i = ((SensitiveWordEntity)updateWords.get(sensitiveWordEntity.getId())).getContactNum();
                    ((SensitiveWordEntity)updateWords.get(sensitiveWordEntity.getId())).setContactNum(i++);
                }
                createMonitors = new HashMap();
                baseMonitorMap.put("sensitive_word_id",sensitiveWordEntity.getId());
                baseMonitorMap.put("sensitive_word_name",sensitiveWordEntity.getName());
                createMonitors = baseMonitorMap;
                createMonitorsMap.add(createMonitors);
            }
        }
        matchWordsMap.put("updateWords",updateWordsMap);
        matchWordsMap.put("createMonitors",createMonitorsMap);
        return matchWordsMap;
    }


    /**
     *
     *
     * @description:单条会话信息
     * @return:
     * @author: Huayu
     * @time: 2021/3/10 14:46
     */
    private Map getChatContent(String corpId, WorkMsgEntity message) {
        List<WorkMsgEntity> beforeList = null;
        List<WorkMsgEntity> afterList = null;
        if(!message.getWxRoomId().isEmpty()){
            beforeList = new ArrayList();
            afterList = new ArrayList();
            //查询此条信息前十条
            beforeList = workMsgServiceImpl.getWorkMessagesRangeByCorpIdWxRoomId(corpId,message.getWxRoomId(),message.getId(),0,"id,from,msg_type,content,msg_time");
            //查询此条信息后十条
            afterList = workMsgServiceImpl.getWorkMessagesRangeByCorpIdWxRoomId(corpId,message.getWxRoomId(),message.getId(),1,"id,from,msg_type,content,msg_time");
        }else{
            beforeList = new ArrayList();
            afterList = new ArrayList();
            //查询此条信息前十条
            beforeList = workMsgServiceImpl.getWorkMessagesRangeByCorpId(corpId,message.getFrom(),message.getTolist(),message.getId(),0,"id,from,msg_type,content,msg_time");
            //查询此条信息后十条
            afterList = workMsgServiceImpl.getWorkMessagesRangeByCorpId(corpId,message.getFrom(),message.getTolist(),message.getId(),1,"id,from,msg_type,content,msg_time");
        }
        beforeList.addAll(afterList);
        List<String> fromIdArr = new ArrayList();
        for (WorkMsgEntity workMsgEntity:
                beforeList) {
            fromIdArr.add(workMsgEntity.getFrom());
        }
        Map fromUserList = getEmployeeList(Integer.valueOf(corpId), fromIdArr);
        Map fromContactList = getContactList(Integer.valueOf(corpId), fromIdArr);
        Map map = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (WorkMsgEntity workMsgEntity:
                beforeList) {
            map = new HashMap();
            map.put("isTrigger",workMsgEntity.getId().equals(message.getId()) ? 1 : 0);
            map.put("sender",handleMessageSender(workMsgEntity,fromUserList,fromContactList));
            map.put("sendTime",formatter.format(new Date(Long.parseLong(String.valueOf(message.getMsgTime())))));
            map.put("msgType",workMsgEntity.getMsgType());
            map.put("msgContent",workMsgEntity.getContent());
        }
        return map;
    }


    /**
     *
     *
     * @description:会话消息 触发员工信息 触发客户信息
     * @return:
     * @author: Huayu
     * @time: 2021/3/10 18:13
     */
    private String handleMessageSender(WorkMsgEntity workMsgEntity, Map fromUserList, Map fromContactList) {
        String sender = "系统";
        //发送人微信唯一标识前缀
        String prefixFrom = workMsgEntity.getFrom().substring(0,workMsgEntity.getFrom().length()-1) + workMsgEntity.getFrom().substring(1,workMsgEntity.getFrom().length()-2);
        //机器人
        if(prefixFrom.equals("we")){
            sender = "机器人";
            //客户
        }else if(prefixFrom.equals("wo") || prefixFrom.equals("wm")){
            sender = fromContactList.get(workMsgEntity.getFrom()) != null ? ((WorkContactEntity)fromContactList.get(workMsgEntity.getFrom())).getName():workMsgEntity.getFrom();
            //员工
        }else{
            sender = fromContactList.get(workMsgEntity.getFrom()) != null ? ((WorkEmployeeEntity)fromUserList.get(workMsgEntity.getFrom())).getName():workMsgEntity.getFrom();
        }
        return sender;
    }


    /**
     *
     *
     * @description:微信群聊ID
     * @return:
     * @author: Huayu
     * @time: 2021/3/10 9:36
     */
    private Map getRoomList(List<String> wxRoomIdArr) {
        List<WorkRoomEntity> workRoomEntityList = workRoomServiceImpl.getWorkRoomsByChatId(wxRoomIdArr,"id, wx_chat_id, name");
        Map<String,Object> map = null;
        for (WorkRoomEntity workRoomEntity:
                workRoomEntityList) {
            map = new HashMap();
            map.put(workRoomEntity.getWxChatId(),workRoomEntity);
        }
        return map;
    }


    /**
     *
     *
     * @description:根据微信外部联系人ID获取外部联系人
     * @return:
     * @author: Huayu
     * @time: 2021/3/9 18:36
     */
    private Map<String, Object> getContactList(Integer corpId, List<String> participantIdArr) {
        List<WorkContactEntity> workContactEntityList = workContactServiceImpl.getWorkContactByCorpIdWxExternalUserIds(corpId,participantIdArr,"id,wx_external_userid,name");
        Map<String,Object> map = null;
        //组装map并返回
        for (WorkContactEntity workContactEntity:
                workContactEntityList) {
            map = new HashMap();
            map.put(workContactEntity.getWxExternalUserid(),workContactEntity);
        }
        return map;
    }


    /**
     *
     *
     * @description:根据微信用户Id获取员工
     * @return:
     * @author: Huayu
     * @time: 2021/3/9 18:36
     */
    private Map<String, Object>  getEmployeeList(Integer corpId, List<String> participantIdArr) {
        List<WorkEmployeeEntity> workEmployeeEntityList = workEmployeeServiceImpl.getWorkEmployeesByCorpIdsWxUserId(corpId,participantIdArr,"id,wx_user_id,name");
        Map<String,Object> map = null;
        //组装map并返回
        for (WorkEmployeeEntity workEmployeeEntity:
                workEmployeeEntityList) {
            map = new HashMap();
            map.put(workEmployeeEntity.getWxUserId(),workEmployeeEntity);
        }
        return map;
    }

}
