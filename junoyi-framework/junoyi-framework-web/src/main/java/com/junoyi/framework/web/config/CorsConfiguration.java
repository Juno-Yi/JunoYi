package com.junoyi.framework.web.config;

import com.junoyi.framework.web.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 * @author Fan
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfiguration implements WebMvcConfigurer {

    private final CorsProperties  corsProperties;

    /**
     * 添加跨域配置映射
     * 根据配置属性启用或禁用跨域支持，并设置相应的跨域规则
     *
     * @param registry CorsRegistry 跨域注册器，用于配置跨域映射规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (!corsProperties.isEnable())
            return;

        // 配置跨域映射规则：允许所有路径，设置凭证、请求头、请求方法和源站的允许列表
        registry.addMapping("/**")
                .allowCredentials(corsProperties.isAllowCredentials())
                .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
                .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[0]))
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .maxAge(3600);
    }
}
