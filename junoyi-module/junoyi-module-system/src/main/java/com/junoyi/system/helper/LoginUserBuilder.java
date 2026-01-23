package com.junoyi.system.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.datasource.datascope.DataScopeType;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.system.domain.po.*;
import com.junoyi.system.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * LoginUser 构建器
 * 用于构建包含完整权限信息的 LoginUser 对象
 *
 * @author Fan
 */
@Component
@RequiredArgsConstructor
public class LoginUserBuilder {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(LoginUserBuilder.class);

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysUserGroupMapper sysUserGroupMapper;
    private final SysRoleGroupMapper sysRoleGroupMapper;
    private final SysDeptGroupMapper sysDeptGroupMapper;
    private final SysPermGroupMapper sysPermGroupMapper;
    private final SysUserPermMapper sysUserPermMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysDeptMapper sysDeptMapper;

    /**
     * 构建 LoginUser（包含完整的权限、角色、部门信息）
     *
     * @param user 用户实体
     * @return LoginUser
     */
    public LoginUser build(SysUser user) {
        Long userId = user.getUserId();

        // 一次性查询所有权限相关数据
        UserPermissionContext ctx = loadUserPermissionContext(userId);

        // 判断是否为超级管理员（userId=1 或拥有 * 权限）
        boolean isSuperAdmin = userId == 1L || ctx.permissions.contains("*");

        return LoginUser.builder()
                .userId(userId)
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .depts(ctx.deptIds)
                .dataScope(ctx.dataScope)
                .accessibleDeptIds(ctx.accessibleDeptIds)
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
        Set<Long> roleIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();
        Set<String> groupCodes = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        String dataScope;
        Set<Long> accessibleDeptIds = new HashSet<>();
    }

    /**
     * 一次性加载用户权限上下文
     */
    private UserPermissionContext loadUserPermissionContext(Long userId) {
        log.debug("[权限加载] 开始加载用户权限上下文, userId={}", userId);
        long startTime = System.currentTimeMillis();

        UserPermissionContext ctx = new UserPermissionContext();

        // 超级管理员特殊处理
        if (userId == 1L) {
            ctx.permissions.add("*");
            ctx.groupCodes.add("super_admin");
            ctx.dataScope = DataScopeType.ALL.getValue();
            log.debug("[权限加载] 超级管理员, 直接返回");
            return ctx;
        }

        Date now = new Date();

        // 查询用户角色
        ctx.roleIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .select(SysUserRole::getRoleId)
                        .eq(SysUserRole::getUserId, userId)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
        log.debug("[权限加载] 用户角色: {}", ctx.roleIds);

        // 查询用户部门
        ctx.deptIds = sysUserDeptMapper.selectList(
                new LambdaQueryWrapper<SysUserDept>()
                        .select(SysUserDept::getDeptId)
                        .eq(SysUserDept::getUserId, userId)
        ).stream().map(SysUserDept::getDeptId).collect(Collectors.toSet());
        log.debug("[权限加载] 用户部门: {}", ctx.deptIds);

        // 计算数据范围
        calculateDataScope(ctx);

        // 收集所有权限组ID（用户直绑 + 角色绑定 + 部门绑定）
        sysUserGroupMapper.selectList(
                new LambdaQueryWrapper<SysUserGroup>()
                        .select(SysUserGroup::getGroupId)
                        .eq(SysUserGroup::getUserId, userId)
                        .and(w -> w.isNull(SysUserGroup::getExpireTime).or().gt(SysUserGroup::getExpireTime, now))
        ).forEach(ug -> ctx.groupIds.add(ug.getGroupId()));

        if (!ctx.roleIds.isEmpty()) {
            sysRoleGroupMapper.selectList(
                    new LambdaQueryWrapper<SysRoleGroup>()
                            .select(SysRoleGroup::getGroupId)
                            .in(SysRoleGroup::getRoleId, ctx.roleIds)
                            .and(w -> w.isNull(SysRoleGroup::getExpireTime).or().gt(SysRoleGroup::getExpireTime, now))
            ).forEach(rg -> ctx.groupIds.add(rg.getGroupId()));
        }

        if (!ctx.deptIds.isEmpty()) {
            sysDeptGroupMapper.selectList(
                    new LambdaQueryWrapper<SysDeptGroup>()
                            .select(SysDeptGroup::getGroupId)
                            .in(SysDeptGroup::getDeptId, ctx.deptIds)
                            .and(w -> w.isNull(SysDeptGroup::getExpireTime).or().gt(SysDeptGroup::getExpireTime, now))
            ).forEach(dg -> ctx.groupIds.add(dg.getGroupId()));
        }
        log.debug("[权限加载] 合并后权限组ID: {}", ctx.groupIds);

        // 一次性查询权限组
        if (!ctx.groupIds.isEmpty()) {
            List<SysPermGroup> groups = sysPermGroupMapper.selectList(
                    new LambdaQueryWrapper<SysPermGroup>()
                            .in(SysPermGroup::getId, ctx.groupIds)
                            .eq(SysPermGroup::getStatus, 1)
            );
            for (SysPermGroup group : groups) {
                ctx.groupCodes.add(group.getGroupCode());
                if (group.getPermissions() != null) {
                    ctx.permissions.addAll(group.getPermissions());
                }
            }
        }

        // 查询用户独立权限
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
     * 计算用户数据范围
     */
    private void calculateDataScope(UserPermissionContext ctx) {
        DataScopeType maxScope = DataScopeType.SELF;

        if (!ctx.roleIds.isEmpty()) {
            List<SysRole> roles = sysRoleMapper.selectList(
                    new LambdaQueryWrapper<SysRole>()
                            .select(SysRole::getDataScope)
                            .in(SysRole::getId, ctx.roleIds)
                            .eq(SysRole::getStatus, 0)
                            .eq(SysRole::isDelFlag, false)
            );

            for (SysRole role : roles) {
                if (role.getDataScope() != null) {
                    DataScopeType roleScope = DataScopeType.fromValue(role.getDataScope());
                    maxScope = DataScopeType.max(maxScope, roleScope);
                }
            }
        }

        ctx.dataScope = maxScope != null ? maxScope.getValue() : DataScopeType.SELF.getValue();
        log.debug("[权限加载] 数据范围: {} ({})", ctx.dataScope, maxScope != null ? maxScope.getDesc() : "仅本人数据");

        ctx.accessibleDeptIds = new HashSet<>();
        if (maxScope != null) {
            switch (maxScope) {
                case ALL:
                    break;
                case DEPT_AND_CHILD:
                    for (Long deptId : ctx.deptIds) {
                        ctx.accessibleDeptIds.add(deptId);
                        ctx.accessibleDeptIds.addAll(getChildDeptIds(deptId));
                    }
                    break;
                case DEPT:
                    ctx.accessibleDeptIds.addAll(ctx.deptIds);
                    break;
                case SELF:
                    break;
            }
        }
        log.debug("[权限加载] 可访问部门: {}", ctx.accessibleDeptIds);
    }

    /**
     * 递归获取部门的所有下级部门ID
     */
    private Set<Long> getChildDeptIds(Long parentId) {
        Set<Long> childIds = new HashSet<>();

        List<SysDept> children = sysDeptMapper.selectList(
                new LambdaQueryWrapper<SysDept>()
                        .select(SysDept::getId)
                        .eq(SysDept::getParentId, parentId)
                        .eq(SysDept::isDelFlag, false)
        );

        for (SysDept child : children) {
            childIds.add(child.getId());
            childIds.addAll(getChildDeptIds(child.getId()));
        }

        return childIds;
    }
}
