package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.CacheKeyQueryDTO;
import com.junoyi.system.domain.vo.CacheKeyDetailVO;
import com.junoyi.system.domain.vo.CacheKeyVO;
import com.junoyi.system.domain.vo.RedisInfoVO;

import java.util.List;

/**
 * 系统缓存服务接口
 *
 * @author Fan
 */
public interface ISysCacheService {

    /**
     * 获取 Redis 服务器信息
     *
     * @return Redis 信息
     */
    RedisInfoVO getRedisInfo();

    /**
     * 查询缓存键列表（分页）
     *
     * @param query 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<CacheKeyVO> getCacheKeyList(CacheKeyQueryDTO query, PageQuery pageQuery);

    /**
     * 获取缓存键详情（包含值）
     *
     * @param key 键名
     * @return 缓存详情
     */
    CacheKeyDetailVO getCacheKeyDetail(String key);

    /**
     * 删除指定缓存
     *
     * @param key 键名
     * @return 是否删除成功
     */
    boolean deleteCacheKey(String key);

    /**
     * 批量删除缓存
     *
     * @param keys 键名列表
     */
    void deleteCacheBatch(List<String> keys);
}
