package com.junoyi.system.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 系统菜单排序请求对象
 * 支持两种格式：
 * 1. 直接传列表: [{ "id": 1, "parentId": 0, "sort": 1 }, ...]
 * 2. 包装对象: { "items": [{ "id": 1, "parentId": 0, "sort": 1 }, ...] }
 *
 * @author Fan
 */
@Data
public class SysMenuSortDTO {

    /**
     * 排序项列表（用于包装格式）
     */
    private List<SysMenuSortItem> items;
}
