package com.mochat.mochat.model.wm;

import lombok.Data;

/**
 * @author: yangpengwei
 * @time: 2020/11/25 2:19 下午
 * @description 员工信息
 *
 * 简略的员工信息 bean, 用于 运营-聊天记录-员工下拉
 */
@Data
public class FromUserInfoBO {
    private Integer id;
    private String name;
    private String avatar;
}
