package com.mochat.mochat.controller.contact;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.em.contactfield.AddWayEnum;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.util.wm.ApiRespUtils;
import com.mochat.mochat.model.ApiRespVO;
import com.mochat.mochat.model.workcontact.GetContactPageResponse;
import com.mochat.mochat.model.workcontact.GetContactRequest;
import com.mochat.mochat.model.workcontact.UpdateContactResponse;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.IContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 客户管理
 * @author: zhaojinjian
 * @create: 2020-11-26 14:56
 **/
@RestController
public class ContactController {
    @Autowired
    private IContactService contactService;

    /**
     * @description 客户列表
     * @author zhaojinjian
     * @createTime 2020/12/2 15:43
     */
    @GetMapping("/gateway/mc/workContact/index")
    public ApiRespVO index(GetContactRequest parem, @RequestAttribute ReqPerEnum permission) {
        parem.verifyParam();
        GetContactPageResponse contactPageResponse = new GetContactPageResponse();
        Map<String, Integer> corpIdAndEmpIdMap = AccountService.getCorpIdAndEmpIdMap();
        Integer corpId = corpIdAndEmpIdMap.get("corpId");
        Integer empId = corpIdAndEmpIdMap.get("empId");
        if (corpId != null && empId != null) {
            contactPageResponse = contactService.getContactPage(parem, empId, corpId, permission);
        }
        return ApiRespUtils.getApiRespOfOk(contactPageResponse);
    }

    /**
     * @description 查看客户详情基本信息
     * @author zhaojinjian
     * @createTime 2020/12/2 15:45
     */
    @GetMapping("/gateway/mc/workContact/show")
    public ApiRespVO showContactInfo(
            @NotNull(message = "contactId 不能为空") Integer contactId,
            @NotNull(message = "employeeId 不能为空") Integer employeeId
    ) {
        Integer corpId = AccountService.getCorpId();
        return ApiRespUtils.getApiRespOfOk(contactService.getContactInfo(contactId, employeeId, corpId));
    }

    /**
     * @author: yangpengwei
     * @time: 2021/2/19 4:53 下午
     * @description 客户详情接口
     */
    @GetMapping("/gateway/mc/workContact/detail")
    public ApiRespVO contactDetail(@NotBlank(message = "客户微信 id 不能为空") String wxExternalUserid) {
        return ApiRespUtils.getApiRespOfOk(contactService.getContactDetailByWxExternalUserId(wxExternalUserid));
    }

    /**
     * @author: yangpengwei
     * @time: 2021/2/19 4:53 下午
     * @description 客户互动轨迹
     */
    @GetMapping("/gateway/mc/workContact/track")
    public ApiRespVO contactTrack(@NotNull(message = "客户 id 不能为空") Integer contactId) {
        return ApiRespUtils.getApiRespOfOk(contactService.getContactTrackByContactId(contactId));
    }

    /**
     * @description 修改客户详情基本信息
     * @author zhaojinjian
     * @createTime 2020/12/11 16:18
     */
    @PutMapping("/gateway/mc/workContact/update")
    public ApiRespVO updateContact(@RequestBody UpdateContactResponse parem) {
        parem.verifyParam();
        Map<String, Integer> corpIdAndEmpIdMap = AccountService.getCorpIdAndEmpIdMap();
        Integer corpId = corpIdAndEmpIdMap.get("corpId");
        Integer empId = corpIdAndEmpIdMap.get("empId");
        contactService.updateContact(parem, corpId, empId);
        return ApiRespUtils.getApiRespOfOk(new ArrayList<>());
    }

    /**
     * @description 同步客户
     * @author zhaojinjian
     * @createTime 2020/12/11 16:18
     */
    @PutMapping("/gateway/mc/workContact/synContact")
    public ApiRespVO synContact() {
        Integer corpId = AccountService.getCorpId();
        if (corpId != null) {
            contactService.synContact(corpId);
        }
        return ApiRespUtils.getApiRespOfOk(new ArrayList<>());
    }

    /**
     * @description 客户来源下拉框
     * @author zhaojinjian
     * @createTime 2020/12/11 16:37
     */
    @GetMapping("/gateway/mc/workContact/source")
    public ApiRespVO source() {
        JSONArray jsonArray = new JSONArray();
        for (AddWayEnum addWayEnum : AddWayEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("addWay", addWayEnum.getCode());
            json.put("addWayText", addWayEnum.getMsg());
            jsonArray.add(json);
        }
        return ApiRespUtils.getApiRespOfOk(jsonArray);
    }

    @GetMapping("/gateway/mc/workContact/lossContact")
    public ApiRespVO getLossContact(String employeeId, Integer page, Integer perPage) {
        if (employeeId != null && !employeeId.isEmpty()) {
            Integer corpId = AccountService.getCorpId();
            String[] empIdArray = employeeId.split(",");
            List<Integer> empIds = new ArrayList<>();
            for (int i = 0; i < empIdArray.length; i++) {
                empIds.add(Integer.parseInt(empIdArray[i]));
            }
            return ApiRespUtils.getApiRespOfOk(contactService.getlossContact(corpId, empIds, page, perPage));
        }
        return ApiRespUtils.getApiRespOfOk(new ArrayList<>());
    }
}
