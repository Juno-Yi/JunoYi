package com.junoyi.framework.security.filter;

import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder;
import com.junoyi.framework.datasource.datascope.DataScopeType;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.context.SecurityContext;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.properties.SecurityProperties;
import com.junoyi.framework.security.helper.SessionHelper;
import com.junoyi.framework.security.module.UserSession;
import com.junoyi.framework.security.helper.JwtTokenHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Token 认证过滤器
 * 用于拦截请求并验证 Token 的有效性
 * 验证流程：
 * 1. 检查白名单
 * 2. 验证 Token 签名（JWT 自验证）
 * 3. 从 Redis 获取会话（获取最新权限）
 * 4. 将用户信息存入上下文
 *
 * @author Fan
 */
@RequiredArgsConstructor
public class TokenAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(TokenAuthenticationTokenFilter.class);

    private final JwtTokenHelper tokenService;
    private final SessionHelper sessionHelper;
    private final SecurityProperties securityProperties;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    /**
     * 最后访问时间更新间隔（5分钟）
     * 避免每次请求都更新 Redis，减少 Redis 写入压力
     */
    private static final long TOUCH_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 放行 OPTIONS 预检请求（CORS）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        
        // 检查是否在白名单中
        if (isWhitelisted(requestURI)) {
//            log.info("WhitelistAccess", "Whitelist release: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求头中获取 Token
        String token = getTokenFromRequest(request);

        if (StringUtils.isBlank(token)) {
            log.warn("TokenMissing", "URI: " + requestURI);
            sendUnauthorized(response, "未提供认证令牌");
            return;
        }

        try {
            // 验证 Token 签名（JWT 自验证，不查 Redis）
            if (!tokenService.validateAccessToken(token)) {
                log.warn("TokenInvalid", "URI: " + requestURI + " | Token: " + maskToken(token));
                sendUnauthorized(response, "认证令牌无效或已过期");
                return;
            }

            // 从 Redis 获取会话（获取最新的权限信息）
            UserSession session = sessionHelper.getSession(token);
            
            if (session == null) {
                // 会话不存在（可能被踢出或主动登出）
                log.warn("SessionNotFound", "URI: " + requestURI + " | Token: " + maskToken(token));
                sendUnauthorized(response, "会话已失效，请重新登录");
                return;
            }

            // 构建 LoginUser（从会话中获取最新权限）
            LoginUser loginUser = LoginUser.builder()
                    .tokenId(session.getSessionId())
                    .userId(session.getUserId())
                    .userName(session.getUserName())
                    .nickName(session.getNickName())
                    .platformType(session.getPlatformType())
                    .permissions(session.getPermissions())
                    .groups(session.getGroups())
                    .depts(session.getDepts())
                    .dataScope(session.getDataScope())
                    .accessibleDeptIds(session.getAccessibleDeptIds())
                    .superAdmin(session.isSuperAdmin())
                    .roles(session.getRoles())
                    .loginIp(session.getLoginIp())
                    .loginTime(session.getLoginTime())
                    .build();

            // 将用户信息存储到上下文中
            SecurityContext.set(loginUser);

            // 设置数据范围上下文
            DataScopeContextHolder.set(DataScopeContextHolder.DataScopeContext.builder()
                    .userId(loginUser.getUserId())
                    .userName(loginUser.getUserName())
                    .deptIds(loginUser.getDepts())
                    .scopeType(DataScopeType.fromValue(loginUser.getDataScope()))
                    .accessibleDeptIds(loginUser.getAccessibleDeptIds())
                    .superAdmin(loginUser.isSuperAdmin())
                    .build());

            // 限频更新最后访问时间（每 5 分钟更新一次，异步执行）
             touchIfNeeded(session);

            log.debug("TokenValidated", "User: " + loginUser.getUserName() + " | URI: " + requestURI);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("TokenValidationError", "URI: " + requestURI, e);
            sendUnauthorized(response, "认证失败");
        } finally {
            // 清理上下文
            SecurityContext.clear();
            DataScopeContextHolder.clear();
        }
    }

    /**
     * 发送 401 未授权响应
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + message + "\"}");
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String headerName = securityProperties.getToken().getHeader();
        String token = request.getHeader(headerName);

        if (StringUtils.isNotBlank(token)) {
            // 移除 Bearer 前缀（如果存在）
            if (token.startsWith("Bearer "))
                token = token.substring(7);
            return token;
        }

        return request.getParameter("token");
    }

    /**
     * 检查请求路径是否在白名单中
     */
    private boolean isWhitelisted(String requestURI) {
        List<String> whitelist = securityProperties.getWhitelist();
        if (whitelist == null || whitelist.isEmpty())
            return false;

        for (String pattern : whitelist) {
            if (pathMatcher.match(pattern, requestURI))
                return true;
        }

        return false;
    }

    /**
     * 脱敏 Token（用于日志输出）
     */
    private String maskToken(String token) {
        if (StringUtils.isBlank(token) || token.length() < 10)
            return "***";
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }

    /**
     * 限频更新最后访问时间
     * 只有当距离上次更新超过 5 分钟时才异步更新，避免每次请求都写 Redis
     *
     * @param session 用户会话
     */
    private void touchIfNeeded(UserSession session) {
        if (session == null || session.getLastAccessTime() == null) {
            return;
        }
        
        long lastAccessTime = session.getLastAccessTime().getTime();
        long now = System.currentTimeMillis();
        
        // 距离上次更新超过 5 分钟才更新
        if (now - lastAccessTime > TOUCH_INTERVAL_MILLIS) {
            // 异步更新，不阻塞请求
            CompletableFuture.runAsync(() -> {
                try {
                    sessionHelper.touch(session.getSessionId());
                    log.debug("SessionTouched", "异步更新会话访问时间 | tokenId: " + session.getSessionId().substring(0, 8) + "...");
                } catch (Exception e) {
                    log.warn("SessionTouchFailed", "更新会话访问时间失败: " + e.getMessage());
                }
            });
        }
    }
}
