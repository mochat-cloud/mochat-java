package com.mochat.mochat.model.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @description:阿里云配置类
 * @author: Huayu
 * @time: 2020/11/22 15:23
 */
@Data
@Component
@ConfigurationProperties(prefix = "alisun.oss")
public class AliyunOssProperties {
    private String OSS_ACCESS_ID;
    private String OSS_ACCESS_SECRET;
    private String OSS_BUCKET;
    private String OSS_ENDPOINT;
    private String dir;

}
