package com.mochat.mochat.config.interceptor;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.mochat.mochat.common.em.EnumConvertFactory;
import com.mochat.mochat.common.em.workupdatetime.EnumDeserializer;
import com.mochat.mochat.common.em.workupdatetime.EnumSerializer;
import com.mochat.mochat.interceptor.AuthenticationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:配置拦截器(注册到WebMvcConfigurer)
 * @author: Huayu
 * @time: 2020/11/20 10:27
 */
@Slf4j
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private EnumConvertFactory enumConvertFactory;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求，通过判断是否有LoginToken注解是否需要登录
        registry.addInterceptor(authenticationInterceptor());
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

//    @Bean
//    public RestTemplate reRestTemplate(){
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(20000);
//        requestFactory.setReadTimeout(20000);
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
//
//        List<HttpMessageConverter<?>> converterList=restTemplate.getMessageConverters();
//        HttpMessageConverter<?> converterTarget = null;
//        for (HttpMessageConverter<?> item : converterList) {
//            if (item.getClass() == StringHttpMessageConverter.class) {
//                converterTarget = item;
//                break;
//            }
//        }
//
//        if (converterTarget != null) {
//            converterList.remove(converterTarget);
//        }
//        HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
//        converterList.add(converter);
//
//        return restTemplate;
//    }

    /**
     * FastJson 序列化工具
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConvert = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.DisableCircularReferenceDetect);
        // fastJsonConfig.setDateFormat("yyyy-MM-dd hh:mm:ss");

        SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
        serializeConfig.put(IEnum.class, new EnumSerializer());
        fastJsonConfig.setSerializeConfig(serializeConfig);

        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        parserConfig.putDeserializer(IEnum.class, EnumDeserializer.instance);
        fastJsonConfig.setParserConfig(parserConfig);

        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastConvert.setSupportedMediaTypes(fastMediaTypes);
        fastConvert.setFastJsonConfig(fastJsonConfig);
        converters.add(0, fastConvert);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(enumConvertFactory);
    }
}
