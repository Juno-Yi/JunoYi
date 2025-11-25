package cn.junoyi.framework.log;

import jakarta.annotation.PostConstruct;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Logback配置类
 * 配置控制台和文件输出的Appender
 *
 * @author Fan
 */
@Configuration
@ConditionalOnProperty(prefix = "junoyi.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JunoYiLogbackConfig {

    private final JunoYiLogProperties logProperties;

    public JunoYiLogbackConfig(JunoYiLogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 配置控制台输出
        if (logProperties.getConsole().isEnabled()) {
            setupConsoleAppender(context);
        }

        // 配置文件输出
        if (logProperties.getFile().isEnabled()) {
            setupFileAppender(context);
        }
    }

    /**
     * 配置控制台输出
     */
    private void setupConsoleAppender(LoggerContext context) {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setContext(context);
        appender.setName("JUNO_CONSOLE");

        JunoYiLogbackEncoder encoder = new JunoYiLogbackEncoder();
        encoder.setContext(context);
        
        // 应用配置属性
        encoder.setShowThreadName(logProperties.getConsole().isShowThreadName());
        encoder.setShowMDC(logProperties.getConsole().isShowMDC());
        encoder.setShowClassName(logProperties.getConsole().isShowClassName());
        encoder.setMaxClassNameLength(logProperties.getConsole().getMaxClassNameLength());
        encoder.setColorEnabled(logProperties.getConsole().isColorEnabled());
        
        encoder.start();

        appender.setEncoder(encoder);
        appender.start();

        // 移除所有现有的控制台appender，只保留我们的
        ch.qos.logback.classic.Logger rootLogger = context.getLogger("ROOT");
        rootLogger.detachAppender("CONSOLE");
        rootLogger.detachAppender("JUNO_CONSOLE");
        rootLogger.addAppender(appender);
    }

    /**
     * 配置文件输出
     */
    private void setupFileAppender(LoggerContext context) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);
        appender.setName("JUNO_FILE");

        // 设置文件路径
        String filePath = logProperties.getFile().getPath();
        try {
            filePath = OptionHelper.substVars(filePath, context);
        } catch (Exception e) {
            // 如果变量替换失败，使用原始路径
            JunoYiLogger.warn("日志文件路径变量替换失败，使用原始路径: {}", filePath);
        }
        appender.setFile(filePath);

        // 配置滚动策略
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(appender);
        
        // 设置文件名模式
        String fileNamePattern = filePath + ".%d{yyyy-MM-dd}.%i";
        if (logProperties.getFile().isCompress()) {
            fileNamePattern += ".gz";
        }
        rollingPolicy.setFileNamePattern(fileNamePattern);
        
        // 设置最大文件大小
        try {
            rollingPolicy.setMaxFileSize(FileSize.valueOf(logProperties.getFile().getMaxSize()));
        } catch (Exception e) {
            rollingPolicy.setMaxFileSize(FileSize.valueOf("100MB"));
        }
        
        // 设置最大历史文件数
        rollingPolicy.setMaxHistory(logProperties.getFile().getMaxHistory());
        
        // 设置总大小限制
        try {
            rollingPolicy.setTotalSizeCap(FileSize.valueOf(logProperties.getFile().getTotalSizeCap()));
        } catch (Exception e) {
            rollingPolicy.setTotalSizeCap(FileSize.valueOf("1GB"));
        }
        
        rollingPolicy.start();

        // 配置文件编码器（无颜色版本）
        JunoYiLogbackEncoder encoder = new JunoYiLogbackEncoder();
        encoder.setContext(context);
        encoder.setShowThreadName(logProperties.getConsole().isShowThreadName());
        encoder.setShowMDC(logProperties.getConsole().isShowMDC());
        encoder.setShowClassName(logProperties.getConsole().isShowClassName());
        encoder.setMaxClassNameLength(logProperties.getConsole().getMaxClassNameLength());
        encoder.start();

        appender.setEncoder(encoder);
        appender.setRollingPolicy(rollingPolicy);
        appender.start();

        // 添加到root logger
        context.getLogger("ROOT").addAppender(appender);
    }
}