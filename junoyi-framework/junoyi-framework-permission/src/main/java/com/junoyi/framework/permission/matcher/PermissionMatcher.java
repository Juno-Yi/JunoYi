package com.junoyi.framework.permission.matcher;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限匹配器
 * <p>
 * 支持权限通配符匹配和黑名单机制
 * <p>
 * 匹配规则：
 * <ul>
 *   <li>精确匹配：system.user.delete</li>
 *   <li>单级通配：system.user.* 匹配 system.user.delete</li>
 *   <li>多级通配：system.** 匹配 system.user.delete.field</li>
 *   <li>全局通配：* 匹配所有</li>
 *   <li>黑名单：-system.user.delete 禁止该权限（优先级最高）</li>
 * </ul>
 *
 * @author Fan
 */
public class PermissionMatcher {

    /**
     * 黑名单前缀
     */
    private static final String DENY_PREFIX = "-";

    /**
     * 单级通配符
     */
    private static final String SINGLE_WILDCARD = "*";

    /**
     * 多级通配符
     */
    private static final String MULTI_WILDCARD = "**";

    /**
     * 节点分隔符
     */
    private static final String SEPARATOR = ".";

    /**
     * 检查用户是否拥有指定权限
     *
     * @param userPermissions    用户拥有的权限节点集合
     * @param requiredPermission 需要的权限节点
     * @return true 有权限，false 无权限
     */
    public static boolean hasPermission(Collection<String> userPermissions, String requiredPermission) {
        if (userPermissions == null || userPermissions.isEmpty() || requiredPermission == null) {
            return false;
        }

        // 检查黑名单（优先级最高）
        String denyPermission = DENY_PREFIX + requiredPermission;
        if (userPermissions.contains(denyPermission)) {
            return false;
        }

        // 检查通配符黑名单
        for (String permission : userPermissions) {
            if (permission.startsWith(DENY_PREFIX)) {
                String denyPattern = permission.substring(1);
                if (matchWildcard(denyPattern, requiredPermission)) {
                    return false;
                }
            }
        }

        // 精确匹配
        if (userPermissions.contains(requiredPermission)) {
            return true;
        }

        // 全局通配符
        if (userPermissions.contains(SINGLE_WILDCARD) || userPermissions.contains(MULTI_WILDCARD)) {
            return true;
        }

        // 通配符匹配
        for (String pattern : userPermissions) {
            // 跳过黑名单
            if (pattern.startsWith(DENY_PREFIX)) {
                continue;
            }
            if (matchWildcard(pattern, requiredPermission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查用户是否拥有所有指定权限（AND 逻辑）
     *
     * @param userPermissions     用户拥有的权限节点集合
     * @param requiredPermissions 需要的权限节点数组
     * @return true 拥有所有权限，false 缺少权限
     */
    public static boolean hasAllPermissions(Collection<String> userPermissions, String... requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            return true;
        }
        for (String permission : requiredPermissions) {
            if (!hasPermission(userPermissions, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查用户是否拥有任意一个指定权限（OR 逻辑）
     *
     * @param userPermissions     用户拥有的权限节点集合
     * @param requiredPermissions 需要的权限节点数组
     * @return true 拥有任意一个权限，false 无任何权限
     */
    public static boolean hasAnyPermission(Collection<String> userPermissions, String... requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            return true;
        }
        for (String permission : requiredPermissions) {
            if (hasPermission(userPermissions, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通配符匹配
     * <p>
     * 支持：
     * <ul>
     *   <li>* 匹配单级：system.user.* 匹配 system.user.delete</li>
     *   <li>** 匹配多级：system.** 匹配 system.user.delete.field</li>
     * </ul>
     *
     * @param pattern    权限模式（可包含通配符）
     * @param permission 待匹配的权限节点
     * @return true 匹配成功，false 匹配失败
     */
    public static boolean matchWildcard(String pattern, String permission) {
        if (pattern == null || permission == null) {
            return false;
        }

        // 完全相等
        if (pattern.equals(permission)) {
            return true;
        }

        // 全局通配符
        if (SINGLE_WILDCARD.equals(pattern) || MULTI_WILDCARD.equals(pattern)) {
            return true;
        }

        String[] patternParts = pattern.split("\\.");
        String[] permissionParts = permission.split("\\.");

        return matchParts(patternParts, 0, permissionParts, 0);
    }

    /**
     * 递归匹配权限节点各部分
     */
    private static boolean matchParts(String[] patternParts, int patternIndex,
                                      String[] permissionParts, int permissionIndex) {
        // 模式已匹配完
        if (patternIndex >= patternParts.length) {
            return permissionIndex >= permissionParts.length;
        }

        String patternPart = patternParts[patternIndex];

        // 多级通配符 **
        if (MULTI_WILDCARD.equals(patternPart)) {
            // ** 在末尾，匹配剩余所有
            if (patternIndex == patternParts.length - 1) {
                return true;
            }
            // ** 不在末尾，尝试匹配后续
            for (int i = permissionIndex; i <= permissionParts.length; i++) {
                if (matchParts(patternParts, patternIndex + 1, permissionParts, i)) {
                    return true;
                }
            }
            return false;
        }

        // 权限已匹配完但模式还有
        if (permissionIndex >= permissionParts.length) {
            return false;
        }

        String permissionPart = permissionParts[permissionIndex];

        // 单级通配符 * 或精确匹配
        if (SINGLE_WILDCARD.equals(patternPart) || patternPart.equals(permissionPart)) {
            return matchParts(patternParts, patternIndex + 1, permissionParts, permissionIndex + 1);
        }

        return false;
    }

    /**
     * 从权限集合中提取黑名单权限
     *
     * @param permissions 权限集合
     * @return 黑名单权限集合（不含前缀）
     */
    public static Set<String> extractDenyPermissions(Collection<String> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .filter(p -> p != null && p.startsWith(DENY_PREFIX))
                .map(p -> p.substring(1))
                .collect(Collectors.toSet());
    }

    /**
     * 从权限集合中提取允许的权限（排除黑名单）
     *
     * @param permissions 权限集合
     * @return 允许的权限集合
     */
    public static Set<String> extractAllowPermissions(Collection<String> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .filter(p -> p != null && !p.startsWith(DENY_PREFIX))
                .collect(Collectors.toSet());
    }
}
