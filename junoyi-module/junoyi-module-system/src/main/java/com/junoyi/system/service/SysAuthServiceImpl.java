package com.junoyi.system.service;

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
import com.junoyi.system.domain.po.SysUserDept;
import com.junoyi.system.domain.po.SysDeptGroup;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.po.SysRoleGroup;
import com.junoyi.system.domain.po.SysUserGroup;
import com.junoyi.system.domain.vo.AuthVO;
import com.junoyi.system.domain.po.SysUserPerm;
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
import com.junoyi.system.mapper.SysUserPermMapper;
import com.junoyi.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysAuthServiceImpl.class);

    private final AuthHelper authHelper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysUserGroupMapper sysUserGroupMapper;
    private final SysRoleGroupMapper sysRoleGroupMapper;
    private final SysDeptGroupMapper sysDeptGroupMapper;
    private final SysPermGroupMapper sysPermGroupMapper;
    private final SysUserPermMapper sysUserPermMapper;

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

            // 构建 LoginUser
            LoginUser loginUser = buildLoginUser(user);

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
     * 构建 LoginUser（不再设置 platformType，由 login 参数传入）
     */
    private LoginUser buildLoginUser(SysUser user) {
        Long userId = user.getUserId();
        
        // 一次性查询所有权限相关数据，避免重复查询
        UserPermissionContext ctx = loadUserPermissionContext(userId);
        
        // 判断是否为超级管理员（userId=1 或拥有 * 权限）
        boolean isSuperAdmin = userId == 1L || ctx.permissions.contains("*");

        return LoginUser.builder()
                .userId(userId)
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .depts(ctx.deptIds)
                .superAdmin(isSuperAdmin)
                .permissions(ctx.permissions)
                .groups(ctx.groupCodes)
                .roles(ctx.roleIds)
                .build();
    }

    /**
     * 用户权限上下文（避免重复查询）
     */
    private static class UserPermissionContext {
        // 用户角色列表
        Set<Long> roleIds = new HashSet<>();
        // 用户部门列表
        Set<Long> deptIds = new HashSet<>();
        // 用户权限组 ID 列表
        Set<Long> groupIds = new HashSet<>();
        // 用户权限组 code
        Set<String> groupCodes = new HashSet<>();
        // 用户权限集合
        Set<String> permissions = new HashSet<>();
    }

    /**
     * 一次性加载用户权限上下文
     * 优化：角色、部门、权限组只查询一次，权限从权限组中提取
     */
    private UserPermissionContext loadUserPermissionContext(Long userId) {
        log.debug("[权限加载] 开始加载用户权限上下文, userId={}", userId);
        long startTime = System.currentTimeMillis();
        
        UserPermissionContext ctx = new UserPermissionContext();
        
        // 超级管理员特殊处理
        if (userId == 1L) {
            ctx.permissions.add("*");
            ctx.groupCodes.add("super_admin");
            log.debug("[权限加载] 超级管理员, 直接返回");
            return ctx;
        }

        Date now = new Date();

        // 查询用户角色（只查一次）
        ctx.roleIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .select(SysUserRole::getRoleId)
                        .eq(SysUserRole::getUserId, userId)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
        log.debug("[权限加载] 用户角色: {}", ctx.roleIds);

        // 查询用户部门（只查一次）
        ctx.deptIds = sysUserDeptMapper.selectList(
                new LambdaQueryWrapper<SysUserDept>()
                        .select(SysUserDept::getDeptId)
                        .eq(SysUserDept::getUserId, userId)
        ).stream().map(SysUserDept::getDeptId).collect(Collectors.toSet());
        log.debug("[权限加载] 用户部门: {}", ctx.deptIds);

        // 收集所有权限组ID（用户直绑 + 角色绑定 + 部门绑定）
        // 用户直绑权限组
        sysUserGroupMapper.selectList(
                new LambdaQueryWrapper<SysUserGroup>()
                        .select(SysUserGroup::getGroupId)
                        .eq(SysUserGroup::getUserId, userId)
                        .and(w -> w.isNull(SysUserGroup::getExpireTime).or().gt(SysUserGroup::getExpireTime, now))
        ).forEach(ug -> ctx.groupIds.add(ug.getGroupId()));

        // 角色绑定权限组
        if (!ctx.roleIds.isEmpty()) {
            sysRoleGroupMapper.selectList(
                    new LambdaQueryWrapper<SysRoleGroup>()
                            .select(SysRoleGroup::getGroupId)
                            .in(SysRoleGroup::getRoleId, ctx.roleIds)
                            .and(w -> w.isNull(SysRoleGroup::getExpireTime).or().gt(SysRoleGroup::getExpireTime, now))
            ).forEach(rg -> ctx.groupIds.add(rg.getGroupId()));
        }

        // 部门绑定权限组
        if (!ctx.deptIds.isEmpty()) {
            sysDeptGroupMapper.selectList(
                    new LambdaQueryWrapper<SysDeptGroup>()
                            .select(SysDeptGroup::getGroupId)
                            .in(SysDeptGroup::getDeptId, ctx.deptIds)
                            .and(w -> w.isNull(SysDeptGroup::getExpireTime).or().gt(SysDeptGroup::getExpireTime, now))
            ).forEach(dg -> ctx.groupIds.add(dg.getGroupId()));
        }
        log.debug("[权限加载] 合并后权限组ID: {}", ctx.groupIds);

        // 一次性查询权限组（groupCode + permissions）
        if (!ctx.groupIds.isEmpty()) {
            List<SysPermGroup> groups = sysPermGroupMapper.selectList(
                    new LambdaQueryWrapper<SysPermGroup>()
                            .in(SysPermGroup::getId, ctx.groupIds)
                            .eq(SysPermGroup::getStatus, 1)
            );
            for (SysPermGroup group : groups) {
                ctx.groupCodes.add(group.getGroupCode());
                // 权限组的 permissions 字段存储权限列表
                if (group.getPermissions() != null) {
                    ctx.permissions.addAll(group.getPermissions());
                }
            }
        }

        // 查询用户独立权限（sys_user_perm）
        List<SysUserPerm> userPerms = sysUserPermMapper.selectList(
                new LambdaQueryWrapper<SysUserPerm>()
                        .select(SysUserPerm::getPermission)
                        .eq(SysUserPerm::getUserId, userId)
                        .and(w -> w.isNull(SysUserPerm::getExpireTime).or().gt(SysUserPerm::getExpireTime, now))
        );
        for (SysUserPerm perm : userPerms) {
            if (perm.getPermission() != null && !perm.getPermission().isBlank()) {
                ctx.permissions.add(perm.getPermission());
            }
        }
        log.debug("[权限加载] 用户独立权限数量: {}", userPerms.size());

        log.debug("[权限加载] 最终权限组Code: {}", ctx.groupCodes);
        log.debug("[权限加载] 最终权限: {}", ctx.permissions);
        log.debug("[权限加载] 完成, 耗时: {}ms", System.currentTimeMillis() - startTime);
        
        return ctx;
    }

    /**
     * 获取用户权限列表（供外部调用，如 getUserInfo）
     */
    private Set<String> getUserPermissions(Long userId) {
        if (userId == 1L) {
            Set<String> permissions = new HashSet<>();
            permissions.add("*");
            return permissions;
        }
        return loadUserPermissionContext(userId).permissions;
    }

    /**
     * 获取用户权限组列表（供外部调用）
     */
    private Set<String> getUserGroups(Long userId) {
        if (userId == 1L) {
            Set<String> groups = new HashSet<>();
            groups.add("super_admin");
            return groups;
        }
        return loadUserPermissionContext(userId).groupCodes;
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
