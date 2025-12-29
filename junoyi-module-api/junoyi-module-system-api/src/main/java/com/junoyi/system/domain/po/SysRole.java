package com.junoyi.system.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 系统角色实体类
 * 用于封装系统角色相关的信息，包括角色名称、权限标识、状态等属性
 */
@Data
public class SysRole {

    /**
     * 角色主键ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限标识
     */
    private String roleKey;

    /**
     * 角色排序
     */
    private int sort;

    /**
     * 数据权限范围
     */
    private String  dataScope;

    /**
     * 角色状态（0-正常，1-禁用）
     */
    private int status;

    /**
     * 删除标志（true-已删除，false-未删除）
     */
    private boolean delFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注信息
     */
    private String remark;
}
