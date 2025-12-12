package com.junoyi.framework.security.helper;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
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

    @Value("${junoyi.auth.token.header}")
    private String header;

    @Value("${junoyi.auth.token.secret")
    private String secret;
}