package com.junoyi.framework.permission.helper;

import com.junoyi.framework.permission.enums.Logical;
import com.junoyi.framework.permission.matcher.PermissionMatcher;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 权限工具类
 * <p>
 * 提供静态方法用于权限校验，简化业务代码中的权限判断
 * <p>
 * 使用示例：
 * <pre>
 * // 判断是否有权限
 * if (PermissionHelper.hasPermission("system.user.delete")) {
 *     // 执行删除操作
 * }
 *
 * // 判断是否有任意一个权限
 * if (PermissionHelper.hasAnyPermission("system.user.view", "system.admin")) {
 *     // 执行查看操作
 * }
 * </pre>
 *
 * @author Fan
 */
public class PermissionHelper {

    /**
     * 权限信息提供者（由 Security 模块注入）
     */
    private static Supplier<Set<String>> permissionsSupplier;
    private static Supplier<Set<String>> groupsSupplier;
    private static Supplier<Long> userIdSupplier;
    private static Supplier<Long> deptIdSupplier;
    private static Supplier<Boolean> superAdminSupplier;

    private PermissionHelper() {
    }

    /**
     * 初始化权限信息提供者
     * <p>
     * 由 Security 模块在启动时调用，注入获取权限信息的方法
     */
    public static void init(Supplier<Set<String>> permissions,
                            Supplier<Set<String>> groups,
                            Supplier<Long> userId,
                            Supplier<Long> deptId,
                            Supplier<Boolean> superAdmin) {
        permissionsSupplier = permissions;
        groupsSupplier = groups;
        userIdSupplier = userId;
        deptIdSupplier = deptId;
        superAdminSupplier = superAdmin;
    }

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限节点
     * @return true 有权限，false 无权限
     */
    public static boolean hasPermission(String permission) {
        // 超级管理员拥有所有权限
        if (isSuperAdmin()) {
            return true;
        }
        Set<String> userPermissions = getCurrentUserPermissions();
        return PermissionMatcher.hasPermission(userPermissions, permission);
    }

    /**
     * 判断当前用户是否拥有所有指定权限（AND 逻辑）
     *
     * @param permissions 权限节点数组
     * @return true 拥有所有权限，false 缺少权限
     */
    public static boolean hasAllPermissions(String... permissions) {
        if (isSuperAdmin()) {
            return true;
        }
        Set<String> userPermissions = getCurrentUserPermissions();
        return PermissionMatcher.hasAllPermissions(userPermissions, permissions);
    }

    /**
     * 判断当前用户是否拥有任意一个指定权限（OR 逻辑）
     *
     * @param permissions 权限节点数组
     * @return true 拥有任意一个权限，false 无任何权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        if (isSuperAdmin()) {
            return true;
        }
        Set<String> userPermissions = getCurrentUserPermissions();
        return PermissionMatcher.hasAnyPermission(userPermissions, permissions);
    }

    /**
     * 根据逻辑类型判断权限
     *
     * @param permissions 权限节点数组
     * @param logical     逻辑类型
     * @return true 满足条件，false 不满足
     */
    public static boolean hasPermissions(String[] permissions, Logical logical) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        if (logical == Logical.AND) {
            return hasAllPermissions(permissions);
        } else {
            return hasAnyPermission(permissions);
        }
    }

    /**
     * 判断当前用户是否为超级管理员
     *
     * @return true 是超级管理员，false 不是
     */
    public static boolean isSuperAdmin() {
        if (superAdminSupplier == null) {
            return false;
        }
        Boolean result = superAdminSupplier.get();
        return result != null && result;
    }

    /**
     * 判断当前用户是否在指定权限组中
     *
     * @param groupCode 权限组编码
     * @return true 在权限组中，false 不在
     */
    public static boolean inGroup(String groupCode) {
        Set<String> groups = getCurrentUserGroups();
        return groups != null && groups.contains(groupCode);
    }

    /**
     * 判断当前用户是否在任意一个指定权限组中
     *
     * @param groupCodes 权限组编码数组
     * @return true 在任意一个权限组中，false 不在
     */
    public static boolean inAnyGroup(String... groupCodes) {
        if (groupCodes == null || groupCodes.length == 0) {
            return true;
        }
        for (String groupCode : groupCodes) {
            if (inGroup(groupCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户的权限集合
     *
     * @return 权限集合，如果未登录返回空集合
     */
    public static Set<String> getCurrentUserPermissions() {
        if (permissionsSupplier == null) {
            return Set.of();
        }
        Set<String> permissions = permissionsSupplier.get();
        return permissions != null ? permissions : Set.of();
    }

    /**
     * 获取当前用户的权限组集合
     *
     * @return 权限组集合，如果未登录返回空集合
     */
    public static Set<String> getCurrentUserGroups() {
        if (groupsSupplier == null) {
            return Set.of();
        }
        Set<String> groups = groupsSupplier.get();
        return groups != null ? groups : Set.of();
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID，如果未登录返回 null
     */
    public static Long getCurrentUserId() {
        return userIdSupplier != null ? userIdSupplier.get() : null;
    }

    /**
     * 获取当前用户部门ID
     *
     * @return 部门ID，如果未登录返回 null
     */
    public static Long getCurrentDeptId() {
        return deptIdSupplier != null ? deptIdSupplier.get() : null;
    }

    /**
     * 检查指定用户是否拥有权限
     *
     * @param userPermissions 用户权限集合
     * @param permission      需要的权限
     * @return true 有权限，false 无权限
     */
    public static boolean checkPermission(Collection<String> userPermissions, String permission) {
        return PermissionMatcher.hasPermission(userPermissions, permission);
    }
}
