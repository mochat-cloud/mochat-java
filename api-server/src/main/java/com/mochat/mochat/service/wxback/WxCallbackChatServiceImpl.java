package com.mochat.mochat.service.wxback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.model.workroom.WXWorkRoomIdsModel;
import com.mochat.mochat.model.workroom.WXWorkRoomInfoModel;
import com.mochat.mochat.model.workroom.WXWorkRoomModel;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.workroom.IWorkRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/12/21 9:40
 */
@Service
public class WxCallbackChatServiceImpl implements IWxCallbackChatService{

    @Autowired
    private ICorpService corpServiceImpl;

    @Autowired
    private IWorkRoomService workRoomServiceImpl;

    @Override
    public String updateChatCallBack(String dataJson){
        JSONObject jsonObject = JSON.parseObject(dataJson);
        JSONObject xmlJsonObject = jsonObject.getJSONObject("xml");
        String wxCorpId = xmlJsonObject.getString("ToUserName");
        String chatId = xmlJsonObject.getString("CHAT_ID");
        CorpEntity corpEntity = new CorpEntity();
        if(chatId != null || !chatId.equals("")){
            //获取群聊详情
            corpEntity.setWxCorpId(wxCorpId);
            String workRoomInfo = WxApiUtils.getWorkRoomInfoData(corpEntity,chatId);
            JSONObject workRoomInfoJson = JSONObject.parseObject(workRoomInfo);
            String ownerId = workRoomInfoJson.get("owner").toString();
            //List<WXWorkRoomInfoModel> resultInfoList = jsonArray.toJavaList(WXWorkRoomInfoModel.class);
            //获取客户群列表
            String workRoomIndexData = WxApiUtils.getWorkRoomIndexData(corpEntity,ownerId);
            List<WXWorkRoomIdsModel> workRoomModelList = JSONArray.parseArray(workRoomIndexData, WXWorkRoomIdsModel.class);
            List<WXWorkRoomModel> WXWorkRoomModelList = new ArrayList<WXWorkRoomModel>();
            for (WXWorkRoomIdsModel workRoomModel:
                    workRoomModelList) {
                WXWorkRoomModel WXWorkRoomModel = new WXWorkRoomModel();
                WXWorkRoomModel.setChatId(workRoomModel.getChatId());
                WXWorkRoomModel.setStatus(workRoomModel.getStatus());
                //获取群聊详情
                String workRoomInfoData = WxApiUtils.getWorkRoomInfoData(corpEntity,workRoomModel.getChatId());
                //合并客户群和客户群详情数据
                //取出member_list
                JSONObject workRoomInfoJsonData = JSONObject.parseObject(workRoomInfoData);
                WXWorkRoomModel.setName(workRoomInfoJsonData.get("name").toString());
                WXWorkRoomModel.setOwner(workRoomInfoJsonData.get("owner").toString());
                WXWorkRoomModel.setCreateTime(Timestamp.valueOf(workRoomInfoJsonData.get("create_time").toString()));
                WXWorkRoomModel.setNotice(workRoomInfoJsonData.get("notice").toString());
                JSONArray jsonArray= workRoomInfoJsonData.getJSONArray("member_list");
                List<WXWorkRoomInfoModel> resultInfoList = jsonArray.toJavaList(WXWorkRoomInfoModel.class);
                WXWorkRoomModel.setWXWorkRoomInfoModel(resultInfoList);
                WXWorkRoomModelList.add(WXWorkRoomModel);
            }
        //查询企业授信信息
            CorpEntity corpEntity1 = corpServiceImpl.getCorpsByWxCorpId(wxCorpId,"id");
            if(corpEntity1 != null){
                try {
                    workRoomServiceImpl.syncWorkRoomIndex(corpEntity1.getCorpId(),WXWorkRoomModelList,1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }
}
