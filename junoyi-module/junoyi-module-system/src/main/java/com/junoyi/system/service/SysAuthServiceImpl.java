package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.domain.base.BaseException;
import com.junoyi.framework.core.exception.auth.LoginPasswordIsNullException;
import com.junoyi.framework.core.exception.auth.LoginPasswordWrongException;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.system.domain.dto.LoginRequest;
import com.junoyi.system.domain.po.LoginIdentity;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.AuthVo;
import com.junoyi.system.enums.LoginType;
import com.junoyi.system.enums.SysUserStatus;
import com.junoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 系统认证服务实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysAuthServiceImpl implements ISysAuthService {

    private final AuthHelper authHelper;
    private final SysUserMapper sysUserMapper;

    @Override
    public AuthVo login(LoginRequest loginRequest) {
        // 解析登录账号类型
        LoginIdentity loginIdentity = parseIdentity(loginRequest);

        // 获取请求信息
        String loginIp = ServletUtils.getClientIp();
        String userAgent = ServletUtils.getUserAgent();
        
        // 获取平台类型
        PlatformType platformType = loginRequest.getPlatformType() != null 
                ? loginRequest.getPlatformType() 
                : PlatformType.ADMIN_WEB;

        try {
            // 根据账号类型查询用户
            SysUser user = findUserByIdentity(loginIdentity);

            // 登录校验（用户状态等）
            validateUser(user);

            // 校验密码
            validatePassword(loginRequest.getPassword(), user.getSalt(), user.getPassword());

            // 构建 LoginUser
            LoginUser loginUser = buildLoginUser(user);

            // 调用 AuthHelper 登录（自动创建会话存入 Redis）
            TokenPair tokenPair = authHelper.login(loginUser, platformType, loginIp, userAgent);

            // 登录成功，清除失败记录
            authHelper.onLoginSuccess(loginIdentity.getAccount(), platformType, loginIp);

            // 构建返回结果
            AuthVo authVo = new AuthVo();
            authVo.setAccessToken(tokenPair.getAccessToken());
            authVo.setRefreshToken(tokenPair.getRefreshToken());

            return authVo;
            
        } catch (Exception e) {
            // 登录失败，记录失败次数
            boolean locked = authHelper.onLoginFail(loginIdentity.getAccount(), platformType, loginIp);
            if (locked) {
                throw new RuntimeException("登录失败次数过多，账号已被锁定，请稍后再试");
            }
            throw e;
        }
    }

    /**
     * 解析登录账号类型
     */
    private LoginIdentity parseIdentity(LoginRequest request) {
        if (StringUtils.isNotBlank(request.getPhonenumber()))
            return new LoginIdentity(LoginType.PHONENUMBER, request.getPhonenumber());

        if (StringUtils.isNotBlank(request.getEmail()))
            return new LoginIdentity(LoginType.EMAIL, request.getEmail());

        if (StringUtils.isNotBlank(request.getUsername()))
            return new LoginIdentity(LoginType.USERNAME, request.getUsername());

        throw new RuntimeException("登录账号不能为空");
    }

    /**
     * 根据登录标识查询用户
     */
    private SysUser findUserByIdentity(LoginIdentity identity) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::isDelFlag, false);
        
        switch (identity.getLoginType()) {
            case USERNAME -> wrapper.eq(SysUser::getUserName, identity.getAccount());
            case PHONENUMBER -> wrapper.eq(SysUser::getPhonenumber, identity.getAccount());
            case EMAIL -> wrapper.eq(SysUser::getEmail, identity.getAccount());
        }

        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null)
            throw new RuntimeException("用户不存在或已被删除");

        return user;
    }

    /**
     * 校验用户状态
     */
    private void validateUser(SysUser user) {
        if (user.isDelFlag())
            throw new RuntimeException("用户已被删除");

        if (user.getStatus() == SysUserStatus.DISABLED.getCode())
            throw new RuntimeException("用户已被禁用");

        if (user.getStatus() == SysUserStatus.LOCKED.getCode())
            throw new RuntimeException("用户已被锁定");

    }

    /**
     * 校验密码
     */
    private void validatePassword(String rawPassword, String salt, String encodedPassword) {
        if (StringUtils.isBlank(rawPassword))
            throw new LoginPasswordIsNullException("密码不能为空");

        if (!PasswordUtils.matches(rawPassword, salt, encodedPassword)) {
            throw new LoginPasswordWrongException("密码错误");
        }
    }

    /**
     * 构建 LoginUser（不再设置 platformType，由 login 参数传入）
     */
    private LoginUser buildLoginUser(SysUser user) {
        // TODO: 从数据库查询用户权限和角色
        Set<String> permissions = getUserPermissions(user.getUserId());
        Set<Long> roles = getUserRoles(user.getUserId());

        return LoginUser.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .nickName(user.getNickName())
                // platformType 不在这里设置，由 authService.login() 参数传入
                .permissions(permissions)
                .roles(roles)
                .build();
    }

    /**
     * 获取用户权限列表
     * TODO: 实现从数据库查询
     */
    private Set<String> getUserPermissions(Long userId) {
        // 这里应该从数据库查询用户权限
        // return sysPermissionMapper.selectPermissionsByUserId(userId);
        return new HashSet<>();
    }

    /**
     * 获取用户角色ID列表
     * TODO: 实现从数据库查询
     */
    private Set<Long> getUserRoles(Long userId) {
        // 这里应该从数据库查询用户角色
        // return sysRoleMapper.selectRoleIdsByUserId(userId);
        return new HashSet<>();
    }
}
