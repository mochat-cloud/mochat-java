package com.mochat.mochat.model.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespChannelCodeStatisticsItemVO {

    /**
     * 时间坐标
     */
    private String time;

    /**
     * 新增客户数
     */
    private Integer addNumRange;

    /**
     * 被客户删除/拉黑的人数
     */
    private Integer defriendNumRange;

    /**
     * 删除人数
     */
    private Integer deleteNumRange;

    /**
     * 净增人数
     */
    private Integer netNumRange;

    public Integer getAddNumRange() {
        return Objects.isNull(addNumRange) ? 0 : addNumRange;
    }

    public Integer getDefriendNumRange() {
        return Objects.isNull(defriendNumRange) ? 0 : defriendNumRange;
    }

    public Integer getDeleteNumRange() {
        return Objects.isNull(deleteNumRange) ? 0 : deleteNumRange;
    }

    public Integer getNetNumRange() {
        return Objects.isNull(netNumRange) ? 0 : netNumRange;
    }

}
