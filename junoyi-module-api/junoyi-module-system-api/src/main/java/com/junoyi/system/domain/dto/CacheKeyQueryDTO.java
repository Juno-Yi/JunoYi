package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 缓存键列表查询参数
 *
 * @author Fan
 */
@Data
public class CacheKeyQueryDTO {

    /**
     * 键名模式（支持通配符）
     */
    private String pattern;

    /**
     * 缓存键列表响应
     */
    private String type;
}