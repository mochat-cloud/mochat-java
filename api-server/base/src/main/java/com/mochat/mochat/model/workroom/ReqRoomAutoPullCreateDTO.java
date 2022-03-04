package com.mochat.mochat.model.workroom;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/25 4:43 下午
 * @description 自动拉群管理 - 创建提交 DTO
 */
@NoArgsConstructor
@Data
public class ReqRoomAutoPullCreateDTO {

    @NotNull(message = "企业 id 不能为 null")
    private Integer corpId;
    @NotBlank(message = "扫码名称不能为 null")
    private String qrcodeName;
    @Range(min = 1, max = 2, message = "非法添加验证数值")
    private Integer isVerified;
    @NotBlank(message = "成员 id 不能为空")
    private String employees;
    @NotBlank(message = "客户标签不能为空")
    private String tags;
    @NotBlank(message = "入群引导语不能为空")
    private String leadingWords;
    @NotBlank(message = "群聊 id 不能为空")
    private String rooms;

}
