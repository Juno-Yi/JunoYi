package com.junoyi.system.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 路由项 VO
 *
 * @author Fan
 */
@Data
public class RouterItemVo {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由元信息
     */
    private RouterMetaVo meta;

    /**
     * 子路由
     */
    private List<RouterItemVo> children;
}
