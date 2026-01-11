package com.junoyi.framework.redis.utils;

import com.junoyi.framework.core.utils.SpringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.*;
import org.redisson.api.redisnode.RedisNode;
import org.redisson.api.redisnode.RedisNodes;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Redis 工具类，提供基于 Redisson 的 Redis 操作封装。
 * 包括限流、发布订阅、缓存操作（对象、List、Set、Map）、原子计数器等功能。
 *
 * @author Fan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtils {

    /**
     * Redisson 客户端实例，通过 Spring 上下文获取。
     */
    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    /**
     * 使用令牌桶算法进行限流控制。
     *
     * @param key          限流标识键
     * @param rateType     限流类型（如：OVERALL 全局限流）
     * @param rate         单位时间内允许的请求数量
     * @param rateInterval 时间单位（秒）
     * @return 可用许可数量；若无法获取则返回 -1
     */
    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval) {
        RRateLimiter rateLimiter = CLIENT.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, rateInterval, RateIntervalUnit.SECONDS);
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        } else {
            return -1L;
        }
    }

    /**
     * 获取 Redisson 客户端实例。
     *
     * @return RedissonClient 实例
     */
    public static RedissonClient getClient() {
        return CLIENT;
    }

    /**
     * 向指定频道发布消息，并执行自定义消费逻辑。
     *
     * @param channelKey 频道名称
     * @param msg        要发送的消息内容
     * @param consumer   消息处理回调函数
     * @param <T>        泛型参数，表示消息类型
     */
    public static <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
        consumer.accept(msg);
    }

    /**
     * 向指定频道发布消息。
     *
     * @param channelKey 频道名称
     * @param msg        要发送的消息内容
     * @param <T>        泛型参数，表示消息类型
     */
    public static <T> void publish(String channelKey, T msg) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
    }

    /**
     * 订阅指定频道并注册消息监听器。
     *
     * @param channelKey 频道名称
     * @param clazz      接收消息的数据类型
     * @param consumer   消息处理回调函数
     * @param <T>        泛型参数，表示消息类型
     */
    public static <T> void subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.addListener(clazz, (channel, msg) -> consumer.accept(msg));
    }

    /**
     * 缓存一个对象，默认不保留 TTL。
     *
     * @param key   缓存键名
     * @param value 缓存值
     * @param <T>   泛型参数，表示缓存对象类型
     */
    public static <T> void setCacheObject(final String key, final T value) {
        setCacheObject(key, value, false);
    }

    /**
     * 缓存一个对象，可选择是否保留当前 TTL。
     *
     * @param key       缓存键名
     * @param value     缓存值
     * @param isSaveTtl 是否保留原 TTL
     * @param <T>       泛型参数，表示缓存对象类型
     */
    public static <T> void setCacheObject(final String key, final T value, final boolean isSaveTtl) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        if (isSaveTtl) {
            try {
                bucket.setAndKeepTTL(value);
            } catch (Exception e) {
                long timeToLive = bucket.remainTimeToLive();
                setCacheObject(key, value, Duration.ofMillis(timeToLive));
            }
        } else {
            bucket.set(value);
        }
    }

    /**
     * 缓存一个对象，并设置过期时间。
     *
     * @param key      缓存键名
     * @param value    缓存值
     * @param duration 过期时间
     * @param <T>      泛型参数，表示缓存对象类型
     */
    public static <T> void setCacheObject(final String key, final T value, final Duration duration) {
        RBatch batch = CLIENT.createBatch();
        RBucketAsync<T> bucket = batch.getBucket(key);
        bucket.setAsync(value);
        bucket.expireAsync(duration);
        batch.execute();
    }

    /**
     * 添加对象变化监听器。
     *
     * @param key      缓存键名
     * @param listener 监听器实现
     * @param <T>      泛型参数，表示缓存对象类型
     */
    public static <T> void addObjectListener(final String key, final ObjectListener listener) {
        RBucket<T> result = CLIENT.getBucket(key);
        result.addListener(listener);
    }

    /**
     * 设置缓存键的有效时间（以秒为单位）。
     *
     * @param key     缓存键名
     * @param timeout 过期时间（秒）
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout) {
        return expire(key, Duration.ofSeconds(timeout));
    }

    /**
     * 设置缓存键的有效时间。
     *
     * @param key      缓存键名
     * @param duration 过期时间
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final Duration duration) {
        RBucket rBucket = CLIENT.getBucket(key);
        return rBucket.expire(duration);
    }

    /**
     * 获取缓存对象。
     *
     * @param key 缓存键名
     * @param <T> 泛型参数，表示缓存对象类型
     * @return 缓存对象值
     */
    public static <T> T getCacheObject(final String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.get();
    }

    /**
     * 查询缓存键剩余存活时间。
     *
     * @param key 缓存键名
     * @param <T> 泛型参数，表示缓存对象类型
     * @return 剩余存活时间（毫秒），-2 表示不存在或已过期
     */
    public static <T> long getTimeToLive(final String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.remainTimeToLive();
    }

    /**
     * 删除单个缓存对象。
     *
     * @param key 缓存键名
     * @return true=删除成功；false=删除失败
     */
    public static boolean deleteObject(final String key) {
        return CLIENT.getBucket(key).delete();
    }

    /**
     * 批量删除多个缓存对象。
     *
     * @param collection 缓存键集合
     */
    public static void deleteObject(final Collection collection) {
        RBatch batch = CLIENT.createBatch();
        collection.forEach(t -> {
            batch.getBucket(t.toString()).deleteAsync();
        });
        batch.execute();
    }

    /**
     * 判断某个缓存键是否存在。
     *
     * @param key 缓存键名
     * @return true=存在；false=不存在
     */
    public static boolean isExistsObject(final String key) {
        return CLIENT.getBucket(key).isExists();
    }

    /**
     * 将 List 数据缓存到 Redis 中。
     *
     * @param key      缓存键名
     * @param dataList 待缓存的 List 数据
     * @param <T>      泛型参数，表示元素类型
     * @return true=添加成功；false=添加失败
     */
    public static <T> boolean setCacheList(final String key, final List<T> dataList) {
        RList<T> rList = CLIENT.getList(key);
        return rList.addAll(dataList);
    }

    /**
     * 添加 List 类型监听器。
     *
     * @param key      缓存键名
     * @param listener 监听器实现
     * @param <T>      泛型参数，表示元素类型
     */
    public static <T> void addListListener(final String key, final ObjectListener listener) {
        RList<T> rList = CLIENT.getList(key);
        rList.addListener(listener);
    }

    /**
     * 获取缓存中的 List 数据。
     *
     * @param key 缓存键名
     * @param <T> 泛型参数，表示元素类型
     * @return List 数据
     */
    public static <T> List<T> getCacheList(final String key) {
        RList<T> rList = CLIENT.getList(key);
        return rList.readAll();
    }

    /**
     * 将 Set 数据缓存到 Redis 中。
     *
     * @param key     缓存键名
     * @param dataSet 待缓存的 Set 数据
     * @param <T>     泛型参数，表示元素类型
     * @return true=添加成功；false=添加失败
     */
    public static <T> boolean setCacheSet(final String key, final Set<T> dataSet) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.addAll(dataSet);
    }

    /**
     * 添加 Set 类型监听器。
     *
     * @param key      缓存键名
     * @param listener 监听器实现
     * @param <T>      泛型参数，表示元素类型
     */
    public static <T> void addSetListener(final String key, final ObjectListener listener) {
        RSet<T> rSet = CLIENT.getSet(key);
        rSet.addListener(listener);
    }

    /**
     * 获取缓存中的 Set 数据。
     *
     * @param key 缓存键名
     * @param <T> 泛型参数，表示元素类型
     * @return Set 数据
     */
    public static <T> Set<T> getCacheSet(final String key) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.readAll();
    }

    /**
     * 将 Map 数据缓存到 Redis 中。
     *
     * @param key     缓存键名
     * @param dataMap 待缓存的 Map 数据
     * @param <T>     泛型参数，表示值类型
     */
    public static <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            RMap<String, T> rMap = CLIENT.getMap(key);
            rMap.putAll(dataMap);
        }
    }

    /**
     * 添加 Map 类型监听器。
     *
     * @param key      缓存键名
     * @param listener 监听器实现
     * @param <T>      泛型参数，表示值类型
     */
    public static <T> void addMapListener(final String key, final ObjectListener listener) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.addListener(listener);
    }

    /**
     * 获取缓存中的 Map 数据。
     *
     * @param key 缓存键名
     * @param <T> 泛型参数，表示值类型
     * @return Map 数据
     */
    public static <T> Map<String, T> getCacheMap(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.getAll(rMap.keySet());
    }

    /**
     * 获取缓存 Map 的所有键集合。
     *
     * @param key 缓存键名
     * @param <T> 泛型参数，表示值类型
     * @return 键集合
     */
    public static <T> Set<String> getCacheMapKeySet(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.keySet();
    }

    /**
     * 在 Hash 结构中存储一个字段值。
     *
     * @param key   Redis 键名
     * @param hKey  Hash 字段名
     * @param value 存储的值
     * @param <T>   泛型参数，表示值类型
     */
    public static <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.put(hKey, value);
    }

    /**
     * 获取 Hash 结构中的字段值。
     *
     * @param key  Redis 键名
     * @param hKey Hash 字段名
     * @param <T>  泛型参数，表示值类型
     * @return 字段对应的值
     */
    public static <T> T getCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.get(hKey);
    }

    /**
     * 删除 Hash 结构中的字段。
     *
     * @param key  Redis 键名
     * @param hKey Hash 字段名
     * @param <T>  泛型参数，表示值类型
     * @return 被删除的字段值
     */
    public static <T> T delCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.remove(hKey);
    }

    /**
     * 批量获取 Hash 中多个字段的值。
     *
     * @param key   Redis 键名
     * @param hKeys Hash 字段名集合
     * @param <K>   泛型参数，表示字段类型
     * @param <V>   泛型参数，表示值类型
     * @return 字段与值组成的映射表
     */
    public static <K, V> Map<K, V> getMultiCacheMapValue(final String key, final Set<K> hKeys) {
        RMap<K, V> rMap = CLIENT.getMap(key);
        return rMap.getAll(hKeys);
    }

    /**
     * 设置原子长整型变量的初始值。
     *
     * @param key   Redis 键名
     * @param value 初始值
     */
    public static void setAtomicValue(String key, long value) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        atomic.set(value);
    }

    /**
     * 获取原子长整型变量的当前值。
     *
     * @param key Redis 键名
     * @return 当前值
     */
    public static long getAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.get();
    }

    /**
     * 对原子长整型变量执行递增操作。
     *
     * @param key Redis 键名
     * @return 递增后的值
     */
    public static long incrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.incrementAndGet();
    }

    /**
     * 对原子长整型变量执行递减操作。
     *
     * @param key Redis 键名
     * @return 递减后的值
     */
    public static long decrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.decrementAndGet();
    }

    /**
     * 根据模式匹配查找所有符合规则的键。
     *
     * @param pattern 键名匹配模式（支持通配符）
     * @return 符合条件的所有键集合
     */
    public static Collection<String> keys(final String pattern) {
        Stream<String> stream = CLIENT.getKeys().getKeysStreamByPattern(pattern);
        return stream.collect(Collectors.toList());
    }

    /**
     * 根据模式匹配批量删除键。
     *
     * @param pattern 键名匹配模式（支持通配符）
     */
    public static void deleteKeys(final String pattern) {
        CLIENT.getKeys().deleteByPattern(pattern);
    }

    /**
     * 判断 Redis 中是否存在指定键。
     *
     * @param key 键名
     * @return true=存在；false=不存在
     */
    public static Boolean hasKey(String key) {
        RKeys rKeys = CLIENT.getKeys();
        return rKeys.countExists(key) > 0;
    }

    /**
     * 获取 Redis 服务器信息。
     *
     * @param section 信息分类（如 server、memory、stats 等）
     * @return 服务器信息 Map
     */
    public static Map<String, String> getServerInfo(String section) {
        return CLIENT.getRedisNodes(RedisNodes.SINGLE).getInstance().info(RedisNode.InfoSection.valueOf(section.toUpperCase()));
    }

    /**
     * 获取 Redis 所有服务器信息。
     *
     * @return 服务器信息 Map
     */
    public static Map<String, String> getServerInfo() {
        return CLIENT.getRedisNodes(RedisNodes.SINGLE).getInstance().info(RedisNode.InfoSection.ALL);
    }

    /**
     * 获取 Redis 数据库大小（键数量）。
     *
     * @return 键数量
     */
    public static long getDbSize() {
        return CLIENT.getKeys().count();
    }

    /**
     * 获取键的类型。
     *
     * @param key 键名
     * @return 类型：string、list、set、zset、hash、none
     */
    public static String getType(String key) {
        RType type = CLIENT.getKeys().getType(key);
        if (type == null) {
            return "none";
        }
        return type.name().toLowerCase();
    }

    /**
     * 获取键的 TTL（秒）。
     *
     * @param key 键名
     * @return TTL（秒），-1 表示永不过期，-2 表示不存在
     */
    public static long getTtl(String key) {
        long ttl = CLIENT.getBucket(key).remainTimeToLive();
        if (ttl == -1) {
            return -1; // 永不过期
        } else if (ttl == -2) {
            return -2; // 不存在
        }
        return ttl / 1000; // 毫秒转秒
    }

    /**
     * 获取键的内存占用（字节）。
     * 使用 MEMORY USAGE 命令。
     *
     * @param key 键名
     * @return 内存占用（字节），null 表示键不存在或命令不支持
     */
    public static Long getMemoryUsage(String key) {
        try {
            Object result = CLIENT.getScript().eval(
                    RScript.Mode.READ_ONLY,
                    "return redis.call('MEMORY', 'USAGE', KEYS[1])",
                    RScript.ReturnType.INTEGER,
                    List.of(key)
            );
            return result != null ? (Long) result : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取键的元素数量/大小。
     * 根据类型调用不同命令：STRLEN/LLEN/SCARD/ZCARD/HLEN
     *
     * @param key 键名
     * @return 大小，null 表示键不存在
     */
    public static Long getSize(String key) {
        RType type = CLIENT.getKeys().getType(key);
        if (type == null) {
            return null;
        }
        return switch (type) {
            case OBJECT -> (long) CLIENT.getBucket(key).size();
            case LIST -> (long) CLIENT.getList(key).size();
            case SET -> (long) CLIENT.getSet(key).size();
            case ZSET -> (long) CLIENT.getScoredSortedSet(key).size();
            case MAP -> (long) CLIENT.getMap(key).size();
        };
    }
}
