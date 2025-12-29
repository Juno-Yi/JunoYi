package com.junoyi.system.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 系统用户与权限组关联数据实体
 * 用于维护系统中用户与权限组之间的关联关系，包含关联ID、用户ID、权限组ID、过期时间等信息
 *
 * @author Fan
 */
@Data
public class SysUserGroup {

    /**
     * 关联记录的唯一标识ID
     */
    private Long id;

    /**
     * 用户ID，关联系统用户表
     */
    private Long userId;

    /**
     * 权限组ID，关联权限组表
     */
    private Long groupId;

    /**
     * 关联关系过期时间，超过此时间关联关系失效
     */
    private Date expireTime;

    /**
     * 记录创建时间
     */
    private Date createTime;
}
