package com.mochat.mochat.controller.medium;

import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.medium.MediumGroupEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.medium.IMediumGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description:媒体库分组
 * @author: Huayu
 * @time: 2020/12/4 18:42
 */
@RestController
@RequestMapping("/mediumGroup")
@Validated
public class MediumGroupController {

    @Autowired
    private IMediumGroupService mediumGroupServiceImpl;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private Map<String, Object> mapData;

    /**
     *
     *
     * @description:分组列表
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 15:18
     */
    @GetMapping("index")
    public ApiRespVO mediumGroupIndex(){
        Integer corpId = AccountService.getCorpId();
        List<MediumGroupEntity> mediumGroupEntityList= mediumGroupServiceImpl.getMediumGroupsByCorpId(corpId);
        MediumGroupEntity  mediumGroupEntity = new MediumGroupEntity();
        mediumGroupEntity.setId(0);
        mediumGroupEntity.setName("未分组");
        mediumGroupEntityList.add(mediumGroupEntity);
        return ApiRespUtils.getApiRespOfOk(mediumGroupEntityList);
    }

    /**
     *
     *
     * @description: 分组添加
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 18:05
     */
    @PostMapping("/store")
    public ApiRespVO mediumGroupStore(@RequestBody Map<String,Object> mapData){
        if(mapData.get("name") == null || mapData.get("name").equals("")){
            throw new ParamException(100013,"无效字符");
        }
        List<MediumGroupEntity> mediumGroupEntity = mediumGroupServiceImpl.getMediumGroupByName(mapData.get("name").toString());
        if(mediumGroupEntity.size() > 0){
            throw new ParamException(100013,"分组名称已存在");
        }
        Integer corpId = AccountService.getCorpId();
        MediumGroupEntity mediumGroup= new MediumGroupEntity();
        mediumGroup.setName(mapData.get("name").toString());
        mediumGroup.setCorp_id(corpId);
        Integer i = mediumGroupServiceImpl.createMediumGroup(mediumGroup);
        if(i <= 0){
            throw new CommonException(100014,"添加失败");
        }
        return ApiRespUtils.getApiRespOfOk("");
    }

    /**
     *
     *
     * @description: 分组修改
     * @return: 
     * @author: Huayu
     * @time: 2020/12/7 18:56
     */
    @PutMapping("/update")
    public ApiRespVO mediumGroupUpdate(@RequestBody Map<String,Object> mapData){
        Integer corpId = AccountService.getCorpId();
        if(mapData.get("id") == null || mapData.get("id").equals("")){
            throw new ParamException(100013,"分组id不能为空");
        }
        if(mapData.get("name") == null || mapData.get("name").equals("")){
            throw new ParamException(100013,"分组名称不能为空");
        }
        List<MediumGroupEntity> mediumGroupList = mediumGroupServiceImpl.existMediumGroupByName(Integer.valueOf(mapData.get("id").toString()),mapData.get("name").toString());
        if(mediumGroupList != null && mediumGroupList.size() > 0){
            throw new ParamException(100013,"分组名称已存在");
        }
        Integer i = mediumGroupServiceImpl.updateMediumGroupById(Integer.valueOf(mapData.get("id").toString()),mapData.get("name").toString(),Integer.valueOf(corpId));
        if(i <=0 ){
            throw new ParamException(100013,"修改失败");
        }
        return  ApiRespUtils.getApiRespOfOk("");
    }

    /**
     *
     *
     * @description:删除分组
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 19:02
     */
    @DeleteMapping("/destroy")
    public ApiRespVO mediumGroupDelete(@RequestBody Map<String,Object> mapData){
        Integer i = mediumGroupServiceImpl.deleteMediumGroup(Integer.valueOf(mapData.get("id").toString()));
        MediumGroupEntity mediumGroupEntity = new MediumGroupEntity();
        if(i <= 0){
            throw new ParamException(100013,"删除分组失败");
        }
        return  ApiRespUtils.getApiRespOfOk("");
    }



}
