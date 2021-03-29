package com.mochat.mochat.service.channel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mochat.mochat.common.em.permission.ReqPerEnum;
import com.mochat.mochat.common.model.RequestPage;
import com.mochat.mochat.dao.entity.channel.ChannelCodeEntity;
import com.mochat.mochat.model.channel.*;

import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2021/2/22 4:59 下午
 * @description 渠道码服务
 */
public interface IChannelCodeService extends IService<ChannelCodeEntity> {

    /**
     * @author: yangpengwei
     * @time: 2021/3/1 3:02 下午
     * @description 创建渠道码
     */
    void storeOrUpdateCode(ReqChannelCodeDTO req);

    /**
     * @author: yangpengwei
     * @time: 2021/3/2 2:39 下午
     * @description 获取渠道码详情
     */
    RespChannelCodeVO getChannelCodeDetail(Integer codeId);

    /**
     * @author: yangpengwei
     * @time: 2021/3/1 3:02 下午
     * @description 编辑渠道码所在分组
     */
    void updateGroupId(Integer codeId, Integer codeGroupId);

    /**
     * @author: yangpengwei
     * @time: 2021/3/1 3:00 下午
     * @description 获取渠道码 - 客户列表
     */
    Page<RespChannelCodeContactVO> getChannelCodeContactByReq(Integer channelCodeId, RequestPage page);

    /**
     * @author: yangpengwei
     * @time: 2021/3/1 3:00 下午
     * @description 获取渠道码列表
     */
    Page<RespChannelCodeListVO> getChannelCodePageByReq(ReqChannelCodeListDTO req, RequestPage page, ReqPerEnum permission);

    /**
     * @author: yangpengwei
     * @time: 2021/3/3 2:26 下午
     * @description 获取渠道码中的欢迎语 map
     * map["welcomeContent"] = 文本内容
     * map["content"] = json 内容: appid小程序, imageLink 图文, 没有前两者的是图片
     */
    Map<String, String> getWelcomeMsgMap(Integer channelCodeId);

    /**
     * @author: yangpengwei
     * @time: 2021/3/3 3:17 下午
     * @description 更新渠道码二维码所属成员信息
     */
    void updateChannelCodeQr(Integer channelCodeId);

}
