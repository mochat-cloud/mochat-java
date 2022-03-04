package com.mochat.mochat.common.em.workupdatetime;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.mochat.mochat.common.em.IEnum;

import java.lang.reflect.Type;

public class EnumSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int i) {
        SerializeWriter out = serializer.getWriter();
        if (object == null) {
            serializer.getWriter().writeNull();
            return;
        }
        Object value = ((IEnum<Object>)object).getValue();
        out.write(String.valueOf(value));
    }
}
