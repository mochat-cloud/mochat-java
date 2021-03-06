/**
 * This file is part of MoChat.
 * @link     https://mo.chat
 * @document https://mochat.wiki
 * @contact  group@mo.chat
 * @license  https://github.com/mochat-cloud/mochat-java/blob/master/LICENSE
 */

package com.mochat.mochat.common.em.channel;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.mochat.mochat.common.em.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: yangpengwei
 * @time: 2021/2/23 4:40 下午
 * @description 渠道码 - 统计筛选类型枚举
 */
@Getter
@AllArgsConstructor
public enum ReqStatisticsIndexEnum implements IEnum<Integer> {
    DAY(1),
    WEEK(2),
    MONTH(3);

    @EnumValue
    private Integer value;

    @Override
    public Integer getValue() {
        return value;
    }

}
