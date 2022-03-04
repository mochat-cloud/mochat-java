package com.mochat.mochat.model.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespChannelCodeStatisticsVO {

    /**
     * 今日新增客户数
     */
    private Integer addNum;

    /**
     * 今日被客户删除/拉黑的人数
     */
    private Integer defriendNum;

    /**
     * 今日删除人数
     */
    private Integer deleteNum;

    /**
     * 今日净增人数
     */
    private Integer netNum;


    /**
     * 时间段-新增客户数
     */
    private Integer addNumLong;

    /**
     * 时间段-被客户删除/拉黑的人数
     */
    private Integer defriendNumLong;

    /**
     * 时间段-删除人数
     */
    private Integer deleteNumLong;

    /**
     * 时间段-净增人数
     */
    private Integer netNumLong;

    /**
     * list
     */
    private List<RespChannelCodeStatisticsItemVO> list;

    public Integer getAddNum() {
        return getOrDefault(addNum);
    }

    public Integer getDefriendNum() {
        return getOrDefault(defriendNum);
    }

    public Integer getDeleteNum() {
        return getOrDefault(deleteNum);
    }

    public Integer getNetNum() {
        return getOrDefault(netNum);
    }

    public Integer getAddNumLong() {
        return getOrDefault(addNumLong);
    }

    public Integer getDefriendNumLong() {
        return getOrDefault(defriendNumLong);
    }

    public Integer getDeleteNumLong() {
        return getOrDefault(deleteNumLong);
    }

    public Integer getNetNumLong() {
        return getOrDefault(netNumLong);
    }

    private int getOrDefault(Integer i) {
        return i == null ? 0 : i;
    }

}
