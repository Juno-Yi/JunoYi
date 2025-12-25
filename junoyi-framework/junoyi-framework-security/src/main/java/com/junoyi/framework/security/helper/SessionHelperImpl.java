package com.junoyi.framework.security.helper;

import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.properties.SecurityProperties;
import com.junoyi.framework.security.module.UserSession;
import com.junoyi.framework.security.module.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.junoyi.framework.core.constant.CacheConstants.*;

/**
 * 会话服务助手实现类
 *
 * Redis 存储结构：
 * 1. junoyi:session:{tokenId}       -> UserSession（会话详情）
 * 2. junoyi:refresh:{tokenId}       -> userId（RefreshToken 白名单）
 * 3. junoyi:user:sessions:{userId}  -> Set<tokenId>（用户会话索引）
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SessionHelperImpl implements SessionHelper {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SessionHelperImpl.class);
    private final JwtTokenHelper tokenService;
    private final SecurityProperties securityProperties;

    /**
     * 用户登录并创建新的会话
     *
     * @param loginUser 登录用户信息
     * @param loginIp   登录IP地址
     * @param userAgent 客户端标识
     * @return TokenPair 包含访问令牌和刷新令牌的对象
     * @throws IllegalStateException 单点登录模式下，同平台已有会话时抛出
     */
    @Override
    public TokenPair login(LoginUser loginUser, String loginIp, String userAgent) {
        // 单点登录检查
        if (isSingleLoginEnabled()) {
            checkSingleLogin(loginUser.getUserId(), loginUser.getPlatformType());
        }

        // 创建 Token 对
        TokenPair tokenPair = tokenService.createTokenPair(loginUser);
        String tokenId = tokenPair.getTokenId();

        // 构建会话信息
        Date now = new Date();
        UserSession session = UserSession.builder()
                .sessionId(tokenId)
                .userId(loginUser.getUserId())
                .userName(loginUser.getUserName())
                .nickName(loginUser.getNickName())
                .platformType(loginUser.getPlatformType())
                .roles(loginUser.getRoles())
                .loginIp(loginIp)
                .loginTime(now)
                .lastAccessTime(now)
                .userAgent(userAgent)
                .deviceType(parseDeviceType(userAgent))
                .accessExpireTime(tokenPair.getAccessExpireTime())
                .refreshExpireTime(tokenPair.getRefreshExpireTime())
                .build();

        // 计算 TTL（使用 RefreshToken 的过期时间）
        long ttlMillis = tokenPair.getRefreshExpireTime() - System.currentTimeMillis();
        Duration ttl = Duration.ofMillis(ttlMillis);

        // 存储会话到 Redis
        String sessionKey = SESSION + tokenId;
        RedisUtils.setCacheObject(sessionKey, session, ttl);

        // 存储 RefreshToken 白名单
        String refreshKey = REFRESH_TOKEN + tokenId;
        RedisUtils.setCacheObject(refreshKey, loginUser.getUserId(), ttl);

        // 添加到用户会话索引（修复：使用 addToCacheSet 而不是 setCacheSet）
        addToUserSessionIndex(loginUser.getUserId(), tokenId);

        log.info("SessionCreated", "用户登录成功 | 用户: " + loginUser.getUserName()
                + " | 平台: " + loginUser.getPlatformType().getLabel()
                + " | tokenId: " + tokenId.substring(0, 8) + "..."
                + " | IP: " + loginIp
                + " | 单点登录: " + (isSingleLoginEnabled() ? "开启" : "关闭"));

        return tokenPair;
    }

    /**
     * 检查是否开启单点登录
     */
    private boolean isSingleLoginEnabled() {
        return securityProperties.getToken() != null 
                && securityProperties.getToken().isSingleLogin();
    }

    /**
     * 单点登录检查：同一用户同一平台是否已有会话
     */
    private void checkSingleLogin(Long userId, PlatformType platformType) {
        List<UserSession> existingSessions = getUserSessions(userId);
        
        for (UserSession session : existingSessions) {
            if (session.getPlatformType() == platformType) {
                log.warn("SingleLoginBlocked", "单点登录拦截 | 用户ID: " + userId 
                        + " | 平台: " + platformType.getLabel()
                        + " | 已有会话: " + session.getSessionId().substring(0, 8) + "...");
                throw new IllegalStateException("该平台已有登录会话，请先退出后再登录");
            }
        }
    }

    /**
     * 添加 tokenId 到用户会话索引
     */
    private void addToUserSessionIndex(Long userId, String tokenId) {
        String userSessionsKey = USER_SESSIONS + userId;
        Set<String> existingTokenIds = RedisUtils.getCacheSet(userSessionsKey);
        
        if (existingTokenIds == null) {
            existingTokenIds = new HashSet<>();
        } else {
            // 创建可修改的副本
            existingTokenIds = new HashSet<>(existingTokenIds);
        }
        
        existingTokenIds.add(tokenId);
        RedisUtils.deleteObject(userSessionsKey);
        RedisUtils.setCacheSet(userSessionsKey, existingTokenIds);
    }

    /**
     * 从用户会话索引中移除 tokenId
     */
    private void removeFromUserSessionIndex(Long userId, String tokenId) {
        String userSessionsKey = USER_SESSIONS + userId;
        Set<String> existingTokenIds = RedisUtils.getCacheSet(userSessionsKey);
        
        if (existingTokenIds == null || existingTokenIds.isEmpty())
            return;
        
        // 创建可修改的副本
        existingTokenIds = new HashSet<>(existingTokenIds);
        existingTokenIds.remove(tokenId);
        
        if (existingTokenIds.isEmpty()) {
            RedisUtils.deleteObject(userSessionsKey);
        } else {
            RedisUtils.deleteObject(userSessionsKey);
            RedisUtils.setCacheSet(userSessionsKey, existingTokenIds);
        }
    }

    /**
     * 根据访问令牌执行登出操作
     *
     * @param token 访问令牌字符串
     * @return boolean 是否成功登出
     */
    @Override
    public boolean logout(String token) {
        if (StringUtils.isBlank(token))
            return false;

        // 获取 tokenId
        String tokenId = tokenService.getTokenId(token);
        if (StringUtils.isBlank(tokenId))
            return false;

        return doLogout(tokenId);
    }

    /**
     * 执行登出逻辑
     *
     * @param tokenId 会话唯一标识符
     * @return boolean 是否成功登出
     */
    private boolean doLogout(String tokenId) {
        // 获取会话信息（用于获取 userId）
        UserSession session = getSessionByTokenId(tokenId);

        // 删除会话
        String sessionKey = SESSION + tokenId;
        RedisUtils.deleteObject(sessionKey);

        // 删除 RefreshToken 白名单
        String refreshKey = REFRESH_TOKEN + tokenId;
        RedisUtils.deleteObject(refreshKey);

        // 从用户会话索引中移除
        if (session != null) {
            removeFromUserSessionIndex(session.getUserId(), tokenId);

            log.info("SessionDestroyed", "用户登出成功 | 用户: " + session.getUserName()
                    + " | tokenId: " + tokenId.substring(0, 8) + "...");
        }

        return true;
    }

    /**
     * 根据访问令牌获取当前用户的会话信息
     *
     * @param token 访问令牌字符串
     * @return UserSession 当前用户的会话对象，若不存在则返回null
     */
    @Override
    public UserSession getSession(String token) {
        if (StringUtils.isBlank(token))
            return null;

        String tokenId = tokenService.getTokenId(token);
        return getSessionByTokenId(tokenId);
    }

    /**
     * 根据tokenId获取对应的会话信息
     *
     * @param tokenId 会话唯一标识符
     * @return UserSession 会话对象，若不存在则返回null
     */
    @Override
    public UserSession getSessionByTokenId(String tokenId) {
        if (StringUtils.isBlank(tokenId))
            return null;

        String sessionKey = SESSION + tokenId;
        return RedisUtils.getCacheObject(sessionKey);
    }

    /**
     * 根据访问令牌提取登录用户的基本信息
     *
     * @param token 访问令牌字符串
     * @return LoginUser 登录用户基本信息对象，若无效则返回null
     */
    @Override
    public LoginUser getLoginUser(String token) {
        UserSession session = getSession(token);
        if (session == null)
            return null;

        return LoginUser.builder()
                .userId(session.getUserId())
                .userName(session.getUserName())
                .nickName(session.getNickName())
                .platformType(session.getPlatformType())
                .roles(session.getRoles())
                .loginIp(session.getLoginIp())
                .loginTime(session.getLoginTime())
                .build();
    }

    /**
     * 使用刷新令牌重新生成一对新的访问与刷新令牌，并替换旧的会话
     *
     * @param refreshToken 刷新令牌字符串
     * @return TokenPair 新的一对访问与刷新令牌
     * @throws IllegalArgumentException 若刷新令牌无效、已过期或已被撤销时抛出异常
     */
    @Override
    public TokenPair refreshToken(String refreshToken) {
        // 验证 RefreshToken
        if (!tokenService.validateRefreshToken(refreshToken))
            throw new IllegalArgumentException("RefreshToken 无效或已过期");

        // 获取 tokenId
        String oldTokenId = tokenService.getTokenId(refreshToken);
        if (StringUtils.isBlank(oldTokenId))
            throw new IllegalArgumentException("无法解析 RefreshToken");

        // 检查 RefreshToken 是否在白名单中（是否被主动失效）
        String refreshKey = REFRESH_TOKEN + oldTokenId;
        if (!RedisUtils.isExistsObject(refreshKey))
            throw new IllegalArgumentException("RefreshToken 已被撤销");

        // 获取旧会话
        UserSession oldSession = getSessionByTokenId(oldTokenId);
        if (oldSession == null)
            throw new IllegalArgumentException("会话不存在或已过期");

        // 构建 LoginUser
        LoginUser loginUser = LoginUser.builder()
                .userId(oldSession.getUserId())
                .userName(oldSession.getUserName())
                .nickName(oldSession.getNickName())
                .platformType(oldSession.getPlatformType())
                .roles(oldSession.getRoles())
                .loginIp(oldSession.getLoginIp())
                .loginTime(oldSession.getLoginTime())
                .build();

        // 删除旧会话
        doLogout(oldTokenId);

        // 创建新会话
        TokenPair newTokenPair = login(loginUser, oldSession.getLoginIp(), oldSession.getUserAgent());

        log.info("TokenRefreshed", "Token 刷新成功 | 用户: " + loginUser.getUserName()
                + " | 旧tokenId: " + oldTokenId.substring(0, 8) + "..."
                + " | 新tokenId: " + newTokenPair.getTokenId().substring(0, 8) + "...");

        return newTokenPair;
    }

    /**
     * 更新指定会话中的用户权限等信息
     *
     * @param tokenId   会话唯一标识符
     * @param loginUser 要更新的用户信息
     * @return boolean 是否更新成功
     */
    @Override
    public boolean updateSession(String tokenId, LoginUser loginUser) {
        if (StringUtils.isBlank(tokenId))
            return false;

        UserSession session = getSessionByTokenId(tokenId);
        if (session == null)
            return false;

        // 更新会话信息
        session.setUserName(loginUser.getUserName());
        session.setNickName(loginUser.getNickName());
        session.setRoles(loginUser.getRoles());
        session.setLastAccessTime(new Date());

        // 保存到 Redis（保留原 TTL）
        String sessionKey = SESSION + tokenId;
        RedisUtils.setCacheObject(sessionKey, session, true);

        log.info("SessionUpdated", "会话更新成功 | 用户: " + loginUser.getUserName()
                + " | tokenId: " + tokenId.substring(0, 8) + "...");

        return true;
    }

    /**
     * 查询某个用户的所有活跃会话列表
     *
     * @param userId 用户ID
     * @return List<UserSession> 该用户的所有活跃会话集合
     */
    @Override
    public List<UserSession> getUserSessions(Long userId) {
        if (userId == null)
            return Collections.emptyList();

        String userSessionsKey = USER_SESSIONS + userId;
        Set<String> tokenIds = RedisUtils.getCacheSet(userSessionsKey);

        if (tokenIds == null || tokenIds.isEmpty())
            return Collections.emptyList();

        return tokenIds.stream()
                .map(this::getSessionByTokenId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 强制踢出会话（单个）
     *
     * @param tokenId 会话唯一标识符
     * @return boolean 是否成功踢出
     */
    @Override
    public boolean kickOut(String tokenId) {
        if (StringUtils.isBlank(tokenId))
            return false;

        UserSession session = getSessionByTokenId(tokenId);
        if (session != null) {
            log.info("SessionKicked", "会话被踢出 | 用户: " + session.getUserName()
                    + " | tokenId: " + tokenId.substring(0, 8) + "...");
        }

        return doLogout(tokenId);
    }

    /**
     * 强制踢出某用户的所有在线会话
     *
     * @param userId 用户ID
     * @return int 成功踢出的会话数量
     */
    @Override
    public int kickOutAll(Long userId) {
        if (userId == null)
            return 0;

        List<UserSession> sessions = getUserSessions(userId);
        int count = 0;

        for (UserSession session : sessions) {
            if (doLogout(session.getSessionId()))
                count++;
        }

        log.info("AllSessionsKicked", "用户所有会话被踢出 | userId: " + userId + " | 数量: " + count);

        return count;
    }

    /**
     * 验证给定的令牌是否有效且对应一个有效的会话
     *
     * @param token 待验证的令牌字符串
     * @return boolean 令牌是否有效
     */
    @Override
    public boolean isValid(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }

        // 验证 Token 签名
        if (!tokenService.validateAccessToken(token) && !tokenService.validateRefreshToken(token))
            return false;

        // 检查会话是否存在
        String tokenId = tokenService.getTokenId(token);
        if (StringUtils.isBlank(tokenId))
            return false;

        String sessionKey = SESSION + tokenId;
        return RedisUtils.isExistsObject(sessionKey);
    }

    /**
     * 更新会话最后访问时间
     *
     * @param tokenId 会话唯一标识符
     */
    @Override
    public void touch(String tokenId) {
        if (StringUtils.isBlank(tokenId))
            return;

        UserSession session = getSessionByTokenId(tokenId);
        if (session != null) {
            session.setLastAccessTime(new Date());
            String sessionKey = SESSION + tokenId;
            RedisUtils.setCacheObject(sessionKey, session, true);
        }
    }

    /**
     * 解析设备类型
     *
     * @param userAgent 浏览器请求头中的User-Agent字段
     * @return String 设备类型：Mobile / Tablet / Desktop / Unknown
     */
    private String parseDeviceType(String userAgent) {
        if (StringUtils.isBlank(userAgent))
            return "Unknown";

        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "Tablet";
        } else if (userAgent.contains("windows") || userAgent.contains("macintosh") || userAgent.contains("linux")) {
            return "Desktop";
        }

        return "Unknown";
    }
}
