package com.mochat.mochat.controller.contactfield;

import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.model.contactfield.AddContactFieldModel;
import com.mochat.mochat.model.contactfield.BatchUpdateContactFieldModel;
import com.mochat.mochat.model.contactfield.UpdateContactFieldModel;
import com.mochat.mochat.service.impl.IContactFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zhaojinjian
 * @ClassName ContactFieldController.java
 * @Description 客户高级属性控制器, 编辑所有客户共用的属性
 * @see com.mochat.mochat.controller.contactfieldpivot.ContactFieldPivotController 设置单个客户高级属性的属性值
 *
 * @createTime 2020/12/16 11:58
 */
@RestController
@RequestMapping("/contactField")
public class ContactFieldController {
    @Autowired
    private IContactFieldService contactFieldService;

    @GetMapping("/portrait")
    public ApiRespVO getPortrait(Integer fieldId, String name) {
        return ApiRespUtils.ok(contactFieldService.getPortrait(fieldId, name));
    }

    /**
     * @description 查看高级属性
     * @author zhaojinjian
     * @createTime 2020/12/16 15:00
     */
    @GetMapping("/show")
    public ApiRespVO showContactField(Integer id) {
        return ApiRespUtils.ok(contactFieldService.getContactFieldInfo(id));
    }

    /**
     * @description 获取高级属性的集合列表
     * @author zhaojinjian
     * @createTime 2020/12/16 15:03
     */
    @GetMapping("/index")
    public ApiRespVO getContactFieldList(Integer status, Integer page, Integer perPage) {
        if (status != null && status > 2) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
        return ApiRespUtils.ok(contactFieldService.getContactFieldList(status, page, perPage));
    }

    /**
     * @description 新增高级属性
     * @author zhaojinjian
     * @createTime 2020/12/16 15:05
     */
    @PostMapping("/store")
    public ApiRespVO insertContactField(@RequestBody AddContactFieldModel parem) {
        parem.verifyParam();
        return ApiRespUtils.ok(contactFieldService.insertContactField(parem));
    }

    /**
     * @description 修改高级属性
     * @author zhaojinjian
     * @createTime 2020/12/16 15:06
     */
    @PutMapping("/update")
    public ApiRespVO updateContactField(@RequestBody UpdateContactFieldModel parem) {
        parem.verifyParam();
        return ApiRespUtils.ok(contactFieldService.updateContactField(parem));
    }

    /**
     * @description 删除高级属性
     * @author zhaojinjian
     * @createTime 2020/12/16 15:09
     */
    @DeleteMapping("/destroy")
    public ApiRespVO deleteContactField(@RequestBody Map<String, Integer> map) {
        if (map.get("id") == null) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
        return ApiRespUtils.ok(contactFieldService.deleteContactField(map.get("id")));
    }

    /**
     * @description 批量删除高级属性
     * @author zhaojinjian
     * @createTime 2020/12/16 15:11
     */
    @PutMapping("/batchUpdate")
    public ApiRespVO batchUpdate(@RequestBody BatchUpdateContactFieldModel parem) {
        if (parem.getUpdate() == null) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
        return ApiRespUtils.ok(contactFieldService.BatchUpdateContactField(parem));
    }

    /**
     * @description 修改高级属性的状态
     * @author zhaojinjian
     * @createTime 2020/12/16 15:14
     */
    @PutMapping("/statusUpdate")
    public ApiRespVO statusUpdate(@RequestBody JSONObject req) {
        int id = req.getIntValue("id");
        if (id <= 0) {
            throw new ParamException("id 不能为空");
        }

        int status = (int) req.getOrDefault("status", -1);
        if (status < 0 || status > 1) {
            throw new ParamException("status 超出范围");
        }

        return ApiRespUtils.ok(contactFieldService.updateStatus(id, status));
    }
}
