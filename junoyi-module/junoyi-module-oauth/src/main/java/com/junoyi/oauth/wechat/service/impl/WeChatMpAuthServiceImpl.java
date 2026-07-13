package com.junoyi.oauth.wechat.service.impl;

import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.oauth.wechat.domain.vo.OauthUserInfoVO;
import com.junoyi.oauth.wechat.service.IWeChatMpAuthService;
import com.junoyi.platform.auth.OAuthProvider;
import com.junoyi.platform.core.PlatformManager;
import com.junoyi.platform.domain.OAuthRequest;
import com.junoyi.platform.domain.OAuthResponse;
import com.junoyi.platform.enums.Platform;
import com.junoyi.system.domain.dto.SysThirdAuthUserDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.po.SysUserThirdAuth;
import com.junoyi.system.domain.vo.AuthVO;
import com.junoyi.system.enums.SysUserStatus;
import com.junoyi.system.helper.LoginUserBuilder;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.mapper.SysUserThirdAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 微信小程序认证登录业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class WeChatMpAuthServiceImpl implements IWeChatMpAuthService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(WeChatMpAuthServiceImpl.class);

    private final SysUserMapper sysUserMapper;
    private final SysUserThirdAuthMapper sysUserThirdAuthMapper;
    private final AuthHelper authHelper;
    private final PlatformManager platformManager;
    private final LoginUserBuilder loginUserBuilder;

    /**
     * 微信小程序登录
     * @param code 微信小程序用户获取的的code
     * @return 返回 Token 对
     */
    @Override
    public AuthVO login(String code) {

        // 获取请求信息
        String loginIp = ServletUtils.getClientIp();
        String userAgent = ServletUtils.getUserAgent();

        OAuthProvider oAuthProvider = platformManager.getOAuthProvider(Platform.WECHAT, "mp");
        if (oAuthProvider == null){
            throw new RuntimeException("未开启微信小程序支持");
        }

        OAuthRequest request = OAuthRequest.builder()
                .code(code)
                .build();

        OAuthResponse response = oAuthProvider.getUserInfo(request);

        if (!"SUCCESS".equals(response.getCode())) {
            throw new RuntimeException("微信授权失败: " + response.getMessage());
        }
        String openId = response.getOpenId();
        SysThirdAuthUserDTO userSnapshot = sysUserThirdAuthMapper.selectUserSnapshotByAuth("wechat_mp",openId);
        SysUser user;

        // 未绑定过: 创建新用户并绑定
        if (userSnapshot == null){
            user = new SysUser();
            // 使用 openid 前 8 位作为用户名
            user.setUserName("mp_" + openId.substring(0, 8));
            user.setNickName("微信用户");
            user.setStatus(SysUserStatus.NORMAL.getCode());
            user.setDelFlag(false);
            user.setCreateTime(new Date());
            user.setCreateBy("system");
            sysUserMapper.insert(user);

            SysUserThirdAuth thirdAuth = new SysUserThirdAuth();
            thirdAuth.setUserId(user.getUserId());
            thirdAuth.setAuthType("wechat_mp");
            thirdAuth.setAuthKey(openId);
            thirdAuth.setCreateTime(new Date());
            thirdAuth.setUpdateTime(new Date());
            sysUserThirdAuthMapper.insert(thirdAuth);

            log.info("创建新的微信小程序用户，userId: {}, openid: {}", user.getUserId(), openId);
        } else {
            // 已绑定：直接使用联表快照构建用户，避免再次按主键查询
            if (userSnapshot.getUserId() == null || Boolean.TRUE.equals(userSnapshot.getDelFlag())) {
                throw new RuntimeException("用户不存在或已被删除");
            }
            if (SysUserStatus.DISABLED.getCode() == userSnapshot.getStatus()
                    || SysUserStatus.LOCKED.getCode() == userSnapshot.getStatus()) {
                throw new RuntimeException("用户已被禁用");
            }

            user = new SysUser();
            user.setUserId(userSnapshot.getUserId());
            user.setUserName(userSnapshot.getUserName());
            user.setNickName(userSnapshot.getNickName());
        }

        // 使用 LoginUserBuilder 构建 LoginUser
        LoginUser loginUser = loginUserBuilder.build(user);

        // 构建 token 对
        TokenPair tokenPair = authHelper.login(loginUser, PlatformType.MINI_PROGRAM, loginIp, userAgent);

        // 构建返回接口
        return AuthVO.builder()
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .build();

    }

    /**
     * 获取用户信息
     * @param userId 用户Id
     * @return 用户信息
     */
    @Override
    public OauthUserInfoVO getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        OauthUserInfoVO oauthUserInfoVO = new OauthUserInfoVO();
        oauthUserInfoVO.setUserId(user.getUserId());
        oauthUserInfoVO.setAvatar(user.getAvatar());
        oauthUserInfoVO.setUserName(user.getUserName());
        oauthUserInfoVO.setNickName(user.getNickName());
        oauthUserInfoVO.setPhoneNumber(user.getPhonenumber());
        oauthUserInfoVO.setEmail(user.getEmail());
        return oauthUserInfoVO;
    }

    /**
     * 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return 令牌对
     */
    @Override
    public AuthVO refreshToken(String refreshToken) {
        TokenPair tokenPair = authHelper.refresh(refreshToken);
        return AuthVO.builder()
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .build();
    }

    /**
     * 退出当前会话
     * @return 如果成功就返回true，否则返回false
     */
    @Override
    public boolean logout() {
        return authHelper.logout();
    }
}