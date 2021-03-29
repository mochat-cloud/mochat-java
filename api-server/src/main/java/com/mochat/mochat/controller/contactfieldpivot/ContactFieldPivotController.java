package com.mochat.mochat.controller.contactfieldpivot;

import com.mochat.mochat.common.em.RespErrCodeEnum;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.config.ex.ParamException;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.contactfieldpivot.UpdateContactFieldPivotModel;
import com.mochat.mochat.service.impl.IWorkContactFieldPivotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaojinjian
 * @ClassName ContactFieldPivotController.java
 * @Description TODO
 * @createTime 2020/12/24 16:41
 */
@RestController
@RequestMapping(path = "/gateway/mc/contactFieldPivot")
public class ContactFieldPivotController {
    @Autowired
    private IWorkContactFieldPivotService contactFieldPivotService;

    /**
     * @description 客户 - 客户详情 - 用户画像
     * @author zhaojinjian
     * @createTime 2020/12/24 18:05
     */
    @GetMapping("/index")
    public ApiRespVO getContactFieldPivotList(Integer contactId) {
        if (contactId == null) {
            throw new ParamException(RespErrCodeEnum.INVALID_PARAMS.getCode(), RespErrCodeEnum.INVALID_PARAMS.getMsg());
        }
        return ApiRespUtils.getApiRespOfOk(contactFieldPivotService.getContactFieldPivotList(contactId));
    }

    /**
     * @description 客户 - 客户详情 - 编辑用户画像
     * @author zhaojinjian
     * @createTime 2020/12/24 18:15
     */
    @PutMapping("/update")
    public ApiRespVO updateContactFieldPivot(@RequestBody UpdateContactFieldPivotModel param) {
        contactFieldPivotService.updateContactFieldPivot(param);
        return ApiRespUtils.getApiRespOfOk("");
    }
}
