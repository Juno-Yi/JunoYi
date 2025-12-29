package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 权限组实体
 *
 * @author Fan
 */
@Data
@TableName("sys_perm_group")
public class SysPermGroup {

    @TableId
    private Long id;

    /**
     * 权限组编码
     */
    private String code;

    /**
     * 权限组名称
     */
    private String name;

    /**
     * 父级权限组ID，用于构建权限组层级关系
     */
    private Long parentId;

    /**
     * 优先级，用于排序显示
     */
    private int priority;

    /**
     * 权限组描述信息
     */
    private String description;

    /**
     * 状态标识，用于控制权限组的启用/禁用状态
     */
    private int status;

    /**
     * 权限集合（非数据库字段，需要单独查询）
     */
    @TableField(exist = false)
    private Set<String> permissions;

    /**
     * 创建人标识
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人标识
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
