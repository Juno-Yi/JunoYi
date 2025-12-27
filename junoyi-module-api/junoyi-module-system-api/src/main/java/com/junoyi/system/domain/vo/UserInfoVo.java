package com.junoyi.system.domain.vo;


import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * 用户信息 vo 数据实体
 * 用于响应给前端的用户信息数据传输实体
 *
 * @author Fan
 */
@Data
@Builder
public class UserInfoVo {

    /**
     * 用户 ID
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
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 角色
     */
    private Set<Long> roles;

    /**
     * 部门
     */
    private Set<Long> depts;

}