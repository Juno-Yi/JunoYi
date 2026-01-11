package com.junoyi.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 缓存键信息 VO
 *
 * @author Fan
 */
@Data
public class CacheKeyVO {

    /**
     * 键名
     */
    private String key;

    /**
     * 值类型：string、list、set、zset、hash
     */
    private String type;

    /**
     * TTL（秒），-1 表示永不过期，-2 表示已过期
     */
    private Long ttl;

    /**
     * 内存占用（字节）
     */
    private Long memoryUsage;

    /**
     * 值大小
     */
    private Long size;
}
