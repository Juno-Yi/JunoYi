package com.junoyi.framework.log.config;

/**
 * Logback配置类
 * 配置控制台和文件输出的Appender
 * 注意：已禁用，使用XML配置替代
 *
 * @author Fan
 */
//@Component  // 禁用此组件，使用XML配置
public class JunoYiLogbackConfig {

    // 保留构造函数以防万一，但不再使用
    public JunoYiLogbackConfig() {
        // 空构造函数
    }

    /**
     * 初始化方法 - 已禁用，使用XML配置替代
     */
    // @PostConstruct
    // public void init() {
    //     // 已禁用，使用XML配置替代
    //     // 所有Logback配置现在由 logback-spring.xml 控制
    // }
    
    /**
     * 配置控制台输出 - 已禁用，使用XML配置替代
     */
    // private void setupConsoleAppender(LoggerContext context) {
    //     // 已禁用，使用XML配置替代
    // }

    /**
     * 配置文件输出 - 已禁用，使用XML配置替代
     */
    // private void setupFileAppender(LoggerContext context) {
    //     // 已禁用，使用XML配置替代
    // }
    
    /**
     * 确保日志目录存在 - 已禁用，使用XML配置替代
     */
    // private void ensureLogDirectoryExists(String directoryPath) {
    //     // 已禁用，使用XML配置替代
    // }
}