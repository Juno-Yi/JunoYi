package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 菜单排序项
 *
 * @author Fan
 */
@Data
public class SysMenuSortItem {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父级菜单ID
     */
    private Long parentId;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 路由路径（可选）
     */
    private String path;

    /**
     * 组件路径（可选）
     */
    private String component;
}
