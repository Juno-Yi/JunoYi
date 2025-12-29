package com.junoyi.system.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 用户独立权限数据实体对象
 * 用于存储用户权限相关信息，包括权限标识、过期时间等
 *
 * @author Fan
 */
@Data
public class SysUserPerm {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID，关联用户表
     */
    private Long userId;

    /**
     * 权限字符串，表示具体的权限标识
     */
    private String permission;

    /**
     * 权限过期时间，超过此时间权限失效
     */
    private Date expireTime;

    /**
     * 记录创建时间
     */
    private Date createTime;
}
