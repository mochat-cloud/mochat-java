package com.mochat.mochat.config.handler;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @description: Entity 属性填充, 触发条件: {@link TableField#fill()}
 * 注: {@link MetaObject#setValue(String, Object)} 方法参数: String 代表 Entity 类的属性, object 代表给属性填充的值
 * @author: Huayu
 * @time: 2020/12/7 16:31
 */
@Component
public class MetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createdAt", new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updatedAt", new Date());
    }

}
