package com.junoyi.framework.log.util;

import com.junoyi.framework.log.core.JunoYiLogger;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * JunoYi日志工具类
 * 提供便捷的日志记录方法和异步日志支持
 *
 * @author Fan
 */
@Component
public class JunoYiLogUtils {

    /**
     * 异步记录日志
     */
    @Async
    public void logAsync(String level, String message, Object... args) {
        switch (level.toUpperCase()) {
            case "INFO":
                JunoYiLogger.info(message, args);
                break;
            case "WARN":
                JunoYiLogger.warn(message, args);
                break;
            case "ERROR":
                JunoYiLogger.error(message, args);
                break;
            case "DEBUG":
                JunoYiLogger.debug(message, args);
                break;
            case "TRACE":
                JunoYiLogger.trace(message, args);
                break;
            default:
                JunoYiLogger.info(message, args);
        }
    }

    /**
     * 异步记录带异常的日志
     */
    @Async
    public void logAsync(String level, String message, Throwable throwable) {
        switch (level.toUpperCase()) {
            case "INFO":
                JunoYiLogger.info(message);
                break;
            case "WARN":
                JunoYiLogger.warn(message);
                break;
            case "ERROR":
                JunoYiLogger.error(message, throwable);
                break;
            case "DEBUG":
                JunoYiLogger.debug(message);
                break;
            case "TRACE":
                JunoYiLogger.trace(message);
                break;
            default:
                JunoYiLogger.error(message, throwable);
        }
    }

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
                    JunoYiLogger.info(message, args);
                    break;
                case "WARN":
                    JunoYiLogger.warn(message, args);
                    break;
                case "ERROR":
                    JunoYiLogger.error(message, args);
                    break;
                case "DEBUG":
                    JunoYiLogger.debug(message, args);
                    break;
                case "TRACE":
                    JunoYiLogger.trace(message, args);
                    break;
                default:
                    JunoYiLogger.info(message, args);
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
     * 清除MDC上下文
     */
    public static void clearMDC() {
        MDC.clear();
    }

    /**
     * 清除指定的MDC键
     */
    public static void removeMDC(String... keys) {
        for (String key : keys) {
            MDC.remove(key);
        }
    }

    /**
     * 性能监控日志
     */
    public static void logPerformance(String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        JunoYiLogger.performance(operation, duration);
    }

    /**
     * 性能监控日志（异步）
     */
    @Async
    public CompletableFuture<Void> logPerformanceAsync(String operation, long startTime) {
        logPerformance(operation, startTime);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 业务日志
     */
    public static void logBusiness(String module, String action, String result) {
        JunoYiLogger.business(module, action, result);
    }

    /**
     * 业务异常日志
     */
    public static void logBusinessError(String module, String action, String errorMsg, Throwable t) {
        JunoYiLogger.businessError(module, action, errorMsg, t);
    }

    /**
     * 记录方法执行时间
     */
    public static <T> T logMethodExecution(String methodName, Supplier<T> supplier) {
        long startTime = System.currentTimeMillis();
        try {
            T result = supplier.get();
            logPerformance(methodName, startTime);
            return result;
        } catch (Exception e) {
            JunoYiLogger.error("方法执行异常: {}", methodName, e);
            throw e;
        }
    }

    /**
     * 记录方法执行时间（带返回值日志）
     */
    public static <T> T logMethodExecutionWithResult(String methodName, Supplier<T> supplier) {
        long startTime = System.currentTimeMillis();
        try {
            T result = supplier.get();
            long duration = System.currentTimeMillis() - startTime;
            JunoYiLogger.info("方法执行完成: [{}], 耗时: [{}]ms, 返回值: [{}]", 
                methodName, duration, result);
            return result;
        } catch (Exception e) {
            JunoYiLogger.error("方法执行异常: {}", methodName, e);
            throw e;
        }
    }

    /**
     * 条件日志记录
     */
    public static void logIf(boolean condition, String level, String message, Object... args) {
        if (condition) {
            switch (level.toUpperCase()) {
                case "INFO":
                    JunoYiLogger.info(message, args);
                    break;
                case "WARN":
                    JunoYiLogger.warn(message, args);
                    break;
                case "ERROR":
                    JunoYiLogger.error(message, args);
                    break;
                case "DEBUG":
                    JunoYiLogger.debug(message, args);
                    break;
                case "TRACE":
                    JunoYiLogger.trace(message, args);
                    break;
                default:
                    JunoYiLogger.info(message, args);
            }
        }
    }

    /**
     * 记录系统信息
     */
    public static void logSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        
        JunoYiLogger.info("系统信息 - 最大内存: [{}]MB, 已用内存: [{}]MB, 可用内存: [{}]MB", 
            maxMemory, usedMemory, freeMemory);
    }

    /**
     * 记录配置信息
     */
    public static void logConfiguration(String configName, Object configValue) {
        JunoYiLogger.info("配置信息 - [{}]: [{}]", configName, configValue);
    }
}
