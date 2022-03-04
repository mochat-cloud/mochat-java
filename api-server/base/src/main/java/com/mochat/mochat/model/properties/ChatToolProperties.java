package com.mochat.mochat.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:阿里云配置类
 * @author: Huayu
 * @time: 2020/11/22 15:23
 */
@Data
@Component
@ConfigurationProperties(prefix = "chat-tools")
public class ChatToolProperties {
    private String apiUrl;
    private String webUrl;
}
