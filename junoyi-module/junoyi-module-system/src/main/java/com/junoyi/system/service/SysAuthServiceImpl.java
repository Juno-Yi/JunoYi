package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.exception.auth.LoginAccountIsNullException;
import com.junoyi.framework.core.exception.auth.LoginFailedAccountLockedException;
import com.junoyi.framework.core.exception.auth.LoginPasswordIsNullException;
import com.junoyi.framework.core.exception.auth.LoginPasswordWrongException;
import com.junoyi.framework.core.exception.user.UserNotExistException;
import com.junoyi.framework.core.exception.user.UserStatusIsDisableException;
import com.junoyi.framework.core.exception.user.UserStatusIsLockedException;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.system.domain.bo.LoginBO;
import com.junoyi.system.domain.po.LoginIdentity;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.po.SysUserDept;
import com.junoyi.system.domain.po.SysDeptGroup;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.po.SysRoleGroup;
import com.junoyi.system.domain.po.SysUserGroup;
import com.junoyi.system.domain.vo.AuthVo;
import com.junoyi.system.domain.po.SysUserRole;
import com.junoyi.system.domain.vo.UserInfoVO;
import com.junoyi.system.enums.LoginType;
import com.junoyi.system.enums.SysUserStatus;
import com.junoyi.system.mapper.SysDeptGroupMapper;
import com.junoyi.system.mapper.SysPermGroupMapper;
import com.junoyi.system.mapper.SysRoleGroupMapper;
import com.junoyi.system.mapper.SysUserDeptMapper;
import com.junoyi.system.mapper.SysUserGroupMapper;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysUserGroupMapper sysUserGroupMapper;
    private final SysRoleGroupMapper sysRoleGroupMapper;
    private final SysDeptGroupMapper sysDeptGroupMapper;
    private final SysPermGroupMapper sysPermGroupMapper;

    @Override
    public AuthVo login(LoginBO loginBO) {
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
            throw new UserNotExistException("用户不存在或已被删除");

        return user;
    }

    /**
     * 校验用户状态
     */
    private void validateUser(SysUser user) {
        if (user.isDelFlag())
            throw new UserNotExistException("用户已被删除");

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
            throw new LoginPasswordWrongException("密码错误");
        }
    }

    /**
     * 构建 LoginUser（不再设置 platformType，由 login 参数传入）
     */
    private LoginUser buildLoginUser(SysUser user) {
        // TODO: 从数据库查询用户权限和角色
        Set<String> permissions = getUserPermissions(user.getUserId());
        Set<String> groups = getUserGroups(user.getUserId());
        Set<Long> roles = getUserRoles(user.getUserId());
        Set<Long> userDept = getUserDept(user.getUserId());

        // 判断是否为超级管理员（userId=1 或拥有 * 权限）
        boolean isSuperAdmin = user.getUserId() == 1L || permissions.contains("*");

        return LoginUser.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .depts(userDept)
                .superAdmin(isSuperAdmin)
                // platformType 不在这里设置，由 authService.login() 参数传入
                .permissions(permissions)
                .groups(groups)
                .roles(roles)
                .build();
    }

    /**
     * 获取用户权限列表
     * TODO: 实现从数据库查询
     */
    private Set<String> getUserPermissions(Long userId) {
        // 超级管理员拥有所有权限
        if (userId == 1L) {
            Set<String> permissions = new HashSet<>();
            permissions.add("*");
            return permissions;
        }
        // TODO: 从数据库查询用户权限（合并权限组的 permissions）
        // 计算用户最终权限合集

        // 权限合并
        // 1 从sys_user_perm获取所有用户单独的权限
        // 2 用户sys_user_group直绑的权限组
        // 3 sys_role_group角色绑定的权限组
        // 4 sys_dept_group部门绑定的权限组
        // 通过这些权限组获取所有的权限，
        // 最后得到一个权限的集合
        // return sysPermissionMapper.selectPermissionsByUserId(userId);
        return new HashSet<>();
    }

    /**
     * 获取用户权限组列表
     * 合并来源：用户直绑 + 角色绑定 + 部门绑定
     *
     * @param userId 用户ID
     * @return 权限组 code 集合
     */
    private Set<String> getUserGroups(Long userId) {
        // 超级管理员
        if (userId == 1L) {
            Set<String> groups = new HashSet<>();
            groups.add("super_admin");
            return groups;
        }

        Set<Long> groupIds = new HashSet<>();
        Date now = new Date();

        // 1. 用户直接绑定的权限组
        sysUserGroupMapper.selectList(
                new LambdaQueryWrapper<SysUserGroup>()
                        .select(SysUserGroup::getGroupId)
                        .eq(SysUserGroup::getUserId, userId)
                        .and(w -> w.isNull(SysUserGroup::getExpireTime)
                                .or().gt(SysUserGroup::getExpireTime, now))
        ).forEach(ug -> groupIds.add(ug.getGroupId()));

        // 2. 用户角色绑定的权限组
        Set<Long> roleIds = getUserRoles(userId);
        if (!roleIds.isEmpty()) {
            sysRoleGroupMapper.selectList(
                    new LambdaQueryWrapper<SysRoleGroup>()
                            .select(SysRoleGroup::getGroupId)
                            .in(SysRoleGroup::getRoleId, roleIds)
                            .and(w -> w.isNull(SysRoleGroup::getExpireTime)
                                    .or().gt(SysRoleGroup::getExpireTime, now))
            ).forEach(rg -> groupIds.add(rg.getGroupId()));
        }

        // 3. 用户部门绑定的权限组
        Set<Long> deptIds = getUserDept(userId);
        if (!deptIds.isEmpty()) {
            sysDeptGroupMapper.selectList(
                    new LambdaQueryWrapper<SysDeptGroup>()
                            .select(SysDeptGroup::getGroupId)
                            .in(SysDeptGroup::getDeptId, deptIds)
                            .and(w -> w.isNull(SysDeptGroup::getExpireTime)
                                    .or().gt(SysDeptGroup::getExpireTime, now))
            ).forEach(dg -> groupIds.add(dg.getGroupId()));
        }

        // 根据 groupId 查询权限组 code
        if (groupIds.isEmpty()) {
            return new HashSet<>();
        }

        return sysPermGroupMapper.selectList(
                new LambdaQueryWrapper<SysPermGroup>()
                        .select(SysPermGroup::getCode)
                        .in(SysPermGroup::getId, groupIds)
                        .eq(SysPermGroup::getStatus, 1)
        ).stream()
                .map(SysPermGroup::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    private Set<Long> getUserRoles(Long userId) {
        return sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .select(SysUserRole::getRoleId)
                        .eq(SysUserRole::getUserId, userId)
        ).stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户部门 ID 列表
     * @param userId 用户 ID
     * @return 返回用户部门 ID 列表
     */
    private Set<Long> getUserDept(Long userId) {
        return sysUserDeptMapper.selectList(
                new LambdaQueryWrapper<SysUserDept>()
                        .select(SysUserDept::getDeptId)
                        .eq(SysUserDept::getUserId, userId)
        ).stream()
                .map(SysUserDept::getDeptId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户信息
     * @param loginUser 用户会话信息
     * @return 返回用户信息
     */
    public UserInfoVO getUserInfo(LoginUser loginUser){
        Long userId = loginUser.getUserId();

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::isDelFlag, false)
                .eq(SysUser::getUserId, userId)
                .select(SysUser::getAvatar, SysUser::getEmail);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);
        String avatar = sysUser.getAvatar();
        if (avatar == null || avatar.isBlank()) {
            // 未设置头像返回默认头像
            avatar = "/default-avatar.png";
        }

        Set<String> userPermissions = getUserPermissions(userId);
        Set<Long> userRoles = getUserRoles(userId);
        Set<Long> userDept = getUserDept(userId);

        return UserInfoVO.builder()
                .userId(userId)
                .userName(loginUser.getUserName())
                .nickName(loginUser.getNickName())
                .email(sysUser.getEmail())
                .avatar(avatar)
                .permissions(userPermissions)
                .roles(userRoles)
                .depts(userDept)
                .build();
    }

}
