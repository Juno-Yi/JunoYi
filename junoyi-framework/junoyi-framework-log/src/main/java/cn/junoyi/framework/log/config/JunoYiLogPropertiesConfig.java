package cn.junoyi.framework.log.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JunoYi日志属性配置类
 * 负责创建和管理JunoYiLogProperties Bean
 *
 * @author Fan
 */
@Configuration
@EnableConfigurationProperties(JunoYiLogProperties.class)
public class JunoYiLogPropertiesConfig {
}
