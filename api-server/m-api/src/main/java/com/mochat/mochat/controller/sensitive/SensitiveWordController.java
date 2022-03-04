package com.mochat.mochat.controller.sensitive;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.sensitive.SensitiveWordEntity;
import com.mochat.mochat.model.sensitiveword.ReqSensitiveWordIndex;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.businesslog.IBusinessLogService;
import com.mochat.mochat.service.sensitiveword.ISensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:敏感词库管理
 * @author: Huayu
 * @time: 2021/1/27 10:29
 */
@RestController
@Validated
@RequestMapping(path = "/sensitiveWord")
public class SensitiveWordController {

    @Autowired
    private ISensitiveWordService sensitiveWordServiceImpl;

    @Autowired
    private IBusinessLogService businessLogServiceImpl;

    /**
     *
     * @description:删除敏感词
     * @author: Huayu
     * @time: 2021/1/27 11:26
     */
    @DeleteMapping("/destroy")
    public ApiRespVO deleteSensitiveWord(@RequestBody Map<String,Object> mapData){
        Integer i = sensitiveWordServiceImpl.deleteSensitiveWord(Integer.valueOf(mapData.get("sensitiveWordId").toString()));
        return ApiRespUtils.ok("");
    }

    /**
     * @description:敏感词列表
     * @author: Huayu
     * @time: 2021/1/28 9:07
     */
    @GetMapping("/index")
    public ApiRespVO sensitiveWordIndex(ReqSensitiveWordIndex sensitiveWordIndex, @RequestAttribute ReqPerEnum permission){
        Page<SensitiveWordEntity> page = sensitiveWordServiceImpl.getSensitiveWordList(sensitiveWordIndex, permission);

        List<SensitiveWordEntity> entityList = page.getRecords();
        List<Map<String,Object>> voList = new ArrayList<>(entityList.size());
        Map<String,Object> vo;
        for (SensitiveWordEntity entity: entityList) {
            vo = new HashMap<>();
            vo.put("sensitiveWordId",entity.getId());
            vo.put("name",entity.getName());
            vo.put("employeeNum",entity.getEmployeeNum());
            vo.put("contactNum",entity.getContactNum());
            vo.put("createdAt", entity.getCreatedAt());
            vo.put("status",entity.getStatus());
            voList.add(vo);
        }

        Page<Map<String, Object>> voPage = ApiRespUtils.transPage(page, voList);
        return ApiRespUtils.okPage(voPage);
    }

    /**
     *
     *
     * @description:添加敏感词
     * @author: Huayu
     * @time: 2021/1/28 18:30
     */
    @PostMapping("/store")
    public ApiRespVO sensitiveWordStore(@RequestBody Map<String,Object> mapData){
        Integer groupId = Integer.valueOf(mapData.get("groupId").toString());
        String name = mapData.get("name").toString();
        //判断用户绑定企业信息
        if(AccountService.getCorpId() == null){
            throw new ParamException(100013,"未选择登录企业，不可操作");
        }
        if(groupId == null || groupId.equals("")){
            throw new ParamException(100013,"非法参数");
        }
        if(name == null || name.equals("")){
            throw new ParamException(100013,"非法参数");
        }
        //创建
        boolean flag = createSensitiveWord(mapData);
        if(!flag){
            throw new CommonException(100014,"敏感词创建失败");
        }
        return ApiRespUtils.ok("");
    }


    /**
     *
     *
     * @description:敏感词移动
     * @author: Huayu
     * @time: 2021/1/28 18:30
     */
    @PutMapping("/move")
    public ApiRespVO sensitiveWordMove(@RequestBody Map<String,Object> mapData){
        Integer sensitiveWordId = Integer.valueOf(mapData.get("sensitiveWordId").toString());
        String groupId = mapData.get("groupId").toString();
        if(groupId == null || groupId.equals("")){
            throw new ParamException(100013,"敏感词ID不能为空");
        }
        if(sensitiveWordId == null || sensitiveWordId.equals("")){
            throw new ParamException(100013,"敏感词分组ID不能为空");
        }
        String clStr = "group_id";
        Integer i = sensitiveWordServiceImpl.updateSensitiveWordById(sensitiveWordId,clStr,groupId);
        if(i < 1){
            throw new CommonException(100014,"敏感词移动失败");
        }
        return ApiRespUtils.ok("");
    }


    @PutMapping("/statusUpdate")
    public ApiRespVO sensitiveWordStatusUpdate(@RequestBody Map<String,Object> mapData){
        Integer sensitiveWordId = Integer.valueOf(mapData.get("sensitiveWordId").toString());
        String status = mapData.get("status").toString();
        if(status == null || status.equals("")){
            throw new ParamException(100013,"敏感词状态不能为空");
        }
        if(sensitiveWordId == null || sensitiveWordId.equals("")){
            throw new ParamException(100013,"敏感词分组ID不能为空");
        }
        //数据入表
        String clStr = "status";
        Integer i = sensitiveWordServiceImpl.updateSensitiveWordById(sensitiveWordId,clStr,status);
        if(i < 1){
            throw new CommonException(100014,"敏感词状态更新失败");
        }
        return ApiRespUtils.ok("");
    }

    private boolean createSensitiveWord(Map<String, Object> mapData) {
        boolean flag = false;
        if(mapData.get("name").toString().split(",") != null){
            flag = sensitiveWordServiceImpl.createSensitiveWords(mapData);
        }else{
            flag = sensitiveWordServiceImpl.createSensitiveWord(mapData);
        }
        return flag;
    }


}
