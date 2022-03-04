package com.mochat.mochat.model.wm;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author: Ypw / ypwcode@163.com
 * @time: 2020/12/1 10:50 上午
 * @description corpStore 接口请求参数
 */
@Data
public class ReqCorpStoreDTO {

    /**
     * 企业ID
     */
    private Integer corpId;

    /**
     * 企业代码
     */
    @NotNull(message = "企业统一社会信用代码不能为 null")
    @Length(min = 18, max = 18, message = "统一社会信用代码为 18 位")
    private String socialCode;

    /**
     * 企业负责人
     */
    @NotNull(message = "企业负责人姓名不能为 null")
    private String chatAdmin;

    /**
     * 企业负责人电话
     */
    @NotNull(message = "企业负责人电话不能为 null")
    private String chatAdminPhone;

    /**
     * 企业负责人身份证
     */
    @NotNull(message = "企业负责人身份证号码不能为 null")
    private String chatAdminIdcard;

    /**
     * 当前申请进度 1填写企业信息 2添加客服提交资料(已经开通会话内容存档功能) 3更新微信后台设置
     * 允许值: 1, 2
     */
    @Range(min = 1, max = 2, message = "进度值无效")
    private Integer chatApplyStatus;

}
