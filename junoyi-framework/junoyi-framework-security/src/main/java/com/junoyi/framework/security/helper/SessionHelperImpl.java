package com.junoyi.framework.security.helper;

import com.junoyi.framework.security.exception.TokenExpiredException;
import com.junoyi.framework.core.utils.IPUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.core.utils.UserAgentUtils;
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
 * 滑动会话机制：
 * - Session TTL = AccessToken 有效期（如 30 分钟）
 * - 每次刷新 Token 时，Session TTL 滑动续期
 * - RefreshToken 过期时间作为会话最大生命周期
 * - Session 过期后，只要 RefreshToken 有效，刷新时会自动恢复 Session
 *
 * Redis 存储结构：
 * 1. junoyi:session:{tokenId}       -> UserSession（会话详情，TTL = AccessToken 有效期，滑动续期）
 * 2. junoyi:refresh:{tokenId}       -> UserSession（RefreshToken 白名单 + 会话备份，TTL = RefreshToken 有效期）
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
        String ipRegion = IPUtils.getIpRegion(loginIp);
        UserSession session = UserSession.builder()
                .sessionId(tokenId)
                .userId(loginUser.getUserId())
                .userName(loginUser.getUserName())
                .nickName(loginUser.getNickName())
                .platformType(loginUser.getPlatformType())
                .permissions(loginUser.getPermissions())
                .groups(loginUser.getGroups())
                .depts(loginUser.getDepts())
                .dataScope(loginUser.getDataScope())
                .accessibleDeptIds(loginUser.getAccessibleDeptIds())
                .superAdmin(loginUser.isSuperAdmin())
                .roles(loginUser.getRoles())
                .loginIp(loginIp)
                .ipRegion(ipRegion)
                .loginTime(now)
                .lastAccessTime(now)
                .userAgent(userAgent)
                .deviceType(UserAgentUtils.parseDeviceType(userAgent))
                .os(UserAgentUtils.parseOS(userAgent))
                .browser(UserAgentUtils.parseBrowser(userAgent))
                .accessExpireTime(tokenPair.getAccessExpireTime())
                .refreshExpireTime(tokenPair.getRefreshExpireTime())
                .build();

        // 计算 Session TTL（使用 AccessToken 的过期时间，实现滑动会话）
        long sessionTtlMillis = tokenPair.getAccessExpireTime() - System.currentTimeMillis();
        Duration sessionTtl = Duration.ofMillis(sessionTtlMillis);

        // 存储会话到 Redis（TTL = AccessToken 有效期）
        String sessionKey = SESSION + tokenId;
        RedisUtils.setCacheObject(sessionKey, session, sessionTtl);

        // 存储 RefreshToken 白名单（TTL = RefreshToken 有效期）
        // 存储完整的 UserSession，用于 Session 过期后恢复
        long refreshTtlMillis = tokenPair.getRefreshExpireTime() - System.currentTimeMillis();
        Duration refreshTtl = Duration.ofMillis(refreshTtlMillis);
        String refreshKey = REFRESH_TOKEN + tokenId;
        RedisUtils.setCacheObject(refreshKey, session, refreshTtl);

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
    }

    /**
     * 使用刷新令牌重新生成新的访问令牌（滑动会话模式）
     * 只刷新 AccessToken，RefreshToken 保持不变，到期必须重新登录
     * Session TTL 随 AccessToken 刷新而滑动续期
     * 
     * 当 Session 已过期但 RefreshToken 仍有效时，会从 RefreshToken 白名单恢复 Session
     *
     * @param refreshToken 刷新令牌字符串
     * @return TokenPair 新的访问令牌（refreshToken 保持原值）
     * @throws TokenExpiredException 若刷新令牌无效、已过期或已被撤销时抛出异常
     */
    @Override
    public TokenPair refreshToken(String refreshToken) {
        // 验证 RefreshToken
        if (!tokenService.validateRefreshToken(refreshToken))
            throw new TokenExpiredException("RefreshToken 无效或格式错误");

        // 获取 tokenId
        String tokenId = tokenService.getTokenId(refreshToken);
        if (StringUtils.isBlank(tokenId))
            throw new TokenExpiredException("无法解析 RefreshToken");

        // 检查 RefreshToken 白名单是否存在（存储的是 UserSession，用于恢复过期的 Session）
        String refreshKey = REFRESH_TOKEN + tokenId;
        UserSession refreshSession = RedisUtils.getCacheObject(refreshKey);
        if (refreshSession == null)
            throw new TokenExpiredException("RefreshToken 已被撤销或已过期，请重新登录");

        // 检查 RefreshToken 是否已过期（固定有效期，作为会话最大生命周期）
        if (refreshSession.getRefreshExpireTime() < System.currentTimeMillis())
            throw new TokenExpiredException("RefreshToken 已过期，请重新登录");

        // 获取会话（滑动会话模式下，Session 可能已过期）
        UserSession session = getSessionByTokenId(tokenId);
        
        // 如果 Session 已过期，从 RefreshToken 白名单恢复
        if (session == null) {
            session = refreshSession;
            log.info("SessionRecovered", "Session 已过期，从 RefreshToken 白名单恢复 | tokenId: " + tokenId.substring(0, 8) + "...");
        }

        // 构建 LoginUser
        LoginUser loginUser = LoginUser.builder()
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

        // 只生成新的 AccessToken（保持原有 tokenId）
        String newAccessToken = tokenService.createAccessToken(loginUser, tokenId);
        long newAccessExpireTime = tokenService.getAccessExpireTimeMillis(session.getPlatformType());

        // 更新会话中的 AccessToken 过期时间
        session.setAccessExpireTime(newAccessExpireTime);
        session.setLastAccessTime(new Date());
        
        // 计算 Session 滑动续期时长（取 AccessToken 有效期和 RefreshToken 剩余时间的较小值）
        long accessTtlMillis = newAccessExpireTime - System.currentTimeMillis();
        long refreshRemainingMillis = session.getRefreshExpireTime() - System.currentTimeMillis();
        long sessionTtlMillis = Math.min(accessTtlMillis, refreshRemainingMillis);
        Duration sessionTtl = Duration.ofMillis(sessionTtlMillis);
        
        // 保存更新后的会话（滑动续期 TTL）
        String sessionKey = SESSION + tokenId;
        RedisUtils.setCacheObject(sessionKey, session, sessionTtl);

        log.info("TokenRefreshed", "AccessToken 刷新成功，Session 滑动续期 | 用户: " + loginUser.getUserName()
                + " | tokenId: " + tokenId.substring(0, 8) + "..."
                + " | Session TTL: " + (sessionTtlMillis / 1000 / 60) + "分钟"
                + " | RefreshToken 剩余: " + (refreshRemainingMillis / 1000 / 60 / 60) + "小时");

        // 返回新的 AccessToken，RefreshToken 保持原值
        return TokenPair.builder()
                .tokenId(tokenId)
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)  // 保持原有的 refreshToken
                .accessExpireTime(newAccessExpireTime)
                .refreshExpireTime(session.getRefreshExpireTime())  // 保持原有的过期时间
                .build();
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
        session.setPermissions(loginUser.getPermissions());
        session.setGroups(loginUser.getGroups());
        session.setDepts(loginUser.getDepts());
        session.setDataScope(loginUser.getDataScope());
        session.setAccessibleDeptIds(loginUser.getAccessibleDeptIds());
        session.setSuperAdmin(loginUser.isSuperAdmin());
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
     * 同时清理已过期的会话索引（懒清理）
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

        List<UserSession> activeSessions = new ArrayList<>();
        Set<String> expiredTokenIds = new HashSet<>();

        for (String tokenId : tokenIds) {
            UserSession session = getSessionByTokenId(tokenId);
            if (session != null) {
                activeSessions.add(session);
            } else {
                // 会话已过期，标记为待清理
                expiredTokenIds.add(tokenId);
            }
        }

        // 懒清理：移除已过期的 tokenId
        if (!expiredTokenIds.isEmpty()) {
            Set<String> remainingTokenIds = new HashSet<>(tokenIds);
            remainingTokenIds.removeAll(expiredTokenIds);
            
            if (remainingTokenIds.isEmpty()) {
                RedisUtils.deleteObject(userSessionsKey);
            } else {
                RedisUtils.deleteObject(userSessionsKey);
                RedisUtils.setCacheSet(userSessionsKey, remainingTokenIds);
            }
            
            log.debug("SessionIndexCleaned", "清理过期会话索引 | userId: " + userId 
                    + " | 清理数量: " + expiredTokenIds.size());
        }

        return activeSessions;
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
     * 获取所有活跃会话
     *
     * @return List<UserSession> 所有活跃会话列表
     */
    @Override
    public List<UserSession> getAllSessions() {
        // 扫描所有会话键
        Collection<String> sessionKeys = RedisUtils.keys(SESSION + "*");
        if (sessionKeys == null || sessionKeys.isEmpty())
            return Collections.emptyList();

        List<UserSession> sessions = new ArrayList<>();
        for (String key : sessionKeys) {
            UserSession session = RedisUtils.getCacheObject(key);
            if (session != null) {
                sessions.add(session);
            }
        }
        
        // 按登录时间降序排列
        sessions.sort((a, b) -> {
            if (a.getLoginTime() == null) return 1;
            if (b.getLoginTime() == null) return -1;
            return b.getLoginTime().compareTo(a.getLoginTime());
        });
        
        return sessions;
    }

}
