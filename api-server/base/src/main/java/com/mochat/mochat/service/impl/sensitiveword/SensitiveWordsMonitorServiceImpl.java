package com.mochat.mochat.service.impl.sensitiveword;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mochat.mochat.common.util.ali.AliyunOssUtils;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordEntity;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordsMonitorEntity;
import com.mochat.mochat.dao.mapper.sensitiveword.SensitiveWordsMonitorMapper;
import com.mochat.mochat.dao.model.ReqSensitiveWordsMonitorIndex;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordsMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description:敏感词监控 - 列表
 * @author: Huayu
 * @time: 2021/2/4 10:33
 */
@Service
public class SensitiveWordsMonitorServiceImpl extends ServiceImpl<SensitiveWordsMonitorMapper, SensitiveWordsMonitorEntity> implements ISensitiveWordsMonitorService {


    private Logger logger = LoggerFactory.getLogger(SensitiveWordsMonitorServiceImpl.class);

    @Resource
    private SensitiveWordsMonitorMapper sensitiveWordsMonitorMapper;

    @Autowired
    private ISensitiveWordService sensitiveWordServiceImpl;

    @Override
    public List<SensitiveWordsMonitorEntity> handle(ReqSensitiveWordsMonitorIndex reqSensitiveWordsMonitorIndex) {
        //处理请求参数
        return handleParams(reqSensitiveWordsMonitorIndex);
    }

    @Override
    public SensitiveWordsMonitorEntity getSensitiveWordMonitorById(String sensitiveWordsMonitorId) {
        QueryWrapper<SensitiveWordsMonitorEntity> sensitiveWordsMonitorEntityQueryWrapper = new QueryWrapper();
        sensitiveWordsMonitorEntityQueryWrapper.select("chat_content");
        sensitiveWordsMonitorEntityQueryWrapper.eq("id",sensitiveWordsMonitorId);
        return this.baseMapper.selectOne(sensitiveWordsMonitorEntityQueryWrapper);
    }


    @Override
    public Map<String,Object> contentFormat(JSONObject jsonObject1) {
        Map<String,Object> mapData = new HashMap<String,Object>();
        //文字
        String content = jsonObject1.getString("msgContent");
        if(content != null && !content.equals("")){
            mapData.put("content",content);
        }
        //图片全地址
        String ossPath = jsonObject1.getString("ossPath");
        if(ossPath != null && !ossPath.equals("")){
            mapData.put("ossFullPath",AliyunOssUtils.getUrl(ossPath));
        }
        //其他类型
        Integer msgType  = Integer.valueOf(jsonObject1.get("msgType").toString());
        switch (msgType){
            case 24://混合型
                mapData = contentItemFormat(jsonObject1);
                break;
            case 11://地点
                mapData.put("title",jsonObject1.get("title").toString());
                mapData.put("address",jsonObject1.get("address").toString());
                break;
            case 6://小程序
                mapData.put("displayname",jsonObject1.get("displayname").toString());
                mapData.put("title",jsonObject1.get("title").toString());
                mapData.put("description",jsonObject1.get("description").toString());
                break;
            case 10://个人名片
                mapData.put("corpname",jsonObject1.get("corpname").toString());
                mapData.put("userid",jsonObject1.get("userid").toString());
                break;
            case 19://[红包]总个数
                mapData.put("totalcnt",jsonObject1.get("totalcnt").toString());
                mapData.put("totalamount",Integer.valueOf(jsonObject1.get("totalamount").toString())*0.01);
                break;
            case 21://在线文档
                mapData.put("title",jsonObject1.get("title").toString());
                mapData.put("doc_creator",jsonObject1.get("doc_creator").toString());
                mapData.put("link_url",jsonObject1.get("link_url").toString());
                break;
        }
        return mapData;
    }



