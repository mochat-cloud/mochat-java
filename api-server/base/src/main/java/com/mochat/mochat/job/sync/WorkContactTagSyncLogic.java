package com.mochat.mochat.job.sync;

import com.alibaba.fastjson.JSON;
import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.common.util.HttpClientUtil;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import com.mochat.mochat.service.impl.IContactService;
import com.mochat.mochat.service.impl.IWorkContactTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/5/8 9:49 上午
 * @description 客户标签异步服务
 */
@Component
@EnableAsync
public class WorkContactTagSyncLogic {

    private static final String ACTION_ADD_TAG = "add_tag";
    private static final String ACTION_DEL_TAG = "remove_tag";

    @Autowired
    private IWorkContactTagService contactTagService;

    @Autowired
    private IWorkEmployeeService employeeService;

    @Autowired
    private IContactService contactService;

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/10 10:54 上午
     * @description 同步客户标签
     */
    @Async
    public void onSync(int corpId) {
        contactTagService.synContactTag(corpId);
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/12 5:33 下午
     * @description 给客户的企业微信添加标签
     */
    @Async
    public void contactAddWxTag(Integer empId, Integer contactId, List<Integer> tagIds) {
        contactUpdateWxTag(empId,contactId,tagIds,ACTION_ADD_TAG);
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/12 5:33 下午
     * @description 删除添加给客户企业微信的标签
     */
    @Async
    public void contactDeleteWxTag(Integer empId, Integer contactId, List<Integer> tagIds) {
        contactUpdateWxTag(empId,contactId,tagIds,ACTION_DEL_TAG);
    }

    private void contactUpdateWxTag(Integer empId, Integer contactId, List<Integer> tagIds, String action) {
        List<String> wxTagIds = contactTagService.getWxContactTagId(tagIds);
        String wxEmpId = employeeService.getById(empId).getWxUserId();
        String wxContactId = contactService.getWxExternalUserId(contactId);
        int corpId = AccountService.getCorpIdByEmpId(empId);
        String accessToken = WxApiUtils.getAccessTokenContact(corpId);
        String requestUrl = Const.URL_REQUEST_ADDRESS + "/externalcontact/mark_tag?access_token=" + accessToken;
        Map<String, Object> param = new HashMap<>();
        param.put("userid", wxEmpId);
        param.put("external_userid", wxContactId);
        param.put(action, wxTagIds);
        HttpClientUtil.doPost(requestUrl, JSON.toJSONString(param));
    }

}
