package com.junoyi.framework.web.interceptor;

import com.junoyi.framework.core.utils.IPUtils;
import com.junoyi.framework.web.properties.AccessLogProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 访问日志拦截器
 * <p>
 * DEBUG 级别：输出请求详细信息（URL、方法、参数、请求头等）
 * INFO 级别：输出请求耗时
 * WARN 级别：输出慢请求警告
 *
 * @author Fan
 */
public class AccessLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AccessLogInterceptor.class);

    private static final String START_TIME_ATTR = "junoyi.request.startTime";

    private final AccessLogProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AccessLogInterceptor(AccessLogProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        // 检查是否排除
        String uri = request.getRequestURI();
        if (isExcluded(uri)) {
            return true;
        }

        // 记录开始时间
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        // DEBUG 级别输出请求详情
        if (log.isDebugEnabled()) {
            logRequestDetails(request);
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        // 检查是否排除
        String uri = request.getRequestURI();
        if (isExcluded(uri)) {
            return;
        }

        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime == null) {
            return;
        }

        long duration = System.currentTimeMillis() - startTime;
        String method = request.getMethod();
        int status = response.getStatus();

        // 慢请求警告
        if (duration >= properties.getSlowRequestThreshold()) {
            log.warn("[Slow Request] {} {} - {}ms (status: {})", method, uri, duration, status);
        } else if (log.isInfoEnabled()) {
            // INFO 级别输出耗时
            log.info("[Request] {} {} - {}ms (status: {})", method, uri, duration, status);
        }

        // DEBUG 级别输出响应详情
        if (log.isDebugEnabled()) {
            log.debug("[Response] {} {} completed, status: {}, duration: {}ms",
                    method, uri, status, duration);
        }
    }

    /**
     * 记录请求详情
     */
    private void logRequestDetails(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = IPUtils.getIpAddr(request);

        StringBuilder sb = new StringBuilder();
        sb.append("[Request Start] ").append(method).append(" ").append(uri);
        if (queryString != null) {
            sb.append("?").append(queryString);
        }
        sb.append(" from ").append(clientIp);
        log.debug(sb.toString());

        // 记录请求参数
        if (properties.isLogRequestParams()) {
            logRequestParams(request);
        }

        // 记录请求头
        if (properties.isLogRequestHeaders()) {
            logRequestHeaders(request);
        }
    }

    /**
     * 记录请求参数
     */
    private void logRequestParams(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        if (paramMap.isEmpty()) {
            return;
        }

        StringJoiner joiner = new StringJoiner(", ", "[Params] {", "}");
        paramMap.forEach((key, values) -> {
            String value = values.length == 1 ? values[0] : String.join(",", values);
            // 敏感字段脱敏
            if (isSensitiveParam(key)) {
                value = "******";
            }
            joiner.add(key + "=" + value);
        });
        log.debug(joiner.toString());
    }

    /**
     * 记录请求头
     */
    private void logRequestHeaders(HttpServletRequest request) {
        StringJoiner joiner = new StringJoiner(", ", "[Headers] {", "}");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            // 敏感头脱敏
            if (isSensitiveHeader(name)) {
                value = "******";
            }
            joiner.add(name + "=" + value);
        }
        log.debug(joiner.toString());
    }

    /**
     * 判断是否为敏感参数
     */
    private boolean isSensitiveParam(String paramName) {
        String lower = paramName.toLowerCase();
        return lower.contains("password") || lower.contains("secret")
                || lower.contains("token") || lower.contains("key");
    }

    /**
     * 判断是否为敏感请求头
     */
    private boolean isSensitiveHeader(String headerName) {
        String lower = headerName.toLowerCase();
        return lower.contains("authorization") || lower.contains("token")
                || lower.contains("cookie") || lower.contains("secret");
    }

    /**
     * 判断路径是否被排除
     */
    private boolean isExcluded(String uri) {
        for (String pattern : properties.getExcludePaths()) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }
}
