package com.mochat.mochat.config.interceptor;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.mochat.mochat.common.em.IEnum;
import com.mochat.mochat.common.em.workupdatetime.EnumDeserializer;
import com.mochat.mochat.common.em.workupdatetime.EnumSerializer;

import javax.annotation.PostConstruct;

//@Component
public class FastjsonConfig{
    @PostConstruct
    public void init(){
        SerializeConfig.getGlobalInstance().put(IEnum.class, new EnumSerializer());

        ParserConfig.getGlobalInstance().putDeserializer(IEnum.class, new EnumDeserializer());
    }
}
