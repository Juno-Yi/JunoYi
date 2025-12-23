package com.junoyi.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Cors 跨域配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.web.cors")
public class CorsProperties {

    /**
     * 是否开启跨域
     */
    private boolean enable = true;

    /**
     * 是否允许携带凭证
     */
    private boolean allowCredentials = true;

    /**
     * 允许跨域访问的源
     */
    private List<String> allowedOrigins;

    /**
     * 允许跨域访问的方法
     */
    private List<String> allowedMethods;

    /**
     * 允许跨域访问的请求头
     */
    private List<String> allowedHeaders;
}