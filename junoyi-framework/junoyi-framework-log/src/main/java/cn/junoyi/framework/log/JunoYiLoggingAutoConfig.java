package cn.junoyi.framework.log;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.lang.NonNull;

/**
 * 日志自动配置类
 * 优先级最高，最先启动，初始化日志系统
 * 支持外部配置文件配置日志级别和格式
 *
 * @author Fan
 */
@Configuration
@AutoConfigureOrder(Integer.MIN_VALUE)
@EnableConfigurationProperties(JunoYiLogProperties.class)
@ConditionalOnProperty(prefix = "junoyi.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JunoYiLoggingAutoConfig implements ApplicationContextAware, InitializingBean {

    private LoggingSystem loggingSystem;
    private final JunoYiLogProperties logProperties;

    public JunoYiLoggingAutoConfig(JunoYiLogProperties logProperties) {
        this.logProperties = logProperties;
        // 在构造函数中立即初始化日志系统
        initializeLoggingSystem();
    }

    private void initializeLoggingSystem() {
        // 初始化日志级别
        initLoggingLevels();
    }

    /**
     * 初始化日志级别
     */
    private void initLoggingLevels() {
        if (loggingSystem == null) {
            loggingSystem = LoggingSystem.get(ClassLoader.getSystemClassLoader());
        }

        // 设置根日志级别
        loggingSystem.setLogLevel("root", LogLevel.valueOf(logProperties.getLevel().getRoot()));
        
        // 设置JunoYi框架日志级别
        loggingSystem.setLogLevel("JUNOYI", LogLevel.valueOf(logProperties.getLevel().getJunoyi()));
        loggingSystem.setLogLevel("cn.junoyi", LogLevel.valueOf(logProperties.getLevel().getJunoyi()));
        
        // 设置Spring框架日志级别
        loggingSystem.setLogLevel("org.springframework", LogLevel.valueOf(logProperties.getLevel().getSpring()));
        
        // 设置MyBatis日志级别
        loggingSystem.setLogLevel("com.baomidou", LogLevel.valueOf(logProperties.getLevel().getMybatis()));
        
        // 设置SQL日志级别
        loggingSystem.setLogLevel("cn.junoyi.**.mapper", LogLevel.valueOf(logProperties.getLevel().getSql()));
        loggingSystem.setLogLevel("cn.junoyi.**.dao", LogLevel.valueOf(logProperties.getLevel().getSql()));
        
        // 处理自定义包日志级别配置
        String customLevels = logProperties.getLevel().getCustom();
        if (customLevels != null && !customLevels.trim().isEmpty()) {
            String[] levelConfigs = customLevels.split(",");
            for (String config : levelConfigs) {
                String[] parts = config.split(":");
                if (parts.length == 2) {
                    String packageName = parts[0].trim();
                    String level = parts[1].trim().toUpperCase();
                    try {
                        loggingSystem.setLogLevel(packageName, LogLevel.valueOf(level));
                    } catch (IllegalArgumentException e) {
                        System.err.println("无效的日志级别配置: " + config + ", 级别: " + level);
                    }
                }
            }
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        try {
            this.loggingSystem = applicationContext.getBean(LoggingSystem.class);
        } catch (Exception e) {
            // 如果从ApplicationContext获取失败，则使用默认方式创建
            this.loggingSystem = LoggingSystem.get(ClassLoader.getSystemClassLoader());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 确保在所有属性设置完成后再次初始化日志级别
        initLoggingLevels();
    }
}