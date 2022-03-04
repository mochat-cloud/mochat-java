package com.mochat.mochat.service.contact;

import java.util.Map;

public interface ISendWelcomeMsgService {

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/12 4:49 下午
     * @description 发送新客户欢迎语, 仅文字
     */
    void sendMsg(int corpId, String welcomeCode, String leadingWords);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/12 4:49 下午
     * @description 发送新客户欢迎语, 自动拉群
     */
    void sendMsgOfRoomAutoPull(int corpId, String welcomeCode, String leadingWords, String imageUrl);

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/5/12 4:50 下午
     * @description 发送新客户欢迎语, 渠道码
     */
    void sendMsgOfChannelCode(int corpId, String welcomeCode, Map<String, String> map);

}
