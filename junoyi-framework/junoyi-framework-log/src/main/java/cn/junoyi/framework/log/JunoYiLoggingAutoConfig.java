package cn.junoyi.framework.log;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;


/**
 * 日志自动配置类
 * 优先级最高，最先启动，初始化日志系统
 *
 * @author Fan
 */
@Configuration
@AutoConfigureOrder(Integer.MIN_VALUE)
public class JunoYiLoggingAutoConfig implements ApplicationContextAware {
    private LoggingSystem loggingSystem;

    @PostConstruct
    public void initLoggingLevels() {
        if (loggingSystem == null)
            loggingSystem = LoggingSystem.get(ClassLoader.getSystemClassLoader());

        //隐藏 Spring Boot 所有 INFO，只保留 WARN+
        loggingSystem.setLogLevel("root", LogLevel.WARN);
        //框架日志单独开放 INFO
        loggingSystem.setLogLevel("JUNOYI", LogLevel.INFO);
        loggingSystem.setLogLevel("com.junoyi", LogLevel.INFO);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            this.loggingSystem = applicationContext.getBean(LoggingSystem.class);
        } catch (Exception e) {
            // 如果从ApplicationContext获取失败，则使用默认方式创建
            this.loggingSystem = LoggingSystem.get(ClassLoader.getSystemClassLoader());
        }
    }
}