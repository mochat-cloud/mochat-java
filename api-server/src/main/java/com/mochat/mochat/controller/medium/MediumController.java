package com.mochat.mochat.controller.medium;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.common.em.medium.TypeEnum;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.dao.entity.medium.MediumEntity;
import com.mochat.mochat.dao.entity.medium.MediumGroupEntity;
import com.mochat.mochat.model.medium.MediumIndexDto;
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
@RequestMapping("/medium")
@Validated
public class MediumController {

    @Autowired
    private IMediumService mediumServiceImpl;

    @Autowired
    private IMediumGroupService mediumGroupServiceImpl;

    @Autowired
    private ISubSystemService subsystemServiceImpl;

    /**
     * @description: 查看
     * @return:
     * @author: Huayu
     * @time: 2020/12/6 10:59
     */
    @GetMapping(value = "/show")
    public ApiRespVO mediumShow(@Validated MediumEntity mediumEnyity) {
        MediumEntity medium = mediumServiceImpl.getMediumById(mediumEnyity.getId());
        Map<String, Object> objectMap = new HashMap<String, Object>();
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        objectMap.put("id", medium.getId());
        objectMap.put("corpId", medium.getCorpId());
        objectMap.put("type", medium.getType());
        JSONObject jsonObject = JSON.parseObject(medium.getContent());
        objectMap.put("content", jsonObject);
        objectMap.put("mediumGroupId", medium.getMediumGroupId());
        listMap.add(objectMap);
        return ApiRespUtils.ok(objectMap);
    }

    /**
     * @description: 列表
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 13:21
     */
    @GetMapping(value = "/index")
    public ApiRespVO mediumIndex(MediumIndexDto dto) {
        Page<MediumEntity> page = mediumServiceImpl.getMediumList(dto);
        List<MediumEntity> mediumList = page.getRecords();


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> vo;
        List<Map<String, Object>> voList = new ArrayList<>(mediumList.size());
        for (MediumEntity entity : mediumList) {
            vo = new HashMap<>();

            String fullUrl = mediumServiceImpl.addFullPath(entity.getContent(), entity.getType());
            JSONObject jsonObject = JSON.parseObject(fullUrl);

            vo.put("id", entity.getId());
            vo.put("type", TypeEnum.getTypeByCode(entity.getType()));
            vo.put("content", jsonObject);
            vo.put("mediumGroupId", entity.getMediumGroupId());
            vo.put("corpId", entity.getCorpId());

            List<MediumGroupEntity> mediumGroupList = mediumGroupServiceImpl.getMediumGroupsById(entity.getMediumGroupId().toString());
            vo.put("userId", entity.getUserId());
            vo.put("userName", entity.getUserName());
            vo.put("createdAt", format.format(entity.getCreatedAt()));
            if (mediumGroupList.size() > 0) {
                vo.put("mediumGroupName", mediumGroupList.get(0).getName());
            } else {
                vo.put("mediumGroupName", "");
            }
            voList.add(vo);
        }

        Page<Map<String, Object>> voPage = ApiRespUtils.transPage(page, voList);
        return ApiRespUtils.okPage(voPage);
    }

    /**
     * @description: 添加素材库
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 14:23
     */
    @PostMapping(value = "/store")
    public ApiRespVO mediumStore(@RequestBody MediumEntity mediumEnyity) {
        mediumEnyity.setCorpId(AccountService.getCorpId());
        mediumEnyity.setUserId(AccountService.getUserId());
        GetSubSystemInfoResponse getSubSystemInfoResponse = subsystemServiceImpl.getSubSystemInfo(AccountService.getUserId());
        mediumEnyity.setUserName(getSubSystemInfoResponse.getUserName());
        mediumEnyity.setContent(JSON.parseObject(mediumEnyity.getContent()).toJSONString());
        Integer i = mediumServiceImpl.createMedium(mediumEnyity);
        if (i <= 0) {
            throw new CommonException(100014, "添加失败");
        }
        return ApiRespUtils.ok("");
    }

    /**
     * @description:删除
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 14:46
     */
    @DeleteMapping("/destroy")
    public ApiRespVO mediumDestroy(@RequestBody Map<String, Object> mapData) {
        Integer i = mediumServiceImpl.deleteMedium(Integer.valueOf(mapData.get("id").toString()));
        if (i <= 0) {
            throw new CommonException(100014, "添加失败");
        }
        return ApiRespUtils.ok("");
    }


    /**
     * @description: 移动分组
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 15:00
     */
    @PutMapping("/groupUpdate")
    public ApiRespVO mediumGroupUpdate(@RequestBody Map<String, Object> mapData) {
        if (mapData.get("id") == null || mapData.get("id").equals("")) {
            throw new ParamException(100013, "素材ID不能为空");
        }
        if (mapData.get("mediumGroupId") == null || mapData.get("mediumGroupId").equals("")) {
            throw new ParamException(100013, "素材分组ID不能为空");
        }
        MediumEntity mediumEnyity = new MediumEntity();
        mediumEnyity.setId(Integer.valueOf(mapData.get("id").toString()));
        mediumEnyity.setMediumGroupId(Integer.valueOf(mapData.get("mediumGroupId").toString()));
        boolean b = mediumServiceImpl.updateMediumById(mediumEnyity);
        if (!b) {
            throw new CommonException(100014, "移动失败");
        }
        return ApiRespUtils.ok("");
    }

    /**
     * @description: 修改
     * @return:
     * @author: Huayu
     * @time: 2020/12/7 15:12
     */
    @PutMapping("/update")
    public ApiRespVO mediumUpdate(@RequestBody MediumEntity mediumEnyity, HttpServletRequest request) {
        Integer userId = AccountService.getUserId();
        mediumEnyity.setUserId(userId);
        GetSubSystemInfoResponse subSystemInfoResponse = subsystemServiceImpl.getSubSystemInfo(userId);
        mediumEnyity.setUserName(subSystemInfoResponse.getUserName());
        mediumEnyity.setContent(JSON.parseObject(mediumEnyity.getContent()).toJSONString());
        boolean b = mediumServiceImpl.updateMediumById(mediumEnyity);
        if (!b) {
            throw new CommonException(100014, "修改失败");
        }
        return ApiRespUtils.ok("");
    }
}
