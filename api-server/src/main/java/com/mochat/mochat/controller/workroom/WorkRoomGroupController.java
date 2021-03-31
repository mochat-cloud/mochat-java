package com.mochat.mochat.controller.workroom;

import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.workroom.WorkRoomGroupEntity;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.workroom.IWorkRoomGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @description:客户群分组
 * @author: Huayu
 * @time: 2020/12/8 14:28
 */
@RestController
@RequestMapping("/workRoomGroup")
@Validated
public class WorkRoomGroupController {

    @Autowired
    private IWorkRoomGroupService workRoomGroupServiceImpl;

    /**
     *
     * @description: 客户群分组列表
     * @return: 
     * @author: Huayu
     * @time: 2020/12/8 17:07
     */
    @GetMapping("index")
    public ApiRespVO WorkRoomGroupIndex(@RequestParam(value = "page")Integer page, @RequestParam(value = "perPage")Integer perPage){
        Integer pageNo = (page.equals(""))?0:page-1;
        Integer pageCount = (perPage.equals(""))?10:perPage;
        Integer corpIds = AccountService.getCorpId();        //分页数据
        return ApiRespUtils.getApiRespOfOk(workRoomGroupServiceImpl.getWorkRoomGroupList(String.valueOf(corpIds),pageNo,pageCount));
    }

    /**
     *
     *
     * @description:删除
     * @return:
     * @author: Huayu
     * @time: 2020/12/9 8:48
     */
    @DeleteMapping("/destroy")
    public ApiRespVO WorkRoomGroupDestroy(@RequestBody Map<String,Object> mapData){
        WorkRoomGroupEntity workRoomGroupEntity = workRoomGroupServiceImpl.getWorkRoomGroupById(Integer.valueOf(mapData.get("workRoomGroupId").toString()));
        if(workRoomGroupEntity == null){
            throw  new ParamException(100013,"该客户群分组不存在，不可操作");
        }
        //客户群分组表
        Integer i = workRoomGroupServiceImpl.deleteWorkRoomGroup(Integer.valueOf(mapData.get("workRoomGroupId").toString()));
        return ApiRespUtils.getApiRespOfOk(null);
    }

    /**
     * @description: 客户群分组管理-新建客户群分组提交.
     * @return: 
     * @author: Huayu
     * @time: 2020/12/9 8:57
     */
    @PostMapping("/store")
    public ApiRespVO WorkRoomGroupStore(@RequestBody Map<String,Object> mapData){
        //验证客户群分组名称是否已经存在
        WorkRoomGroupEntity workRoomGroupEntity = workRoomGroupServiceImpl.getWorkRoomGroupsByCorpId(Integer.valueOf(mapData.get("corpId").toString()),mapData.get("workRoomGroupName").toString());
        if(workRoomGroupEntity != null){
            throw new ParamException(100013, "该客户群分组名称已存在，不可重复添加");
        }
        //数据入表
        WorkRoomGroupEntity workRoomGroupEntity1 = new WorkRoomGroupEntity();
        workRoomGroupEntity1.setCorpId(Integer.valueOf((mapData.get("corpId").toString())));
        workRoomGroupEntity1.setName(mapData.get("workRoomGroupName").toString());
        workRoomGroupServiceImpl.createWorkRoomGroup(workRoomGroupEntity1);
        return ApiRespUtils.getApiRespOfOk(null);
    }

    /**
     *
     *
     * @description: 客户群分组管理- 更新提交.
     * @return: 
     * @author: Huayu
     * @time: 2020/12/9 9:37
     */
    @PutMapping("/update")
    public ApiRespVO WorkRoomGroupUpdate(@RequestBody Map<String,Object> mapData){
        Integer id = Integer.valueOf(mapData.get("workRoomGroupId").toString());
        String workRoomGroupName = mapData.get("workRoomGroupName").toString();
        WorkRoomGroupEntity workRoomGroupEntity = workRoomGroupServiceImpl.getWorkRoomGroupById(id);
        if(workRoomGroupEntity == null){
            throw new ParamException(100013,"该客户群分组不存在，不可操作");
        }
        //验证当前企业下客户群分组名称是否重复
        WorkRoomGroupEntity workRoomGroupEntity1 = workRoomGroupServiceImpl.getWorkRoomGroupsByCorpId(workRoomGroupEntity.getCorpId(),workRoomGroupName);
        if(workRoomGroupEntity1 != null){
            throw new ParamException(100013, "该客户群分组名称已存在，不可更新");
        }
        //数据入表
        workRoomGroupServiceImpl.updateWorkRoomGroupById(id,workRoomGroupName);
        return ApiRespUtils.getApiRespOfOk(null);
    }


}
