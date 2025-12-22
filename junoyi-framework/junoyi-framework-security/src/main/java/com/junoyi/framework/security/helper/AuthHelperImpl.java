package com.junoyi.framework.security.helper;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.UserSession;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.framework.security.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.junoyi.framework.core.constant.CacheConstants.LOGIN_FAIL;
import static com.junoyi.framework.core.constant.CacheConstants.LOGIN_FAIL_IP;

/**
 * 认证服务助手实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class AuthHelperImpl implements AuthHelper {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(AuthHelperImpl.class);

    private final SessionHelper sessionHelper;
    private final SecurityProperties securityProperties;
    /**
     * 用户登录认证（推荐使用）
     *
     * @param loginUser    登录用户信息
     * @param platformType 登录平台类型
     * @param loginIp      登录IP地址
     * @param userAgent    用户代理信息
     * @return TokenPair 访问令牌和刷新令牌对
     */
    @Override
    public TokenPair login(LoginUser loginUser, PlatformType platformType, String loginIp, String userAgent) {
        // 设置平台类型
        loginUser.setPlatformType(platformType);
        return sessionHelper.login(loginUser, loginIp, userAgent);
    }

    /**
     * 用户登录认证（简化版，只传平台类型）
     *
     * @param loginUser    登录用户信息
     * @param platformType 登录平台类型
     * @return TokenPair 访问令牌和刷新令牌对
     */
    @Override
    public TokenPair login(LoginUser loginUser, PlatformType platformType) {
        return login(loginUser, platformType, null, null);
    }

    /**
     * 用户登录认证（使用默认平台 ADMIN_WEB）
     *
     * @param loginUser 登录用户信息
     * @param loginIp   登录IP地址
     * @param userAgent 用户代理信息
     * @return TokenPair 访问令牌和刷新令牌对
     */
    @Override
    public TokenPair login(LoginUser loginUser, String loginIp, String userAgent) {
        // 如果 loginUser 中已设置 platformType 则使用，否则默认 ADMIN_WEB
        PlatformType platform = loginUser.getPlatformType() != null 
                ? loginUser.getPlatformType() 
                : PlatformType.ADMIN_WEB;
        return login(loginUser, platform, loginIp, userAgent);
    }

    /**
     * 用户登录认证（最简版）
     *
     * @param loginUser 登录用户信息
     * @return TokenPair 访问令牌和刷新令牌对
     */
    @Override
    public TokenPair login(LoginUser loginUser) {
        return login(loginUser, PlatformType.ADMIN_WEB, null, null);
    }

    /**
     * 用户登出
     *
     * @param token 访问令牌
     * @return boolean 登出是否成功
     */
    @Override
    public boolean logout(String token) {
        return sessionHelper.logout(token);
    }

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return TokenPair 新的访问令牌和刷新令牌对
     */
    @Override
    public TokenPair refresh(String refreshToken) {
        return sessionHelper.refreshToken(refreshToken);
    }

    /**
     * 根据令牌获取登录用户信息
     *
     * @param token 访问令牌
     * @return LoginUser 登录用户信息
     */
    @Override
    public LoginUser getLoginUser(String token) {
        return sessionHelper.getLoginUser(token);
    }

    /**
     * 根据令牌获取用户会话信息
     *
     * @param token 访问令牌
     * @return UserSession 用户会话信息
     */
    @Override
    public UserSession getSession(String token) {
        return sessionHelper.getSession(token);
    }

    /**
     * 根据令牌ID获取用户会话信息
     *
     * @param tokenId 令牌ID
     * @return UserSession 用户会话信息
     */
    @Override
    public UserSession getSessionByTokenId(String tokenId) {
        return sessionHelper.getSessionByTokenId(tokenId);
    }

    /**
     * 验证令牌是否有效
     *
     * @param token 访问令牌
     * @return boolean 令牌是否有效
     */
    @Override
    public boolean isValid(String token) {
        return sessionHelper.isValid(token);
    }

    /**
     * 更新用户权限信息
     *
     * @param tokenId 令牌ID
     * @param loginUser 更新后的登录用户信息
     * @return boolean 更新是否成功
     */
    @Override
    public boolean updatePermissions(String tokenId, LoginUser loginUser) {
        return sessionHelper.updateSession(tokenId, loginUser);
    }

    /**
     * 获取指定用户的全部会话信息
     *
     * @param userId 用户ID
     * @return List<UserSession> 用户会话列表
     */
    @Override
    public List<UserSession> getUserSessions(Long userId) {
        return sessionHelper.getUserSessions(userId);
    }

    /**
     * 强制踢出指定会话
     *
     * @param tokenId 令牌ID
     * @return boolean 踢出操作是否成功
     */
    @Override
    public boolean kickOut(String tokenId) {
        return sessionHelper.kickOut(tokenId);
    }

    /**
     * 强制踢出指定用户的所有会话
     *
     * @param userId 用户ID
     * @return int 被踢出会话的数量
     */
    @Override
    public int kickOutAll(Long userId) {
        return sessionHelper.kickOutAll(userId);
    }


    /**
     * 获取账户登录失败次数的Redis键值
     * @param account 账户名
     * @param platformType 平台类型
     * @return 返回格式为 LOGIN_FAIL + platformType.getCode() + ":" + account 的键值字符串
     */
    private String getAccountKey(String account, PlatformType platformType){
        return LOGIN_FAIL + platformType.getCode() + ":" + account;
    }

    /**
     * 获取IP登录失败次数的Redis键值
     * @param ip IP地址
     * @return 返回格式为 LOGIN_FAIL_IP + ip 的键值字符串
     */
    private String getIpKey(String ip){
        return LOGIN_FAIL_IP + ip;
    }


    /**
     * 处理登录失败
     * @param account 登录失败的账户名
     * @param platformType 登录的平台类型
     * @param ip 登录失败时的客户端IP地址
     * @return 如果返回true账号/IP已经锁定需要等待冷却，返回false就还能尝试登录
     */
    @Override
    public boolean onLoginFail(String account,PlatformType platformType, String ip){
        // 获取账号错误次数
        String accountKey = getAccountKey(account,platformType);
        Integer accountFailCount = (Integer) RedisUtils.getCacheObject(accountKey);

        accountFailCount = accountFailCount == null ? 1 : accountFailCount ++;

        if (accountFailCount >= securityProperties.getLogin().getMaxFailCount()){
            log.info("", "账号超过登录次数，进入登录冷却");

            RedisUtils.setCacheObject(accountKey, accountFailCount,
                    Duration.ofMinutes(securityProperties.getLogin().getFailCollDownMinutes()));

        }

        // IP限制模式
        if (securityProperties.getLogin().isEnableIpLimit()){
            String ipKey = getIpKey(ip);
            Integer ipFailCount = (Integer) RedisUtils.getCacheObject(ipKey);

            ipFailCount = ipFailCount == null ? 1 : ipFailCount ++;

            if (ipFailCount >= securityProperties.getLogin().getIpMaxFailCount() ){
                log.info("", "当前IP超过登录次数，已经锁定等待冷却");

                RedisUtils.setCacheObject(ipKey, ipFailCount,
                        Duration.ofMinutes(securityProperties.getLogin().getIpFailCollDownMinutes()));
            }

        }

        // 检查是否锁定进入冷却
        boolean accountLocked = accountFailCount >= securityProperties.getLogin().getMaxFailCount();
        boolean ipLocked = securityProperties.getLogin().isEnableIpLimit() &&
                (Integer) RedisUtils.getCacheObject(getIpKey(ip)) >= securityProperties.getLogin().getIpMaxFailCount();

        return accountLocked || ipLocked;
    }

    /**
     * 处理登录成功
     * @param account 登录成功的账户名
     * @param platformType 登录成功的平台类型
     * @param ip 登录成功的ip
     */
    @Override
    public void onLoginSuccess(String account,PlatformType platformType,String ip) {
        RedisUtils.deleteKeys(getAccountKey(account,platformType));
        if (securityProperties.getLogin().isEnableIpLimit()){
            RedisUtils.deleteKeys(getIpKey(ip));
        }
    }


}

