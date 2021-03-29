package com.mochat.mochat.service.wxback;

import java.text.ParseException;

/**
 * @description:客户群变更回调
 * @author: Huayyu
 * @time: 2020/12/21 9:37
 */
public interface IWxCallbackChatService {
    String updateChatCallBack(String dataJson) throws ParseException;
}
