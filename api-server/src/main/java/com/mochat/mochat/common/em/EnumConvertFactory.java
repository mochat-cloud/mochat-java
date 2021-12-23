package com.mochat.mochat.common.em;

import com.baomidou.mybatisplus.core.enums.IEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 参数转换枚举
 *
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/2/23 5:20 下午
 *
 * 枚举类 需要实现接口 {@link IEnum}
 */
@Component
@SuppressWarnings("all")
public final class EnumConvertFactory implements ConverterFactory<String, IEnum> {

    @Override
    public <T extends IEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToIEum<>(targetType);
    }

    private static class StringToIEum<T extends IEnum> implements Converter<String, T> {

        private Class<T> targerType;

        public StringToIEum(Class<T> targerType) {
            this.targerType = targerType;
        }

        @Override
        public T convert(String source) {
            if (!StringUtils.hasLength(source)) {
                return null;
            }
            return (T) EnumConvertFactory.getIEnum(this.targerType, source);
        }
    }

    public static <T extends IEnum> Object getIEnum(Class<T> targerType, String source) {
        for (T enumObj : targerType.getEnumConstants()) {
            if (source.equals(String.valueOf(enumObj.getValue()))) {
                return enumObj;
            }
        }
        return null;
    }
}
