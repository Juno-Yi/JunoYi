package com.junoyi.framework.log.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 框架Logger日志静态类
 * 提供统一的日志记录接口，支持MDC上下文和格式化输出
 *
 * @author Fan
 */
public class JunoYiLogger {

    private static final Logger log = LoggerFactory.getLogger("JUNOYI");

    /**
     * 获取底层Logger实例
     */
    public static Logger getLogger() {
        return log;
    }

    /**
     * 获取指定名称的Logger实例
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    /**
     * 获取指定类的Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    // === 基础日志方法 ===
    
    public static void trace(String msg) {
        log.trace(msg);
    }

    public static void trace(String format, Object... arguments) {
        log.trace(format, arguments);
    }

    public static void debug(String msg) {
        log.debug(msg);
    }

    public static void debug(String format, Object... arguments) {
        log.debug(format, arguments);
    }

    public static void info(String msg) {
        log.info(msg);
    }

    public static void info(String format, Object... arguments) {
        log.info(format, arguments);
    }

    public static void warn(String msg) {
        log.warn(msg);
    }

    public static void warn(String format, Object... arguments) {
        log.warn(format, arguments);
    }

    public static void error(String msg) {
        log.error(msg);
    }

    public static void error(String format, Object... arguments) {
        log.error(format, arguments);
    }

    public static void error(String msg, Throwable t) {
        log.error(msg, t);
    }

    // === 带MDC上下文的日志方法 ===

    /**
     * 在MDC上下文中记录日志
     */
    public static void infoWithMDC(String key, String value, String msg) {
        try {
            MDC.put(key, value);
            info(msg);
        } finally {
            MDC.remove(key);
        }
    }

    /**
     * 在MDC上下文中记录日志（多个键值对）
     */
    public static void infoWithMDC(java.util.Map<String, String> mdcContext, String msg) {
        try {
            if (mdcContext != null) {
                mdcContext.forEach(MDC::put);
            }
            info(msg);
        } finally {
            if (mdcContext != null) {
                mdcContext.keySet().forEach(MDC::remove);
            }
        }
    }

    // === 框架专用日志方法 ===

    /**
     * 框架启动日志
     */
    public static void startup(String component) {
        info("   JunoYi Framework [{}] 启动成功", component);
    }

    /**
     * 框架关闭日志
     */
    public static void shutdown(String component) {
        info("   JunoYi Framework [{}] 已关闭", component);
    }

    /**
     * 性能监控日志
     */
    public static void performance(String operation, long duration) {
        info("   性能监控 - 操作: [{}], 耗时: [{}]ms", operation, duration);
    }

    /**
     * 业务日志
     */
    public static void business(String module, String action, String result) {
        info("   业务日志 - 模块: [{}], 操作: [{}], 结果: [{}]", module, action, result);
    }

    /**
     * 异常日志（带业务上下文）
     */
    public static void businessError(String module, String action, String errorMsg, Throwable t) {
        error("   业务异常 - 模块: [{}], 操作: [{}], 错误: [{}]", module, action, errorMsg, t);
    }

    // === 条件日志方法 ===

    /**
     * 条件日志 - 仅当条件满足时才记录
     */
    public static void infoIf(boolean condition, String msg) {
        if (condition) {
            info(msg);
        }
    }

    /**
     * 条件日志 - 仅当条件满足时才记录格式化日志
     */
    public static void infoIf(boolean condition, String format, Object... arguments) {
        if (condition) {
            info(format, arguments);
        }
    }

    // === 日志级别检查 ===

    public static boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    public static boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public static boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public static boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }
}
