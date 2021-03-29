package com.mochat.mochat.service.wxback;

/**
 * @author: yangpengwei
 * @time: 2020/12/28 11:25 上午
 * @description 企业微信客户标签回调服务
 */
public interface IWxCallbackTagService {

    String CHANGE_TYPE_CONTACT_TAG_CREATE = "create";
    String CHANGE_TYPE_CONTACT_TAG_UPDATE = "update";
    String CHANGE_TYPE_CONTACT_TAG_DELETE = "delete";

    String dispatchEvent(String dataJson);
}
