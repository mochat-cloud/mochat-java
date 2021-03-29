package com.mochat.mochat.controller.sensitive;

import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordGroupEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.sensitiveword.RespSensitiveWordGroupIndex;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:敏感词分组管理
 * @author: Huayu
 * @time: 2021/1/28 14:00
 */
@RestController
@Validated
@RequestMapping(path = "/gateway/mc/sensitiveWordGroup")
public class SensitiveWordGroupController {

    @Autowired
    private ISensitiveWordGroupService sensitiveWordGroupServiceImpl;

    /**
     *
     *
     * @description:敏感词分组下拉列表
     * @author: Huayu
     * @time: 2021/1/28 14:06
     */
    @GetMapping("/select")
    public ApiRespVO sensitiveWordGroupIndex(){
        //判断用户绑定企业信息
        if(AccountService.getCorpId() == null){
            throw new ParamException(100013,"未选择登录企业，不可操作");
        }
        List<SensitiveWordGroupEntity> sensitiveWordGroupEntityList = sensitiveWordGroupServiceImpl.getSensitiveWordGroupsByCorpId(AccountService.getCorpId());
        if(sensitiveWordGroupEntityList == null || sensitiveWordGroupEntityList.size() == 0){
            return ApiRespUtils.getApiRespOfOk("");
        }
        List<RespSensitiveWordGroupIndex> respSensitiveWordGroupIndexList = new ArrayList<RespSensitiveWordGroupIndex>();
        for (SensitiveWordGroupEntity sensitiveWordGroupEntity:
        sensitiveWordGroupEntityList) {
            RespSensitiveWordGroupIndex respSensitiveWordGroupIndex = new RespSensitiveWordGroupIndex();
            respSensitiveWordGroupIndex.setGroupId(sensitiveWordGroupEntity.getId().toString());
            respSensitiveWordGroupIndex.setName(sensitiveWordGroupEntity.getName());
            respSensitiveWordGroupIndexList.add(respSensitiveWordGroupIndex);
        }
        return ApiRespUtils.getApiRespOfOk(respSensitiveWordGroupIndexList);
    }

    /**
     *
     *
     * @description:修改敏感词分组
     * @author: Huayu
     * @time: 2021/1/28 16:31
     */
    @PutMapping("/update")
    public ApiRespVO sensitiveWordGroupUpdate(@RequestBody Map<String,Object> mapData){
        //判断用户绑定企业信息
        if(AccountService.getCorpId() == null){
            throw new ParamException(100013,"未选择登录企业，不可操作");
        }
        if(mapData.get("groupId") == null || mapData.get("groupId").equals("")){
            throw new ParamException(100013,"分组id不能为空");
        }
        if(mapData.get("name") == null || mapData.get("name").equals("")){
            throw new ParamException(100013,"分组名称不能为空");
        }
        //检测分组是否重复
        nameIsUnique(mapData.get("name").toString(),Integer.valueOf(mapData.get("groupId").toString()));
        //数据入表
        Integer i = sensitiveWordGroupServiceImpl.updateSensitiveWordGroupById(Integer.valueOf(mapData.get("groupId").toString()),mapData.get("name").toString());
        if(i < 1){
            throw new ParamException(100013,"分组更新失败");
        }
        return  ApiRespUtils.getApiRespOfOk("");
    }

    /**
     *
     *
     * @description:添加敏感词分组
     * @author: Huayu
     * @time: 2021/1/28 16:38
     */
    @PostMapping("/store")
    public ApiRespVO sensitiveWordGroupStore(@RequestBody Map<String,Object> mapData){
        String name = mapData.get("name").toString();
        if(name == null || name.length() == 0){
            throw new ParamException(100013,"敏感词分组名称 必填");
        }
        //判断用户绑定企业信息
        if(AccountService.getCorpId() == null){
            throw new ParamException(100013,"未选择登录企业，不可操作");
        }
        //创建
        boolean flag = createSensitiveWordGroup(mapData);
        if(!flag){
            throw new CommonException(100014,"敏感词分组创建失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }

    private boolean createSensitiveWordGroup(Map<String, Object> mapData) {
        boolean flag = false;
        if(mapData.get("name").toString().split(",") != null){
            flag = sensitiveWordGroupServiceImpl.createSensitiveWordGroups(mapData);
        }else{
            flag = sensitiveWordGroupServiceImpl.createSensitiveWordGroup(mapData);
        }
        return flag;

    }


    private boolean nameIsUnique(String name, Integer id) {
        SensitiveWordGroupEntity sensitiveWordGroupEntity = sensitiveWordGroupServiceImpl.getSensitiveWordGroupByNameCorpId(name,id,AccountService.getCorpId());
        if(sensitiveWordGroupEntity != null){
            throw new ParamException(100013,"该分组名称已存在");
        }
        return true;
    }


}
