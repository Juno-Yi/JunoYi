package com.junoyi.framework.permission.handler;


/**
 * 权限加载器接口
 * <p>
 * 由业务模块实现，用于加载用户的权限信息
 * <p>
 * 框架层不依赖业务模块，通过此接口解耦
 *
 * @author Fan
 */
public interface PermissionLoader {


    /**
     * 刷新用户权限缓存
     *
     * @param userId 用户ID
     */
    default void refreshPermissionCache(Long userId) {
        // 默认空实现
    }

    /**
     * 清除用户权限缓存
     *
     * @param userId 用户ID
     */
    default void clearPermissionCache(Long userId) {
        // 默认空实现
    }

    /**
     * 清除所有用户权限缓存
     */
    default void clearAllPermissionCache() {
        // 默认空实现
    }
}
