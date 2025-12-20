package com.junoyi.framework.security.token;

import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.properties.SecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static com.junoyi.framework.core.constant.Constants.*;

/**
 * JWT Token 服务实现类
 * 提供安全的 Token 生成、解析和验证功能
 * 安全特性：
 * 1. 使用 HS512 算法签名，密钥长度 >= 512 位
 * 2. Token 包含唯一 JTI，防止重放攻击
 * 3. 设置过期时间，限制 Token 有效期
 * 4. 区分 AccessToken 和 RefreshToken
 * 5. 支持多平台不同过期时间
 * 
 * @author Fan
 */
@Component
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(JwtTokenService.class);

    private final SecurityProperties securityProperties;



    /**
     * 创建访问令牌
     * 
     * @param loginUser 登录用户信息
     * @return 访问令牌字符串
     */
    @Override
    public String createAccessToken(LoginUser loginUser) {
        try {
            // 生成唯一 JTI（JWT ID），防止重放攻击
            String jti = UUID.randomUUID().toString().replace("-", "");

            // 获取平台类型对应的过期时间
            Duration expireDuration = getAccessExpireDuration(loginUser.getPlatformType());
            Date now = new Date();
            Date expiration = new Date(now.getTime() + expireDuration.toMillis());

            //  构建 JWT Token
            String accessToken = Jwts.builder()
                    // 设置主题（用户ID）
                    .subject(String.valueOf(loginUser.getUserId()))
                    // 设置 Token 类型
                    .claim(CLAIM_TYPE, TOKEN_TYPE_ACCESS)
                    // 设置唯一标识
                    .claim(CLAIM_JTI, jti)
                    // 设置平台类型
                    .claim(CLAIM_PLATFORM, loginUser.getPlatformType().getCode())
                    // 设置用户名
                    .claim(CLAIM_USERNAME, loginUser.getUserName())
                    // 设置昵称
                    .claim(CLAIM_NICK_NAME, loginUser.getNickName())
                    // 设置签发时间
                    .issuedAt(now)
                    // 设置过期时间
                    .expiration(expiration)
                    // 使用 HS512 算法签名
                    .signWith(getSecretKey(), Jwts.SIG.HS512)
                    .compact();

            log.info("TokenCreated", "创建 AccessToken 成功 | 用户: " + loginUser.getUserName() 
                    + " | 平台: " + loginUser.getPlatformType().getLabel() 
                    + " | 过期时间: " + expireDuration.toMinutes() + "分钟");

            return accessToken;

        } catch (Exception e) {
            log.error("TokenCreateError", "创建 AccessToken 失败", e);
            throw new RuntimeException("创建访问令牌失败", e);
        }
    }

    /**
     * 解析访问令牌
     * 
     * @param accessToken 访问令牌字符串
     * @return 登录用户信息
     */
    @Override
    public LoginUser paresAccessToken(String accessToken) {
        try {
            // 1. 解析 Token
            Claims claims = parseToken(accessToken);

            // 2. 验证 Token 类型
            String tokenType = claims.get(CLAIM_TYPE, String.class);
            if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
                log.warn("TokenTypeError", "Token 类型不匹配，期望: access，实际: " + tokenType);
                return null;
            }

            // 3. 提取用户信息
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get(CLAIM_USERNAME, String.class);
            String nickName = claims.get(CLAIM_NICK_NAME, String.class);
            Integer platformCode = claims.get(CLAIM_PLATFORM, Integer.class);

            // 4. 构建 LoginUser 对象
            LoginUser loginUser = LoginUser.builder()
                    .userId(userId)
                    .userName(username)
                    .nickName(nickName)
                    .platformType(getPlatformType(platformCode))
                    .build();

            log.debug("TokenParsed", "解析 AccessToken 成功 | 用户: " + username);

            return loginUser;

        } catch (ExpiredJwtException e) {
            log.warn("TokenExpired", "AccessToken 已过期");
            return null;
        } catch (SignatureException e) {
            log.warn("TokenSignatureError", "AccessToken 签名验证失败");
            return null;
        } catch (Exception e) {
            log.error("TokenParseError", "解析 AccessToken 失败", e);
            return null;
        }
    }

    /**
     * 验证访问令牌
     * 
     * @param accessToken 访问令牌字符串
     * @return 验证结果，true表示有效，false表示无效
     */
    @Override
    public boolean validateAccessToken(String accessToken) {
        if (StringUtils.isBlank(accessToken))
            return false;

        try {
            // 解析 Token（会自动验证签名和过期时间）
            Claims claims = parseToken(accessToken);

            // 验证 Token 类型
            String tokenType = claims.get(CLAIM_TYPE, String.class);
            if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
                log.warn("TokenValidationFailed", "Token 类型不匹配");
                return false;
            }

            // 验证必要字段
            if (claims.getSubject() == null || claims.getExpiration() == null) {
                log.warn("TokenValidationFailed", "Token 缺少必要字段");
                return false;
            }

            log.debug("TokenValidated", "AccessToken 验证通过");
            return true;

        } catch (ExpiredJwtException e) {
            log.debug("TokenExpired", "AccessToken 已过期");
            return false;
        } catch (SignatureException e) {
            log.warn("TokenSignatureError", "AccessToken 签名验证失败");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("TokenMalformed", "AccessToken 格式错误");
            return false;
        } catch (Exception e) {
            log.error("TokenValidationError", "验证 AccessToken 失败", e);
            return false;
        }
    }

    /**
     * 创建刷新令牌
     * 
     * @param loginUser 登录用户信息
     * @return 刷新令牌字符串
     */
    @Override
    public String createRefreshToken(LoginUser loginUser) {
        try {
            // 1. 生成唯一 JTI
            String jti = UUID.randomUUID().toString().replace("-", "");

            // 2. 获取平台类型对应的过期时间（RefreshToken 有效期更长）
            Duration expireDuration = getRefreshExpireDuration(loginUser.getPlatformType());
            Date now = new Date();
            Date expiration = new Date(now.getTime() + expireDuration.toMillis());

            // 3. 构建 JWT Token
            String refreshToken = Jwts.builder()
                    .subject(String.valueOf(loginUser.getUserId()))
                    .claim(CLAIM_TYPE, TOKEN_TYPE_REFRESH)
                    .claim(CLAIM_JTI, jti)
                    .claim(CLAIM_PLATFORM, loginUser.getPlatformType().getCode())
                    .claim(CLAIM_USERNAME, loginUser.getUserName())
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(getSecretKey(), Jwts.SIG.HS512)
                    .compact();

            log.info("TokenCreated", "创建 RefreshToken 成功 | 用户: " + loginUser.getUserName() 
                    + " | 过期时间: " + expireDuration.toDays() + "天");

            return refreshToken;

        } catch (Exception e) {
            log.error("TokenCreateError", "创建 RefreshToken 失败", e);
            throw new RuntimeException("创建刷新令牌失败", e);
        }
    }

    /**
     * 解析刷新令牌
     * 
     * @param refreshToken 刷新令牌字符串
     * @return 登录用户信息
     */
    @Override
    public LoginUser pareRefreshToken(String refreshToken) {
        try {
            Claims claims = parseToken(refreshToken);

            // 验证 Token 类型
            String tokenType = claims.get(CLAIM_TYPE, String.class);
            if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
                log.warn("TokenTypeError", "Token 类型不匹配，期望: refresh，实际: " + tokenType);
                return null;
            }

            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get(CLAIM_USERNAME, String.class);
            Integer platformCode = claims.get(CLAIM_PLATFORM, Integer.class);

            LoginUser loginUser = LoginUser.builder()
                    .userId(userId)
                    .userName(username)
                    .platformType(getPlatformType(platformCode))
                    .build();

            log.debug("TokenParsed", "解析 RefreshToken 成功 | 用户: " + username);

            return loginUser;

        } catch (Exception e) {
            log.error("TokenParseError", "解析 RefreshToken 失败", e);
            return null;
        }
    }

    /**
     * 验证刷新令牌
     * 
     * @param refreshToken 刷新令牌字符串
     * @return 验证结果，true表示有效，false表示无效
     */
    @Override
    public boolean validateRefreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return false;
        }

        try {
            Claims claims = parseToken(refreshToken);

            String tokenType = claims.get(CLAIM_TYPE, String.class);
            if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
                return false;
            }

            return claims.getSubject() != null && claims.getExpiration() != null;

        } catch (Exception e) {
            log.debug("TokenValidationFailed", "RefreshToken 验证失败: " + e.getMessage());
            return false;
        }
    }


    /**
     * 解析 Token
     * 
     * @param token Token 字符串
     * @return Claims 对象
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取签名密钥
     * 使用 HMAC-SHA512 算法，密钥长度必须 >= 512 位（64 字节）
     * 
     * @return SecretKey 对象
     */
    private SecretKey getSecretKey() {
        String secret = securityProperties.getToken().getSecret();
        
        // 安全检查：密钥长度必须足够长
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT 密钥长度不足，至少需要 32 个字符（建议 64 个字符）");
        }
        
        // 将密钥转换为字节数组，使用 UTF-8 编码
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        
        // 创建 HMAC-SHA 密钥
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取 AccessToken 过期时间
     * 
     * @param platformType 平台类型
     * @return Duration 对象
     */
    private Duration getAccessExpireDuration(PlatformType platformType) {
        String platformKey = getPlatformKey(platformType);
        String expireStr = securityProperties.getToken().getAccessExpire().get(platformKey);
        
        if (StringUtils.isBlank(expireStr)) {
            // 默认 30 分钟
            log.warn("TokenExpireNotConfigured", "平台 " + platformKey + " 未配置 AccessToken 过期时间，使用默认值 30m");
            return Duration.ofMinutes(30);
        }
        
        return DurationStyle.detectAndParse(expireStr);
    }

    /**
     * 获取 RefreshToken 过期时间
     * 
     * @param platformType 平台类型
     * @return Duration 对象
     */
    private Duration getRefreshExpireDuration(PlatformType platformType) {
        String platformKey = getPlatformKey(platformType);
        String expireStr = securityProperties.getToken().getRefreshExpire().get(platformKey);
        
        if (StringUtils.isBlank(expireStr)) {
            // 默认 7 天
            log.warn("TokenExpireNotConfigured", "平台 " + platformKey + " 未配置 RefreshToken 过期时间，使用默认值 7d");
            return Duration.ofDays(7);
        }
        
        return DurationStyle.detectAndParse(expireStr);
    }

    /**
     * 根据平台类型获取配置键名
     * 
     * @param platformType 平台类型
     * @return 配置键名
     */
    private String getPlatformKey(PlatformType platformType) {
        return switch (platformType) {
            case ADMIN_WEB, FRONT_DESK_WEB -> "web";
            case MINI_PROGRAM -> "miniprogram";
            case APP -> "app";
            case DESKTOP_APP -> "desktop";
        };
    }

    /**
     * 根据平台代码获取平台类型
     * 
     * @param code 平台代码
     * @return 平台类型
     */
    private PlatformType getPlatformType(Integer code) {
        if (code == null)
            return PlatformType.ADMIN_WEB;

        for (PlatformType type : PlatformType.values()) {
            if (type.getCode() == code)
                return type;
        }
        
        return PlatformType.ADMIN_WEB;
    }
}
