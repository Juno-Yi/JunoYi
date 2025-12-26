package com.junoyi.framework.permission.config;

import com.junoyi.framework.permission.aspect.PermissionAspect;
import com.junoyi.framework.permission.field.FieldPermissionModule;
import com.junoyi.framework.permission.properties.PermissionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 权限配置类
 * 用于定义和配置应用程序的权限相关设置，包括权限验证规则、访问控制策略等
 * 该类作为Spring配置类，提供权限管理相关的Bean定义和配置
 *
 * @author Fan
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(PermissionProperties.class)
public class PermissionConfiguration {

    /**
     * 权限校验切面
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "junoyi.permission", name = "enable", havingValue = "true", matchIfMissing = true)
    public PermissionAspect permissionAspect(PermissionProperties properties) {
        return new PermissionAspect(properties);
    }

    /**
     * 字段权限 Jackson 模块
     * <p>
     * 通过 Jackson2ObjectMapperBuilderCustomizer 注册，确保与其他模块兼容
     */
    @Bean
    @ConditionalOnProperty(prefix = "junoyi.permission", name = "field-permission-enable", havingValue = "true", matchIfMissing = true)
    public Jackson2ObjectMapperBuilderCustomizer fieldPermissionCustomizer() {
        log.info("Register FieldPermissionModule with Jackson.");
        return builder -> {
            builder.modulesToInstall(new FieldPermissionModule());
        };
    }
}
