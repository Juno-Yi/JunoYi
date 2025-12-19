package com.junoyi.framework.security.helper;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.module.LoginUser;
import org.redisson.api.RateType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Token工具类
 * 提供Token相关的辅助功能
 * @author Fan
 */
@Component
public class TokenHelper {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(TokenHelper.class);

    @Value("${junoyi.security.token.secret:}")
    private String secret;

    /**
     * 生成AccessToken
     * @return 返回AccessToken
     */
    public String createAccessToken(LoginUser loginUser){
        return null;
    }

    /**
     * 生成RefreshToken
     * @return 返回RefreshToken
     */
    public String createRefreshToken(){

        return null;
    }
}