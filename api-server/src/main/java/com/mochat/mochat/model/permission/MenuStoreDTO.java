package com.mochat.mochat.model.permission;

import com.mochat.mochat.config.ex.ParamException;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author: yangpengwei
 * @time: 2021/3/11 10:13 上午
 * @description 菜单管理 - 修改菜单接口参数
 */
@Data
public class MenuStoreDTO {

    /**
     * 所填菜单级别 1-一级 2-二级 3-三级 4-四级 5-四级的操作
     */
    @NotNull(message = "菜单级别不能为空")
    private Integer level;

    /**
     * 一级菜单 id
     */
    private Integer firstMenuId;

    /**
     * 二级菜单 id
     */
    private Integer secondMenuId;

    /**
     * 三级菜单 id
     */
    private Integer thirdMenuId;

    /**
     * 四级菜单 id
     */
    private Integer fourthMenuId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名不能为空")
    private String name;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 是否为页面菜单 1-是 2-否
     */
    @Range(min = 1, max = 2, message = "是否为页面菜单参数无效")
    private Integer isPageMenu;
    
    /**
     * 地址
     */
    @NotBlank(message = "菜单链接不能为空")
    private String linkUrl;
    
    /**
     * 地址 链接类型【1-内部链接(默认)2-外部链接】
     */
    @Range(min = 1, max = 2, message = "菜单链接类型参数无效")
    private Integer linkType;
    
    /**
     * 数据权限 1-启用, 2-不启用（查看企业下数据）
     */
    @Range(min = 1, max = 2, message = "菜单数据权限参数无效")
    private Integer dataPermission;

    private Integer parentMenuId = 0;

    public void checkParam() {
        if (level >= MenuConst.LEVEL_SECOND) {
            if (Objects.isNull(firstMenuId)) {
                throw new ParamException("一级菜单 id 不能为空");
            }
            parentMenuId = firstMenuId;
        }
        if (level >= MenuConst.LEVEL_THIRD) {
            if (Objects.isNull(secondMenuId)) {
                throw new ParamException("二级菜单 id 不能为空");
            }
            parentMenuId = secondMenuId;
        }
        if (level >= MenuConst.LEVEL_FOURTH) {
            if (Objects.isNull(thirdMenuId)) {
                throw new ParamException("三级菜单 id 不能为空");
            }
            parentMenuId = thirdMenuId;
        }
        if (level >= MenuConst.LEVEL_FIFTH) {
            if (Objects.isNull(fourthMenuId)) {
                throw new ParamException("四级菜单 id 不能为空");
            }
            parentMenuId = fourthMenuId;
        }
    }
}