    @Override
    public void createSensitiveWordMonitors(Map map) {
        SensitiveWordsMonitorEntity sensitiveWordMonitor = new SensitiveWordsMonitorEntity();
        for (Object k: map.keySet())
        {
            if(k.toString().equals("trigger_id")){
                sensitiveWordMonitor.setTriggerId((Integer)map.get(k));
            }
            if(k.toString().equals("trigger_name")){
                sensitiveWordMonitor.setTriggerName((String)map.get(k));
            }
            if(k.toString().equals("receiver_type")){
                sensitiveWordMonitor.setReceiverType((Integer)map.get(k));
            }
            if(k.toString().equals("receiver_id")){
                sensitiveWordMonitor.setReceiverId((Integer)map.get(k));
            }
            if(k.toString().equals("receiver_name")){
                sensitiveWordMonitor.setReceiverName((Integer) map.get(k));
            }
            if(k.toString().equals("trigger_time")){
                sensitiveWordMonitor.setTriggerTime((Date)map.get(k));
            }
            if(k.toString().equals("work_message_id")){
                sensitiveWordMonitor.setWorkMessageId((Integer)map.get(k));
            }
            if(k.toString().equals("chat_content")){
                sensitiveWordMonitor.setChatContent((String)map.get(k));
            }
            if(k.toString().equals("created_at")){
                sensitiveWordMonitor.setCreatedAt((Date)map.get(k));
            }
            if(k.toString().equals("sensitive_word_id")){
                sensitiveWordMonitor.setSensitiveWordId((Integer)map.get(k));
            }
            if(k.toString().equals("sensitive_word_name")){
                sensitiveWordMonitor.setSensitiveWordName((Integer)map.get(k));
            }
        }
        Integer i = this.baseMapper.insert(sensitiveWordMonitor);
        if(i > 0){
            logger.info("会话信息监测敏感词触发入表成功");
        }else{
            logger.info("会话信息监测敏感词触发入表失败");
        }
    }


    private Map<String, Object> contentItemFormat(JSONObject jsonObject1) {
        JSONArray jsonArray = jsonObject1.getJSONArray("item");
        String type = null;
        List<Map<String, Object>> mapDataList = new ArrayList<Map<String, Object>>();
        Map<String, Object> mapDataList2 = new HashMap<String,Object>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Map<String, Object> mapData = new HashMap<String,Object>();
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            type = jsonObject2.getString("type");
            switch (type) {
                case "text":
                    mapData.put("content", "混合消息-文本");
                    break;
                case "image":
                case "emotion":
                    String ossPath = jsonObject1.getString("ossPath");
                    if (ossPath != null && !ossPath.equals("")) {
                        mapData.put("ossFullPath", AliyunOssUtils.getUrl(ossPath));
                        break;
                    }
            }
            mapDataList.add(mapData);
        }
        mapDataList2.put("item",mapDataList);
        return mapDataList2;
    }


    private List<SensitiveWordsMonitorEntity> handleParams(ReqSensitiveWordsMonitorIndex reqSensitiveWordsMonitorIndex) {
        Integer pageNum = reqSensitiveWordsMonitorIndex.getPage();
        pageNum = (pageNum == null || pageNum.equals("")) ? 1 : pageNum;
        Integer perPage = reqSensitiveWordsMonitorIndex.getPerPage();
        perPage = (perPage == null || perPage.equals("")) ? 10 : perPage;
        reqSensitiveWordsMonitorIndex.setPage(pageNum);
        reqSensitiveWordsMonitorIndex.setPerPage(perPage);
        //公司信息
        reqSensitiveWordsMonitorIndex.setCorpId(AccountService.getCorpId());
        //敏感词分组
        if(reqSensitiveWordsMonitorIndex.getIntelligentGroupId() != null && !reqSensitiveWordsMonitorIndex.getIntelligentGroupId().equals("")){
            List<SensitiveWordEntity> sensitiveWordEntityList = sensitiveWordServiceImpl.getSensitiveWordList(reqSensitiveWordsMonitorIndex.getIntelligentGroupId());
            StringBuilder sb =  new StringBuilder();
            String sensitiveIdStr = null;
            for (SensitiveWordEntity sensitiveWordEntity:
            sensitiveWordEntityList) {
                sensitiveIdStr = sensitiveWordEntity.getId().toString();
                sensitiveIdStr = sensitiveIdStr + ",";
                sb.append(sensitiveIdStr);
            }
            sensitiveIdStr = sb.substring(0,sb.length()-1);
            reqSensitiveWordsMonitorIndex.setSensitiveWordIds(sensitiveIdStr);
        }
        //员工信息
        if(reqSensitiveWordsMonitorIndex.getEmployeeId() != null && reqSensitiveWordsMonitorIndex.getEmployeeId().length() > 0){
            String[] employeeIdArr = reqSensitiveWordsMonitorIndex.getEmployeeId().split(",");
        }
        List<SensitiveWordsMonitorEntity> sensitiveWordsMonitorEntityList = sensitiveWordsMonitorMapper.getSensitiveWordMonitorList(reqSensitiveWordsMonitorIndex);
        return sensitiveWordsMonitorEntityList;
    }
}
