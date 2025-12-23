package com.junoyi.framework.web.config;

import com.junoyi.framework.web.filter.SqlInjectionFilter;
import com.junoyi.framework.web.filter.XssFilter;
import com.junoyi.framework.web.properties.CorsProperties;
import com.junoyi.framework.web.properties.SQLInjectionProperties;
import com.junoyi.framework.web.properties.XssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web 模块配置
 *
 * @author Fan
 */
@Configuration
@EnableConfigurationProperties({XssProperties.class, SQLInjectionProperties.class, CorsProperties.class})
public class WebConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    /**
     * XSS 过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "junoyi.web.xss", name = "enable", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(XssProperties xssProperties) {
        log.info("[XSS Protection] XSS filter enabled, mode: {}", xssProperties.getMode());
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter(xssProperties));
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        // 在 SQL 注入过滤器之后执行
        registration.setOrder(2);
        return registration;
    }

    /**
     * SQL 注入防护过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "junoyi.web.sql-injection", name = "enable", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<SqlInjectionFilter> sqlInjectionFilterRegistration(SQLInjectionProperties properties) {
        log.info("[SQL Injection Protection] SQL injection protection filter is enabled, mode:{}", properties.getMode());
        FilterRegistrationBean<SqlInjectionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SqlInjectionFilter(properties));
        registration.addUrlPatterns("/*");
        registration.setName("sqlInjectionFilter");
        // 先执行 SQL 注入检测
        registration.setOrder(1);
        return registration;
    }
}
