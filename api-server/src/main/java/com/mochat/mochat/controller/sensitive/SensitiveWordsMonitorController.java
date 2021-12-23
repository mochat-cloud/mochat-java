package com.mochat.mochat.controller.sensitive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordsMonitorEntity;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.model.sensitiveword.ReqSensitiveWordsMonitorIndex;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordsMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:敏感词监控
 * @author: Huayu
 * @time: 2021/2/4 9:40
 */
@RestController
@RequestMapping(path = "/sensitiveWordsMonitor")
public class SensitiveWordsMonitorController {


    @Autowired
    private ISensitiveWordsMonitorService sensitiveWordsMonitorImpl;


    @GetMapping("/index")
    public ApiRespVO sensitiveWordsMonitorIndex(ReqSensitiveWordsMonitorIndex reqSensitiveWordsMonitorIndex){
//        List<SensitiveWordsMonitorEntity> sensitiveWordsMonitorEntityList =  sensitiveWordsMonitorImpl.handle(reqSensitiveWordsMonitorIndex);
//        Integer perPage = reqSensitiveWordsMonitorIndex.getPerPage();
//        perPage = (perPage == null || perPage.equals("")) ? 10 : perPage;
//        Integer pageNum = reqSensitiveWordsMonitorIndex.getPage();
//        pageNum = (pageNum == null || pageNum.equals("")) ? 1 : pageNum;
//        Map<String,Object> mapData = new HashMap<>();
//        Integer totalPage = sensitiveWordsMonitorEntityList.size()/pageNum;
//        mapData.put("page",new PageModel(perPage,sensitiveWordsMonitorEntityList.size(),totalPage));
//        mapData.put("list",sensitiveWordsMonitorEntityList);
        return ApiRespUtils.ok(null);
    }

    @GetMapping("/show")
    public ApiRespVO sensitiveWordsMonitorShow(@RequestParam(value = "sensitiveWordsMonitorId") @NotNull(message = "敏感词监控ID不能为空")  String sensitiveWordsMonitorId){
        //获取敏感词监控信息
        SensitiveWordsMonitorEntity sensitiveWordsMonitorEntity = sensitiveWordsMonitorImpl.getSensitiveWordMonitorById(sensitiveWordsMonitorId);
        if(sensitiveWordsMonitorEntity.getChatContent() == null ){
            throw new ParamException(100013,"当前敏感词监控信息不存在");
        }
        String content = sensitiveWordsMonitorEntity.getChatContent();
        if(content != null && !content.equals("")){
            JSONObject jsonObject = JSON.parseObject(content);
            JSONArray jsonArray = JSON.parseArray(content);
            List mapDataList = new ArrayList();
            if(jsonArray != null && jsonArray.size() > 0){
                Map<String,Object> mapData = new HashMap<String,Object>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    mapData = sensitiveWordsMonitorImpl.contentFormat(jsonObject1);
                    mapDataList.add(mapData);
                }
            }
            return ApiRespUtils.ok(mapDataList);
        }else{
            return ApiRespUtils.ok("");
        }
    }
}
