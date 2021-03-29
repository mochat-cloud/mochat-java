package com.mochat.mochat.controller.medium;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.em.medium.TypeEnum;
import com.mochat.mochat.common.model.PageModel;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.common.validation.ToolInterface;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.medium.MediumEnyity;
import com.mochat.mochat.dao.entity.medium.MediumGroupEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.subsystem.GetSubSystemInfoResponse;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.ISubSystemService;
import com.mochat.mochat.service.impl.medium.IMediumGroupService;
import com.mochat.mochat.service.impl.medium.IMediumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @description:媒体库
 * @author: Huayu
 * @time: 2020/12/4 18:41
 */
@RestController
@RequestMapping("/gateway/mc/medium")
@Validated
public class MediumController {

    @Autowired
    private IMediumService mediumServiceImpl;

    @Autowired
    private IMediumGroupService mediumGroupServiceImpl;

    @Autowired
    private ISubSystemService subsystemServiceImpl;

    /**
     *
     *
     * @description: 查看
     * @return:
     * @author: Huayu
     * @time: 2020/12/6 10:59
     */
    @GetMapping(value="/show")
    public ApiRespVO mediumShow(@Validated MediumEnyity mediumEnyity){
        MediumEnyity medium = mediumServiceImpl.getMediumById(mediumEnyity.getId());
        Map<String,Object> objectMap = new HashMap<String,Object>();
        List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
        objectMap.put("id",medium.getId());
        objectMap.put("corpId",medium.getCorpId());
        objectMap.put("type",medium.getType());
        JSONObject jsonObject = JSON.parseObject(medium.getContent());
        objectMap.put("content",jsonObject);
        objectMap.put("mediumGroupId",medium.getMediumGroupId());
        listMap.add(objectMap);
        return ApiRespUtils.getApiRespOfOk(objectMap);
    }

    /**
     *
     *
     * @description: 列表
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 13:21
     */
    @GetMapping(value="/index")
    public ApiRespVO  mediumIndex(String mediumGroupId,String searchStr,Integer type,Integer page,Integer perPage){
        if(type == null){
            type = 0;
        }
        Integer pageNo = (page == null)?0:page-1;
        Integer pageCount = (perPage == null)?10:perPage;
        List<MediumEnyity> mediumList= mediumServiceImpl.getMediumList(mediumGroupId,searchStr,type,pageNo,pageCount);

        //响应数据处理
        List<Map<String,Object>> listMapList = new ArrayList<Map<String,Object>>();
        Map<String,Object> mapList = new HashMap<String,Object>();
        int totalPageNum = (mediumList.size() +  pageCount  - 1) / pageCount;
        mapList.put("page",new PageModel(pageCount,mediumList.size(),totalPageNum));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Map<String,Object> listMap = null;
        for (int i = 0; i < mediumList.size(); i++) {
            listMap = new HashMap<String,Object>();
            listMap.put("id",mediumList.get(i).getId());
            listMap.put("type", TypeEnum.getTypeByCode(mediumList.get(i).getType()));
            String fullUrl = mediumServiceImpl.addFullPath(mediumList.get(i).getContent(),mediumList.get(i).getType());
            JSONObject jsonObject = JSON.parseObject(fullUrl);
            listMap.put("content",jsonObject);
            //listMap.put("title",jsonObject.get("title"));
            listMap.put("mediumGroupId",mediumList.get(i).getMediumGroupId());
            listMap.put("corpId",mediumList.get(i).getCorpId());
            List<MediumGroupEntity> mediumGroupList = mediumGroupServiceImpl.getMediumGroupsById(mediumList.get(i).getMediumGroupId().toString());
            listMap.put("userId",mediumList.get(i).getUserId());
            listMap.put("userName",mediumList.get(i).getUserName());
            listMap.put("createdAt", format.format(mediumList.get(i).getCreatedAt()));
            if(mediumGroupList.size() > 0 ){
                listMap.put("mediumGroupName",mediumGroupList.get(0).getName());
              }else{
                listMap.put("mediumGroupName","");
            }
            listMapList.add(listMap);
        }
        mapList.put("list",listMapList);
        //分页参数
        return ApiRespUtils.getApiRespOfOk(mapList);
    }

    /**
     *
     *
     * @description: 添加素材库
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 14:23
     */
    @PostMapping(value="/store")
    public ApiRespVO mediumStore(@Validated(ToolInterface.mediumStore.class) @RequestBody MediumEnyity mediumEnyity){
        mediumEnyity.setCorpId(AccountService.getCorpId());
        mediumEnyity.setUserId(AccountService.getUserId());
        GetSubSystemInfoResponse getSubSystemInfoResponse = subsystemServiceImpl.getSubSystemInfo(AccountService.getUserId());
        mediumEnyity.setUserName(getSubSystemInfoResponse.getUserName());
        mediumEnyity.setContent(JSON.parseObject(mediumEnyity.getContent()).toJSONString());
        Integer i = mediumServiceImpl.createMedium(mediumEnyity);
        if(i <= 0){
            throw new CommonException(100014,"添加失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     *
     *
     * @description:删除
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 14:46
     */
    @DeleteMapping("/destroy")
    public  ApiRespVO mediumDestroy(@RequestBody Map<String,Object> mapData){
        Integer i = mediumServiceImpl.deleteMedium(Integer.valueOf(mapData.get("id").toString()));
        if(i <= 0){
            throw new CommonException(100014,"添加失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }


    /**
     *
     *
     * @description: 移动分组
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 15:00
     */
    @PutMapping("/groupUpdate")
    public ApiRespVO mediumGroupUpdate(@RequestBody Map<String,Object> mapData){
        if(mapData.get("id") == null || mapData.get("id").equals("")){
            throw new ParamException(100013,"素材ID不能为空");
        }
        if(mapData.get("mediumGroupId") == null || mapData.get("mediumGroupId").equals("")){
            throw new ParamException(100013,"素材分组ID不能为空");
        }
        MediumEnyity mediumEnyity = new MediumEnyity();
        mediumEnyity.setId(Integer.valueOf(mapData.get("id").toString()));
        mediumEnyity.setMediumGroupId(Integer.valueOf(mapData.get("mediumGroupId").toString()));
        boolean b = mediumServiceImpl.updateMediumById(mediumEnyity);
        if(!b){
            throw new CommonException(100014,"移动失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     *
     *
     * @description: 修改
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 15:12
     */
    @PutMapping("/update")
    public ApiRespVO mediumUpdate(@RequestBody @Validated(ToolInterface.mediumStore.class)  MediumEnyity mediumEnyity,HttpServletRequest request){
        Integer userId = AccountService.getUserId();
        mediumEnyity.setUserId(userId);
        GetSubSystemInfoResponse subSystemInfoResponse = subsystemServiceImpl.getSubSystemInfo(userId);
        mediumEnyity.setUserName(subSystemInfoResponse.getUserName());
        mediumEnyity.setContent(JSON.parseObject(mediumEnyity.getContent()).toJSONString());
        boolean b = mediumServiceImpl.updateMediumById(mediumEnyity);
        if(!b){
            throw new CommonException(100014,"修改失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }
}
