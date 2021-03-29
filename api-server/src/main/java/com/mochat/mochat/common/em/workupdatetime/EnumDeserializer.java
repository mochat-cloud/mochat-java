package com.mochat.mochat.common.em.workupdatetime;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.mochat.mochat.common.em.EnumConvertFactory;

import java.lang.reflect.Type;

public class EnumDeserializer implements ObjectDeserializer {

    public static EnumDeserializer instance = new EnumDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object o) {
        String value = parser.lexer.stringVal();
        return (T) EnumConvertFactory.getIEnum((Class) type, value);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
