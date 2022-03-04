package com.mochat.mochat.model.channel;

import com.mochat.mochat.common.em.channel.ReqStatisticsIndexEnum;
import com.mochat.mochat.config.ex.ParamException;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/2/23 11:10 上午
 * @description 渠道码 - 统计分页数据参数
 */
@Data
public class ReqChannelCodeStatisticsDTO {

    /**
     * 渠道码 ID
     */
    @NotNull(message = "渠道码 ID 不能为空")
    private Integer channelCodeId;

    /**
     * 统计类型(1-日期2-周3-月)
     */
    @Range(min = 1, max = 3, message = "非法统计类型")
    private ReqStatisticsIndexEnum type;

    /**
     * 开始时间[非必填,type=1必填]
     */
    private String startTime;

    /**
     * 结束时间[非必填,type=1必填]
     */
    private String endTime;

    public void checkParam() {
        if (type.getValue() != 1) {
            return;
        }
        if (Objects.isNull(startTime) || Objects.isNull(endTime) || startTime.isEmpty() || endTime.isEmpty()) {
            throw new ParamException("非法开始时间或结束时间");
        }
    }

}
