package cn.junoyi.framework.log.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * JunoYi Logger 静态版本
 * 提供与JunoYiLogger相同的静态方法，但功能更强大
 *
 * 使用方式：
 * JunoLogStatic.info("普通日志");
 * JunoLogStatic.business("user", "login", "success");
 * JunoLogStatic.logAsync("INFO", "异步日志");
 *
 * @author Fan
 */
public class JunoYiLogStatic {

    private static final Logger log = LoggerFactory.getLogger("JUNOYI");

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

    // === 增强功能方法 ===

    /**
     * 带分类的日志
     */
    public static void info(String category, String msg) {
        log.info("[{}] {}", category, msg);
    }

    /**
     * 带分类和上下文的日志
     */
    public static void info(String category, String msg, Map<String, String> context) {
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            log.info("[{}] {}", category, msg);
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove);
            }
        }
    }

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
    public static void infoWithMDC(Map<String, String> mdcContext, String msg) {
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

    // === 异步日志方法 ===

    /**
     * 异步记录日志
     */
    @Async
    public static void logAsync(String level, String message, Object... args) {
        switch (level.toUpperCase()) {
            case "INFO":
                info(message, args);
                break;
            case "WARN":
                warn(message, args);
                break;
            case "ERROR":
                error(message, args);
                break;
            case "DEBUG":
                debug(message, args);
                break;
            case "TRACE":
                trace(message, args);
                break;
            default:
                info(message, args);
        }
    }

    /**
     * 异步记录带异常的日志
     */
    @Async
    public static void logAsync(String level, String message, Throwable throwable) {
        switch (level.toUpperCase()) {
            case "INFO":
                info(message);
                break;
            case "WARN":
                warn(message);
                break;
            case "ERROR":
                error(message, throwable);
                break;
            case "DEBUG":
                debug(message);
                break;
            case "TRACE":
                trace(message);
                break;
            default:
                error(message, throwable);
        }
    }

    /**
     * 异步性能日志
     */
    @Async
    public static void logPerformanceAsync(String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        performance(operation, duration);
    }

    /**
     * 异步业务日志
     */
    @Async
    public static void logBusinessAsync(String module, String action, String result) {
        business(module, action, result);
    }

    // === MDC工具方法 ===

    /**
     * 在MDC上下文中执行操作
     */
    public static <T> T executeWithMDC(Map<String, String> context, Supplier<T> supplier) {
        Map<String, String> previousContext = MDC.getCopyOfContextMap();
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            return supplier.get();
        } finally {
            if (previousContext != null) {
                MDC.setContextMap(previousContext);
            } else {
                MDC.clear();
            }
        }
    }

    /**
     * 在MDC上下文中记录日志
     */
    public static void logWithMDC(Map<String, String> context, String level, String message, Object... args) {
        executeWithMDC(context, () -> {
            switch (level.toUpperCase()) {
                case "INFO":
                    info(message, args);
                    break;
                case "WARN":
                    warn(message, args);
                    break;
                case "ERROR":
                    error(message, args);
                    break;
                case "DEBUG":
                    debug(message, args);
                    break;
                case "TRACE":
                    trace(message, args);
                    break;
                default:
                    info(message, args);
            }
            return null;
        });
    }

    /**
     * 生成并设置追踪ID到MDC
     */
    public static void setTraceId() {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put("traceId", traceId);
    }

    /**
     * 设置用户信息到MDC
     */
    public static void setUserInfo(String userId, String username) {
        if (userId != null) {
            MDC.put("userId", userId);
        }
        if (username != null) {
            MDC.put("username", username);
        }
    }

    /**
     * 设置请求信息到MDC
     */
    public static void setRequestInfo(String requestId, String ip, String userAgent) {
        if (requestId != null) {
            MDC.put("requestId", requestId);
        }
        if (ip != null) {
            MDC.put("ip", ip);
        }
        if (userAgent != null) {
            MDC.put("userAgent", userAgent);
        }
    }

    /**
     * 清理MDC上下文
     */
    public static void clearMDC() {
        MDC.clear();
    }

    // === 获取底层Logger ===

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
