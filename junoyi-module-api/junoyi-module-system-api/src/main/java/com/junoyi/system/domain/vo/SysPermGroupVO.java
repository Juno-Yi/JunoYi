package com.junoyi.system.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 权限组响应数据
 *
 * @author Fan
 */
@Data
public class SysPermGroupVO {

    /**
     * 权限组ID
     */
    private Long id;

    /**
     * 权限组编码
     */
    private String groupCode;

    /**
     * 权限组名称
     */
    private String groupName;

    /**
     * 父级权限组ID
     */
    private Long parentId;

    /**
     * 父级权限组名称
     */
    private String parentName;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 权限集合
     */
    private Set<String> permissions;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
