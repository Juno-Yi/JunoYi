package com.junoyi.framework.security.utils;

import com.junoyi.framework.security.module.LoginUser;

/**
 * 安全工具类
 * 用于管理当前请求线程的登录用户信息
 *
 * @author Fan
 */
public class SecurityUtils {

    /**
     * 使用 ThreadLocal 存储当前登录用户信息
     * 确保线程安全，每个请求线程独立存储
     */
    private static final ThreadLocal<LoginUser> LOGIN_USER_HOLDER = new ThreadLocal<>();

    /**
     * 私有构造函数，防止实例化
     */
    private SecurityUtils() {
    }

    /**
     * 设置当前登录用户信息
     *
     * @param loginUser 登录用户对象
     */
    public static void setLoginUser(LoginUser loginUser) {
        LOGIN_USER_HOLDER.set(loginUser);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 登录用户对象，如果未登录则返回 null
     */
    public static LoginUser getLoginUser() {
        return LOGIN_USER_HOLDER.get();
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，如果未登录则返回 null
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，如果未登录则返回 null
     */
    public static String getUserName() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserName() : null;
    }

    /**
     * 获取当前登录用户昵称
     *
     * @return 用户昵称，如果未登录则返回 null
     */
    public static String getNickName() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getNickName() : null;
    }

    /**
     * 判断当前是否已登录
     *
     * @return true=已登录，false=未登录
     */
    public static boolean isLogin() {
        return getLoginUser() != null;
    }

    /**
     * 清除当前登录用户信息
     * 通常在请求结束时调用，防止内存泄漏
     */
    public static void clearLoginUser() {
        LOGIN_USER_HOLDER.remove();
    }

    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permission 权限标识
     * @return true=拥有权限，false=没有权限
     */
    public static boolean hasPermission(String permission) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getPermissions() == null) {
            return false;
        }
        return loginUser.getPermissions().contains(permission);
    }

    /**
     * 检查当前用户是否拥有指定角色
     *
     * @param roleId 角色ID
     * @return true=拥有角色，false=没有角色
     */
    public static boolean hasRole(Long roleId) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getRoles() == null) {
            return false;
        }
        return loginUser.getRoles().contains(roleId);
    }
}
