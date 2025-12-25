package com.junoyi.framework.security.config;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.security.context.SecurityContext;
import com.junoyi.framework.security.crypto.RsaCryptoHelper;
import com.junoyi.framework.security.filter.ApiEncryptFilter;
import com.junoyi.framework.security.filter.TokenAuthenticationTokenFilter;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.properties.SecurityProperties;
import com.junoyi.framework.security.helper.SessionHelper;
import com.junoyi.framework.security.helper.JwtTokenHelper;
import jakarta.annotation.PostConstruct;
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

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SecurityConfiguration.class);
    
    private final JwtTokenHelper tokenService;
    private final SessionHelper sessionHelper;
    private final SecurityProperties securityProperties;
    private final RsaCryptoHelper rsaCryptoHelper;

    /**
     * 初始化 PermissionHelper，注入从 SecurityContext 获取权限信息的方法
     */
    @PostConstruct
    public void initPermissionHelper() {
        PermissionHelper.init(
                () -> {
                    LoginUser user = SecurityContext.get();
                    return user != null ? user.getPermissions() : null;
                },
                () -> {
                    LoginUser user = SecurityContext.get();
                    return user != null ? user.getGroups() : null;
                },
                () -> {
                    LoginUser user = SecurityContext.get();
                    return user != null ? user.getUserId() : null;
                },
                () -> {
                    LoginUser user = SecurityContext.get();
                    return user != null ? user.getDeptId() : null;
                },
                () -> {
                    LoginUser user = SecurityContext.get();
                    return user != null && user.isSuperAdmin();
                }
        );
        log.info("PermissionHelperInit", "PermissionHelper initialized with SecurityContext.");
    }

    /**
     * 注册 API 加密过滤器
     * 在 JWT 认证之前执行，用于解密请求和加密响应
     *
     * @return FilterRegistrationBean 过滤器注册对象
     */
    @Bean
    public FilterRegistrationBean<ApiEncryptFilter> apiEncryptFilter() {
        ApiEncryptFilter filter = new ApiEncryptFilter(securityProperties, rsaCryptoHelper);
        
        FilterRegistrationBean<ApiEncryptFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(0);
        registrationBean.setName("apiEncryptFilter");

        log.info("FilterRegistered", "API encryption filter registered.");
        
        return registrationBean;
    }

    /**
     * 注册 Token 认证过滤器
     * 验证 Token 并从 Redis 获取会话信息
     *
     * @return FilterRegistrationBean 过滤器注册对象
     */
    @Bean
    public FilterRegistrationBean<TokenAuthenticationTokenFilter> tokenAuthenticationFilter() {
        TokenAuthenticationTokenFilter filter = new TokenAuthenticationTokenFilter(
                tokenService, sessionHelper, securityProperties);
        
        FilterRegistrationBean<TokenAuthenticationTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setName("tokenAuthenticationFilter");
        
        log.info("FilterRegistered", "Token authentication filter registered.");
        
        return registrationBean;
    }

    /**
     * 注册路径匹配器 Bean
     */
    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }
}
