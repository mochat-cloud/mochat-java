package com.mochat.mochat.service.wm;

import com.mochat.mochat.common.em.RespChatErrCodeEnum;
import com.mochat.mochat.common.util.RSAUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.CorpEntity;
import com.mochat.mochat.dao.entity.TenantEntity;
import com.mochat.mochat.dao.entity.wm.WorkMsgConfigEntity;
import com.mochat.mochat.model.wm.CorpShowBO;
import com.mochat.mochat.model.wm.ReqCorpStoreDTO;
import com.mochat.mochat.model.wm.ReqStepUpdateDTO;
import com.mochat.mochat.model.wm.StepCreateBO;
import com.mochat.mochat.service.AccountService;
import com.mochat.mochat.service.impl.ICorpService;
import com.mochat.mochat.service.impl.ITenantService;
import com.mochat.mochat.service.impl.IWorkMsgConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2020/12/4 12:09 下午
 * @description 会话内容存档配置 - 微信后台配置
 */
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class ChatConfigServiceImpl implements IChatConfigService {

    @Autowired
    private IWorkMsgConfigService msgConfigService;

    @Autowired
    private ICorpService corpService;

    @Autowired
    private ITenantService tenantService;

    @Value("${mochat.serviceUrl}")
    private String serviceUrl;

    /**
     * 会话内容存档配置 - 企业信息查看
     */
    @Override
    public CorpShowBO getCorpShowInfo(int corpId) {
        CorpShowBO corpShowBO = new CorpShowBO();

        CorpEntity corpEntity = corpService.getById(corpId);
        if (corpEntity == null) {
            throw new CommonException(RespChatErrCodeEnum.CHAT_NO_CORP);
        }

        corpShowBO.setName(corpEntity.getCorpName());
        corpShowBO.setCorpId(corpEntity.getCorpId());
        corpShowBO.setWxCorpid(corpEntity.getWxCorpId());
        corpShowBO.setSocialCode(corpEntity.getSocialCode());

        WorkMsgConfigEntity workMsgConfigEntity = msgConfigService.getByCorpId(corpId);

        corpShowBO.setId(workMsgConfigEntity.getId());
        corpShowBO.setChatAdmin(workMsgConfigEntity.getChatAdmin());
        corpShowBO.setChatAdminPhone(workMsgConfigEntity.getChatAdminPhone());
        corpShowBO.setChatAdminIdcard(workMsgConfigEntity.getChatAdminIdcard());
        corpShowBO.setChatApplyStatus(workMsgConfigEntity.getChatApplyStatus());

        return corpShowBO;
    }

    /**
     * 会话内容存档配置 - 企业信息添加
     */
    @Override
    public int setCorpStore(ReqCorpStoreDTO req) {
        Integer corpId = req.getCorpId();
        if (corpId == null) {
            corpId = AccountService.getCorpId();
        }

        CorpEntity corpEntity = corpService.getById(corpId);
        if (corpEntity == null) {
            throw new CommonException(RespChatErrCodeEnum.CHAT_NO_CORP);
        }
        corpEntity.setSocialCode(req.getSocialCode());
        corpService.updateById(corpEntity);

        WorkMsgConfigEntity workMsgConfigEntity = msgConfigService.getByCorpId(corpId);
        workMsgConfigEntity.setChatAdmin(req.getChatAdmin());
        workMsgConfigEntity.setChatAdminPhone(req.getChatAdminPhone());
        workMsgConfigEntity.setChatAdminIdcard(req.getChatAdminIdcard());
        workMsgConfigEntity.setChatApplyStatus(req.getChatApplyStatus());
        msgConfigService.updateById(workMsgConfigEntity);

        return workMsgConfigEntity.getId();
    }

    /**
     * 会话内容存档配置 - 微信后台配置-步骤页面
     */
    @Override
    public StepCreateBO getStepCreateInfo() {
        int corpId = AccountService.getCorpId();

        // 读取会话内容存档配置
        WorkMsgConfigEntity entity = msgConfigService.getByCorpId(corpId);
        StepCreateBO stepCreateBO = new StepCreateBO();
        stepCreateBO.setId(entity.getId());
        stepCreateBO.setChatApplyStatus(entity.getChatApplyStatus());

        int chatApplyStatus = entity.getChatApplyStatus();
        if (chatApplyStatus == 1) {
            // 未申请 返回企业名, 企业微信 id
            CorpEntity corpEntity = corpService.getById(corpId);
            if (corpEntity == null) {
                throw new CommonException(RespChatErrCodeEnum.CHAT_NO_CORP);
            }

            stepCreateBO.setCorpName(corpEntity.getCorpName());
            stepCreateBO.setWxCorpId(corpEntity.getWxCorpId());
        }
        if (chatApplyStatus == 2) {
            // 填写企业信息 返回客服联系方式
            // 通过配置文件 yml 读取客服联系方式二维码
            stepCreateBO.setServiceContactUrl(serviceUrl);
        }
        if (chatApplyStatus == 3) {
            // 添加客户提交资料 客服后台修改
            // 读取租户信息, 暂时默认读取第一条租户信息
            TenantEntity tenantEntity = tenantService.getById(1);
            if (tenantEntity == null) {
                throw new CommonException(RespChatErrCodeEnum.NO_FIND_TENANT);
            }

            stepCreateBO.setChatWhitelistIpJson(tenantEntity.getServerIps());

            // 公私钥
            Map<String, String> keys = RSAUtils.getRsaKeys();
            stepCreateBO.setRsaPrivateKey(keys.get(RSAUtils.KEY_PRIVATE));
            stepCreateBO.setRsaPublicKey(keys.get(RSAUtils.KEY_PUBLIC));
        }

        return stepCreateBO;
    }

    /**
     * 会话内容存档配置 - 微信后台配置-步骤动作
     */
    @Override
    public boolean putStepUpdate(ReqStepUpdateDTO req) {
        int corpId = AccountService.getCorpId();

        // 会话内容存档配置存储
        WorkMsgConfigEntity entity = msgConfigService.getByCorpId(corpId);
        entity.setChatApplyStatus(4);
        entity.setChatRsaKey(req.getChatRsaKey());
        entity.setChatSecret(req.getChatSecret());
        entity.setChatStatus(req.getChatStatus());
        return entity.updateById();
    }

}
