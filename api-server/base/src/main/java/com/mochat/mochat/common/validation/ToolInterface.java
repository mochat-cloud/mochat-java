package com.mochat.mochat.common.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * @description:验证类接口
 * @author: Huayu
 * @time: 2020/12/1 16:29
 */
public class   ToolInterface  {
    // 新增使用(配合spring的@Validated功能分组使用)
    public interface insert{}

   // 更新使用(配合spring的@Validated功能分组使用)
    public interface update{}
    // 素材库插入使用(配合spring的@Validated功能分组使用)
    public interface mediumStore{}
    // 属性必须有这两个分组的才验证(配合spring的@Validated功能分组使用)
    @GroupSequence({insert.class, update.class})
    public interface all{};

}
