package com.mochat.mochat.config.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @description:
 * @author: Huayu
 * @time: 2020/12/7 16:31
 */
@Component
public class MetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("created_at", new Date());
    }


    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updated_at", new Date());
    }

}
