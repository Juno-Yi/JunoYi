package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.security.exception.LoginAccountIsNullException;
import com.junoyi.framework.security.exception.LoginFailedAccountLockedException;
import com.junoyi.framework.security.exception.LoginPasswordIsNullException;
import com.junoyi.framework.security.exception.LoginPasswordWrongException;
import com.junoyi.system.exception.UserNotExistException;
import com.junoyi.system.exception.UserStatusIsDisableException;
import com.junoyi.system.exception.UserStatusIsLockedException;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.system.domain.bo.LoginBO;
import com.junoyi.system.domain.po.LoginIdentity;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.AuthVO;
import com.junoyi.system.domain.vo.UserInfoVO;
import com.junoyi.system.enums.LoginType;
import com.junoyi.system.enums.SysUserStatus;
import com.junoyi.system.helper.LoginUserBuilder;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.service.ISysAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统认证服务实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysAuthServiceImpl implements ISysAuthService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysAuthServiceImpl.class);

    private final AuthHelper authHelper;
    private final SysUserMapper sysUserMapper;
    private final LoginUserBuilder loginUserBuilder;

    @Override
    public AuthVO login(LoginBO loginBO) {
        // 解析登录账号类型
        LoginIdentity loginIdentity = parseIdentity(loginBO);

        // 获取请求信息
        String loginIp = ServletUtils.getClientIp();
        String userAgent = ServletUtils.getUserAgent();
        
        // 获取平台类型
        PlatformType platformType = loginBO.getPlatformType() != null
                ? loginBO.getPlatformType()
                : PlatformType.ADMIN_WEB;

        try {
            // 根据账号类型查询用户
            SysUser user = findUserByIdentity(loginIdentity);

            // 登录校验（用户状态等）
            validateUser(user);

            // 校验密码
            validatePassword(loginBO.getPassword(), user.getSalt(), user.getPassword());

            // 使用 LoginUserBuilder 构建 LoginUser
            LoginUser loginUser = loginUserBuilder.build(user);

            // 调用 AuthHelper 登录（自动创建会话存入 Redis）
            TokenPair tokenPair = authHelper.login(loginUser, platformType, loginIp, userAgent);

            // 登录成功，清除失败记录
            authHelper.onLoginSuccess(loginIdentity.getAccount(), platformType, loginIp);

            // 构建返回结果
            AuthVO authVo = new AuthVO();
            authVo.setAccessToken(tokenPair.getAccessToken());
            authVo.setRefreshToken(tokenPair.getRefreshToken());

            return authVo;
            
        } catch (Exception e) {
            // 登录失败，记录失败次数
            boolean locked = authHelper.onLoginFail(loginIdentity.getAccount(), platformType, loginIp);
            if (locked) {
                throw new LoginFailedAccountLockedException("登录失败次数过多，账号已被锁定，请稍后再试");
            }
            throw e;
        }
    }

    /**
     * 解析登录账号类型
     */
    private LoginIdentity parseIdentity(LoginBO request) {
        if (StringUtils.isNotBlank(request.getPhonenumber()))
            return new LoginIdentity(LoginType.PHONENUMBER, request.getPhonenumber());

        if (StringUtils.isNotBlank(request.getEmail()))
            return new LoginIdentity(LoginType.EMAIL, request.getEmail());

        if (StringUtils.isNotBlank(request.getUsername()))
            return new LoginIdentity(LoginType.USERNAME, request.getUsername());

        throw new LoginAccountIsNullException("登录账号不能为空");
    }

    /**
     * 根据登录标识查询用户
     * <p>
     * 安全检查：如果查询到多条记录，说明数据库存在重复数据，抛出异常
     */
    private SysUser findUserByIdentity(LoginIdentity identity) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::isDelFlag, false);
        
        switch (identity.getLoginType()) {
            case USERNAME -> wrapper.eq(SysUser::getUserName, identity.getAccount());
            case PHONENUMBER -> wrapper.eq(SysUser::getPhonenumber, identity.getAccount());
            case EMAIL -> wrapper.eq(SysUser::getEmail, identity.getAccount());
        }

        // 使用 selectList 查询，检查是否存在重复数据
        List<SysUser> users = sysUserMapper.selectList(wrapper);

        if (users == null || users.isEmpty()) {
            throw new UserNotExistException("用户不存在或已被删除");
        }

        // 安全检查：如果存在多条记录，说明数据不一致
        if (users.size() > 1) {
            log.error("安全", "数据库存在重复账号: loginType={}, account={}, count={}", 
                    identity.getLoginType(), identity.getAccount(), users.size());
            throw new UserNotExistException("系统数据异常，请联系管理员");
        }

        return users.get(0);
    }

    /**
     * 校验用户状态
     */
    private void validateUser(SysUser user) {
        if (user.isDelFlag())
            throw new UserNotExistException("用户账号或密码错误");

        if (user.getStatus() == SysUserStatus.DISABLED.getCode())
            throw new UserStatusIsDisableException("用户已被禁用");

        if (user.getStatus() == SysUserStatus.LOCKED.getCode())
            throw new UserStatusIsLockedException("用户已被锁定");
    }

    /**
     * 校验密码
     */
    private void validatePassword(String rawPassword, String salt, String encodedPassword) {
        if (StringUtils.isBlank(rawPassword))
            throw new LoginPasswordIsNullException("密码不能为空");

        if (!PasswordUtils.matches(rawPassword, salt, encodedPassword)) {
            throw new LoginPasswordWrongException("用户账号或密码错误");
        }
    }

    /**
     * 获取用户信息
     *
     * @param loginUser 用户会话信息（已包含权限、角色、部门等信息）
     * @return 返回用户信息
     */
    public UserInfoVO getUserInfo(LoginUser loginUser) {
        Long userId = loginUser.getUserId();

        SysUser sysUser = null;
        try {
            sysUser = sysUserMapper.selectById(userId);
        } catch (Exception e) {
            log.error("[getUserInfo] 查询用户失败, userId: {}, 异常: {}", userId, e.getMessage());
            if (e.getCause() != null) {
                log.error("[getUserInfo] 根本原因: {}", e.getCause().getMessage(), e.getCause());
            }
            throw e;
        }
        
        String avatar = "/default-avatar.png";
        String email = null;
        if (sysUser != null && !sysUser.isDelFlag()) {
            if (sysUser.getAvatar() != null && !sysUser.getAvatar().isBlank()) {
                avatar = sysUser.getAvatar();
            }
            email = sysUser.getEmail();
        }

        // 直接从 LoginUser（Redis Session）中获取权限信息，无需再查数据库
        return UserInfoVO.builder()
                .userId(userId)
                .userName(loginUser.getUserName())
                .nickName(loginUser.getNickName())
                .email(email)
                .avatar(avatar)
                .permissions(loginUser.getPermissions())
                .roles(loginUser.getRoles())
                .depts(loginUser.getDepts())
                .build();
    }

}
