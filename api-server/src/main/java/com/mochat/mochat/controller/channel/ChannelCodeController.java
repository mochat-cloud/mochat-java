package com.mochat.mochat.controller.channel;

import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.api.ReqPageDto;
import com.mochat.mochat.common.api.ApiRespUtils;
import com.mochat.mochat.common.api.ApiRespVO;
import com.mochat.mochat.model.channel.ReqChannelCodeDTO;
import com.mochat.mochat.model.channel.ReqChannelCodeListDTO;
import com.mochat.mochat.model.channel.ReqChannelCodeStatisticsDTO;
import com.mochat.mochat.model.channel.ReqChannelCodeStatisticsIndexDTO;
import com.mochat.mochat.service.channel.IChannelCodeService;
import com.mochat.mochat.service.impl.IWorkContactEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/2/22 2:47 下午
 * @description 渠道活码
 */
@RestController
@RequestMapping("/channelCode")
public class ChannelCodeController {

    @Autowired
    private IChannelCodeService channelCodeService;

    @Autowired
    private IWorkContactEmployeeService contactEmployeeService;

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 3:53 下午
     * @description 新建渠道码
     */
    @PostMapping("/store")
    public ApiRespVO storeCode(@RequestBody ReqChannelCodeDTO req) {
        channelCodeService.storeOrUpdateCode(req);
        return ApiRespUtils.ok();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 3:53 下午
     * @description 编辑渠道码
     */
    @PutMapping("/update")
    public ApiRespVO updateCode(@RequestBody ReqChannelCodeDTO req) {
        channelCodeService.storeOrUpdateCode(req);
        return ApiRespUtils.ok();
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 3:53 下午
     * @description 编辑渠道码
     */
    @GetMapping("/show")
    public ApiRespVO showCode(@NotNull(message = "渠道码 id 不能为空") Integer channelCodeId) {
        return ApiRespUtils.ok(channelCodeService.getChannelCodeDetail(channelCodeId));
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 3:53 下午
     * @description 编辑渠道码
     */
    @GetMapping("/index")
    public ApiRespVO codeList(ReqChannelCodeListDTO req, ReqPageDto page, @RequestAttribute ReqPerEnum permission) {
        return ApiRespUtils.okPage(channelCodeService.getChannelCodePageByReq(req, page, permission));
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 3:53 下午
     * @description 渠道码客户
     */
    @GetMapping("/contact")
    public ApiRespVO codeContactList(@NotNull(message = "渠道码 id 不能为空") Integer channelCodeId, ReqPageDto page) {
        return ApiRespUtils.okPage(channelCodeService.getChannelCodeContactByReq(channelCodeId, page));
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 3:53 下午
     * @description 统计分页数据
     */
    @GetMapping("/statisticsIndex")
    public ApiRespVO getStatisticsOfPage(ReqChannelCodeStatisticsIndexDTO req) {
        req.checkParam();
        return ApiRespUtils.okPage(contactEmployeeService.getStatisticsOfPage(req));
    }

    /**
     * @author: Ypw / ypwcode@163.com
     * @time: 2021/2/22 3:53 下午
     * @description 统计折线图
     */
    @GetMapping("/statistics")
    public ApiRespVO getStatistics(ReqChannelCodeStatisticsDTO req) {
        return ApiRespUtils.ok(contactEmployeeService.getStatistics(req));
    }


}
