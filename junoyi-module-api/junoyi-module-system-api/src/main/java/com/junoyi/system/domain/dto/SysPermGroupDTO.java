package com.junoyi.system.domain.dto;

import lombok.Data;

import java.util.Set;

/**
 * 权限组传输数据
 *
 * @author Fan
 */
@Data
public class SysPermGroupDTO {

    /**
     * 权限组ID（修改时必填）
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
}
