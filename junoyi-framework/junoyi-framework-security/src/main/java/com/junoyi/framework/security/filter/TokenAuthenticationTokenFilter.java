package com.junoyi.framework.security.filter;

import com.junoyi.framework.core.utils.StringUtils;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

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
                    .roles(session.getRoles())
                    .loginIp(session.getLoginIp())
                    .loginTime(session.getLoginTime())
                    .build();

            // 将用户信息存储到上下文中
            SecurityContext.set(loginUser);

            // 更新最后访问时间
            String tokenId = tokenService.getTokenId(token);
            sessionHelper.touch(tokenId);

            log.debug("TokenValidated", "User: " + loginUser.getUserName() + " | URI: " + requestURI);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("TokenValidationError", "URI: " + requestURI, e);
            sendUnauthorized(response, "认证失败");
        } finally {
            // 清理上下文
            SecurityContext.clear();
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
}
