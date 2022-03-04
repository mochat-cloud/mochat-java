package com.mochat.mochat.model.wm;

import lombok.Data;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/11/25 2:19 下午
 * @description 员工信息
 *
 * 简略的员工信息 bean, 用于 运营-聊天记录-员工下拉
 */
@Data
public class ToUserInfoBO {
    private Integer workEmployeeId;
    private Integer toUsertype;
    private Integer toUserId;
    private String name = "";
    private String alias = "";
    private String avatar = "";
    private String content = "";
    private String msgDataTime = "";
}
