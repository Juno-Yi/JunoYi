package com.junoyi.system.domain.po;

import lombok.Data;

/**
 * 用户角色关联数据实体
 * 用于表示用户与角色之间的关联关系，包含关联ID、用户ID和角色ID三个字段
 *
 * @author Fan
 */
@Data
public class SysUserRole {

    /**
     * 关联记录的唯一标识ID
     */
    private Long id;

    /**
     * 用户ID，关联到系统用户表
     */
    private Long userId;

    /**
     * 角色ID，关联到系统角色表
     */
    private Long roleId;
}
