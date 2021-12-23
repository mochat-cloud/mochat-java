package com.mochat.mochat.service.wm;

import com.mochat.mochat.model.wm.CorpShowBO;
import com.mochat.mochat.model.wm.ReqCorpStoreDTO;
import com.mochat.mochat.model.wm.ReqStepUpdateDTO;
import com.mochat.mochat.model.wm.StepCreateBO;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/4 12:09 下午
 * @description 会话内容存档配置 - 微信后台配置
 */
public interface IChatConfigService {

    /**
     * 会话内容存档配置 - 企业信息查看
     */
    CorpShowBO getCorpShowInfo(int corpId);

    /**
     * 会话内容存档配置 - 企业信息添加
     */
    int setCorpStore(ReqCorpStoreDTO req);

    /**
     * 会话内容存档配置 - 微信后台配置-步骤页面
     */
    StepCreateBO getStepCreateInfo();

    /**
     * 会话内容存档配置 - 微信后台配置-步骤动作
     */
    boolean putStepUpdate(ReqStepUpdateDTO req);

}
