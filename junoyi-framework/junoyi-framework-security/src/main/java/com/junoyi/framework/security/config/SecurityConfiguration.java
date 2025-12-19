package com.junoyi.framework.security.config;

import com.junoyi.framework.security.filter.JwtAuthenticationTokenFilter;
import com.junoyi.framework.security.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;

/**
 * Security 配置类
 *
 * @author Fan
 */
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfiguration {
    

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 注册 JWT 认证过滤器
     * 设置过滤器的执行顺序和拦截路径
     *
     * @return FilterRegistrationBean 过滤器注册对象
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationTokenFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthenticationTokenFilter);
        // 拦截所有请求
        registrationBean.addUrlPatterns("/*");
        // 设置过滤器执行顺序（数字越小优先级越高）
        registrationBean.setOrder(1);
        registrationBean.setName("jwtAuthenticationTokenFilter");
        return registrationBean;
    }

    /**
     * 注册路径匹配器 Bean
     * 用于白名单路径匹配
     *
     * @return AntPathMatcher 实例
     */
    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }
}
