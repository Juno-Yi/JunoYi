package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.system.domain.dto.CacheKeyQueryDTO;
import com.junoyi.system.domain.vo.CacheKeyDetailVO;
import com.junoyi.system.domain.vo.CacheKeyVO;
import com.junoyi.system.domain.vo.RedisInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统缓存服务实现类
 *
 * @author Fan
 */
@Service
public class SysCacheServiceImpl implements ISysCacheService {

    /**
     * 获取 Redis 服务器信息
     *
     * @return Redis 信息
     */
    @Override
    public RedisInfoVO getRedisInfo() {
        Map<String, String> info = RedisUtils.getServerInfo();

        RedisInfoVO vo = new RedisInfoVO();
        // Server 信息
        vo.setVersion(info.get("redis_version"));
        vo.setMode(info.get("redis_mode"));
        vo.setUptimeInSeconds(parseLong(info.get("uptime_in_seconds")));

        // Clients 信息
        vo.setConnectedClients(parseInt(info.get("connected_clients")));

        // Memory 信息
        vo.setUsedMemory(info.get("used_memory"));
        vo.setUsedMemoryHuman(info.get("used_memory_human"));
        vo.setUsedMemoryPeak(info.get("used_memory_peak"));
        vo.setUsedMemoryPeakHuman(info.get("used_memory_peak_human"));

        // Keyspace 信息
        vo.setDbSize(RedisUtils.getDbSize());

        // Stats 信息
        vo.setKeyspaceHits(parseLong(info.get("keyspace_hits")));
        vo.setKeyspaceMisses(parseLong(info.get("keyspace_misses")));
        vo.setInstantaneousOpsPerSec(parseLong(info.get("instantaneous_ops_per_sec")));
        vo.setTotalNetInputBytes(formatBytes(parseLong(info.get("total_net_input_bytes"))));
        vo.setTotalNetOutputBytes(formatBytes(parseLong(info.get("total_net_output_bytes"))));

        // 计算命中率
        long hits = vo.getKeyspaceHits() != null ? vo.getKeyspaceHits() : 0;
        long misses = vo.getKeyspaceMisses() != null ? vo.getKeyspaceMisses() : 0;
        if (hits + misses > 0) {
            double hitRate = (double) hits / (hits + misses) * 100;
            vo.setHitRate(String.format("%.2f%%", hitRate));
        } else {
            vo.setHitRate("0.00%");
        }

        return vo;
    }

    /**
     * 查询缓存键列表（分页）
     *
     * @param query     查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<CacheKeyVO> getCacheKeyList(CacheKeyQueryDTO query, PageQuery pageQuery) {
        // 构建查询模式
        String pattern = StringUtils.hasText(query.getPattern()) ? query.getPattern() : "*";

        // 获取所有匹配的键
        Collection<String> allKeys = RedisUtils.keys(pattern);

        // 按类型过滤
        List<String> filteredKeys = allKeys.stream()
                .filter(key -> {
                    if (!StringUtils.hasText(query.getType())) {
                        return true;
                    }
                    return query.getType().equalsIgnoreCase(RedisUtils.getType(key));
                })
                .collect(Collectors.toList());

        // 总数
        long total = filteredKeys.size();

        // 分页
        int offset = pageQuery.getOffset();
        int size = pageQuery.getSize();
        List<String> pagedKeys = filteredKeys.stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());

        // 转换为 VO
        List<CacheKeyVO> records = pagedKeys.stream()
                .map(this::buildCacheKeyVO)
                .collect(Collectors.toList());

        return PageResult.of(records, total, pageQuery);
    }

    /**
     * 构建缓存键值对象
     * 根据指定的key获取相关的缓存信息并封装成CacheKeyVO对象返回
     *
     * @param key 缓存键值
     * @return CacheKeyVO 包含缓存键的详细信息的对象
     */
    private CacheKeyVO buildCacheKeyVO(String key) {
        CacheKeyVO vo = new CacheKeyVO();
        vo.setKey(key);
        vo.setType(RedisUtils.getType(key));
        vo.setTtl(RedisUtils.getTtl(key));
        vo.setMemoryUsage(RedisUtils.getMemoryUsage(key));
        vo.setSize(RedisUtils.getSize(key));
        return vo;
    }


    /**
     * 获取缓存键详情（包含值）
     *
     * @param key 键名
     * @return 缓存详情
     */
    @Override
    public CacheKeyDetailVO getCacheKeyDetail(String key) {
        if (!RedisUtils.hasKey(key)) {
            return null;
        }
        CacheKeyDetailVO vo = new CacheKeyDetailVO();
        vo.setKey(key);
        vo.setType(RedisUtils.getType(key));
        vo.setTtl(RedisUtils.getTtl(key));
        vo.setMemoryUsage(RedisUtils.getMemoryUsage(key));
        vo.setSize(RedisUtils.getSize(key));
        vo.setValue(RedisUtils.getValue(key));
        return vo;
    }

    /**
     * 删除指定缓存
     *
     * @param key 键名
     * @return 是否删除成功
     */
    @Override
    public boolean deleteCacheKey(String key) {
        return RedisUtils.deleteObject(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 键名列表
     */
    @Override
    public void deleteCacheBatch(List<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            RedisUtils.deleteObject(keys);
        }
    }

    /**
     * 将字符串解析为Long类型数值
     * @param value 待解析的字符串
     * @return 解析成功返回对应的Long值，解析失败或输入为空则返回null
     */
    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 将字符串解析为Integer类型数值
     * @param value 待解析的字符串
     * @return 解析成功返回对应的Integer值，解析失败或输入为空则返回null
     */
    private Integer parseInt(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 将字节数转换为可读的格式化字符串
     * @param bytes 字节数
     * @return 格式化后的大小字符串（B、KB、MB或GB单位）
     */
    private String formatBytes(Long bytes) {
        // 处理空值情况
        if (bytes == null) {
            return "0 B";
        }
        // 根据字节数选择合适的单位进行格式化
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

}
