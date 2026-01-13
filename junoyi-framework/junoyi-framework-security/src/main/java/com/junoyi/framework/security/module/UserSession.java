package com.junoyi.framework.security.module;

import com.junoyi.framework.security.enums.PlatformType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 用户会话实体类
 * 存储在 Redis 中的完整会话信息
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会话唯一标识（即 tokenId）
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 登录平台
     */
    private PlatformType platformType;


    /**
     * 用户角色ID列表
     */
    private Set<Long> roles;

    /**
     * 用户权限节点集合
     */
    private Set<String> permissions;

    /**
     * 用户权限组编码集合
     */
    private Set<String> groups;

    /**
     * 用户部门列表
     */
    private Set<Long> depts;

    /**
     * 数据范围类型（取用户所有角色中权限最大的）
     * 值：1-全部数据，2-本部门数据，3-本部门及下级数据，4-仅本人数据
     */
    private String dataScope;

    /**
     * 可访问的部门ID集合（用于数据范围过滤）
     * 包含用户所属部门及其下级部门（根据 dataScope 计算）
     */
    private Set<Long> accessibleDeptIds;

    /**
     * 是否为超级管理员
     */
    private boolean superAdmin;

    /**
     * 登录IP地址
     */
    private String loginIp;

    /**
     * IP所在地区
     */
    private String ipRegion;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 最后访问时间
     */
    private Date lastAccessTime;

    /**
     * 用户代理（浏览器信息）
     */
    private String userAgent;

    /**
     * 登录设备类型
     */
    private String deviceType;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * AccessToken 过期时间
     */
    private Long accessExpireTime;

    /**
     * RefreshToken 过期时间
     */
    private Long refreshExpireTime;
}
