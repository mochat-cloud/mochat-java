package com.mochat.mochat.service.contact;

import com.mochat.mochat.common.constant.Const;
import com.mochat.mochat.common.util.HttpClientUtil;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.IContactService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.IWorkContactTagService;
import com.mochat.mochat.service.emp.IWorkEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaojinjian
 * @ClassName CorpTag.java
 * @Description TODO
 * @createTime 2020/12/7 16:46
 */
@Service
@EnableAsync
public class CorpTagServiceImpl implements ICorpTagService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private String charset = "utf-8";

    @Autowired
    private IWorkContactTagService workContactTagService;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private IWorkEmployeeService workEmployeeService;
    @Autowired
    @Lazy
    private IContactService workContactService;

    /**
     * @description 编辑客户企业标签
     * @author zhaojinjian
     * @createTime 2020/12/7 16:50
     */
    @Async
    @Override
    public void wxUpdateTag(Integer empId, Integer contactId, List<Integer> tagIds) {
        //region 获取 userid，external_userid，add_tag
        List<String> wx_tagIds = workContactTagService.getWXContactTagId(tagIds);
        String userid = workEmployeeService.getWorkEmployeeInfo(empId).getWxUserId();
        String external_userid = workContactService.getWXExternalUserid(contactId);
        //endregion
        Integer corpId = AccountService.getCorpId();
        CorpEntity corpEntity = corpService.getCorpInfoById(corpId);
        Object mapValue = redisTemplate.opsForHash().get(corpEntity.getWxCorpId(), "acccess_token");
        String requestUrl = Const.URL_REQUEST_ADDRESS + "externalcontact/mark_tag?access_token=" + mapValue.toString();
        Map<String, String> mark_tag_parem = new HashMap<>();
        mark_tag_parem.put("userid", userid);
        mark_tag_parem.put("external_userid", external_userid);
        mark_tag_parem.put("add_tag", wx_tagIds.toString());
        HttpClientUtil.doPost(requestUrl, mark_tag_parem, charset);
    }

 /**
     * @description 编辑客户企业标签
     * @author zhaojinjian
     * @createTime 2020/12/7 16:50
     */
    @Async
    @Override
    public void wxDeleteTag(Integer empId, Integer contactId, List<Integer> tagIds) {
        //region 获取 userid，external_userid，add_tag
        List<String> wx_tagIds = workContactTagService.getWXContactTagId(tagIds);
        String userid = workEmployeeService.getWorkEmployeeInfo(empId).getWxUserId();
        String external_userid = workContactService.getWXExternalUserid(contactId);
        //endregion
        Integer corpId = AccountService.getCorpId();
        CorpEntity corpEntity = corpService.getCorpInfoById(corpId);
        Object mapValue = redisTemplate.opsForHash().get(corpEntity.getWxCorpId(), "acccess_token");
        String requestUrl = Const.URL_REQUEST_ADDRESS + "externalcontact/mark_tag?access_token=" + mapValue.toString();
        Map<String, String> mark_tag_parem = new HashMap<>();
        mark_tag_parem.put("userid", userid);
        mark_tag_parem.put("external_userid", external_userid);
        mark_tag_parem.put("remove_tag", wx_tagIds.toString());
        HttpClientUtil.doPost(requestUrl, mark_tag_parem, charset);
    }

    /**
     * @description 编辑客户企业标签
     * @author zhaojinjian
     * @createTime 2020/12/7 16:50
     */
    @Async
    @Override
    public void wxUpdateTag(String userId, String externalUserID, List<Integer> tagIds) {
        //region 获取 userid，external_userid，add_tag
        List<String> wx_tagIds = workContactTagService.getWXContactTagId(tagIds);

        //endregion
        Integer corpId = AccountService.getCorpId();
        CorpEntity corpEntity = corpService.getCorpInfoById(corpId);
        Object mapValue = redisTemplate.opsForHash().get(corpEntity.getWxCorpId(), "acccess_token");
        String requestUrl = Const.URL_REQUEST_ADDRESS + "externalcontact/mark_tag?access_token=" + mapValue.toString();
        Map<String, String> mark_tag_parem = new HashMap<>();
        mark_tag_parem.put("userid", userId);
        mark_tag_parem.put("external_userid", externalUserID);
        mark_tag_parem.put("add_tag", wx_tagIds.toString());
        HttpClientUtil.doPost(requestUrl, mark_tag_parem, charset);
    }
}
