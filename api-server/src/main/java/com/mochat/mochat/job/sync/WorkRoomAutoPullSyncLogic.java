package com.mochat.mochat.job.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mochat.mochat.common.util.FileUtils;
import com.mochat.mochat.common.util.WxApiUtils;
import com.mochat.mochat.common.util.emp.DownUploadQueueUtils;
import com.mochat.mochat.config.ex.CommonException;
import com.mochat.mochat.dao.entity.WorkEmployeeEntity;
import com.mochat.mochat.dao.entity.WorkRoomAutoPullEntity;
import com.mochat.mochat.dao.mapper.WorkEmployeeMapper;
import com.mochat.mochat.dao.mapper.WorkRoomAutoPullMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: yangpengwei
 * @time: 2020/12/17 2:34 下午
 * @description 自动拉群管理 - 异步任务
 */
@Slf4j
@Component
@EnableAsync
public class WorkRoomAutoPullSyncLogic {

    @Autowired
    private WorkEmployeeMapper workEmployeeMapper;

    @Autowired
    private WorkRoomAutoPullMapper workRoomAutoPullMapper;

    /**
     * 创建微信 配置客户联系「联系我」方式 二维码
     */
    public void onCreateWxAddContactWayQrcode(WorkRoomAutoPullEntity entity) {
        int corpId = entity.getCorpId();
        boolean skipVerify = entity.getIsVerified() == 2;
        // status 组成规则: workRoomAutoPullId + entity.getId
        String state = "workRoomAutoPullId-" + entity.getId();
        String empJson = entity.getEmployees();
        List<String> empIdList = JSON.parseArray(empJson, String.class);
        List<WorkEmployeeEntity> employeeEntityList = workEmployeeMapper.selectBatchIds(empIdList);
        empIdList.clear();
        for (WorkEmployeeEntity emp : employeeEntityList) {
            empIdList.add(emp.getWxUserId());
        }

        // 微信获取二维码
        String resultJson = WxApiUtils.requestCreateContactWay(corpId, skipVerify, state, empIdList);
        log.debug("onCreateWxAddContactWayQrcode: " + resultJson);
        JSONObject jsonObject = JSON.parseObject(resultJson);
        int errcode = jsonObject.getIntValue("errcode");
        if(errcode != 0) {
            throw new CommonException("自动拉群二维码生成失败");
        }

        String qrcodeUrl = jsonObject.getString("qr_code");
        String wxConfigId = jsonObject.getString("config_id");

        // 上传图片
        String qrcodeFileName = FileUtils.getFileNameOfContactWayQrCode();
        DownUploadQueueUtils.uploadFileByUrl(qrcodeFileName, qrcodeUrl);

        entity.setQrcodeUrl(qrcodeFileName);
        entity.setWxConfigId(wxConfigId);

        workRoomAutoPullMapper.updateById(entity);
    }

}
