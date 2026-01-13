package com.junoyi.framework.security.module;

import com.junoyi.framework.security.enums.PlatformType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 登录用户信息类
 * 用于封装用户登录后的基本信息、权限、角色等数据
 *
 * @author Fan
 */
@Data
@Builder
public class LoginUser {

    /**
     * Token ID（用于关联 AccessToken 和 RefreshToken， 这里tokenId即为sessionId）
     */
    private String tokenId;

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
     * 用户角色列表
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
     * 登录时间
     */
    private Date loginTime;
}
