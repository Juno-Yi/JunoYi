package com.junoyi.framework.captcha.store;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Redis 验证码存储实现
 *
 * @author Fan
 */
public class RedisCaptchaStore implements CaptchaStore {

    /**
     * 验证码键名前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    private final RedissonClient redissonClient;

    /**
     * 构造函数
     *
     * @param redissonClient Redisson客户端实例
     */
    public RedisCaptchaStore(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 保存验证码到Redis
     *
     * @param captchaId 验证码ID
     * @param value 验证码值
     * @param expireSeconds 过期时间（秒）
     */
    @Override
    public void save(String captchaId, String value, int expireSeconds) {
        RBucket<String> bucket = redissonClient.getBucket(buildKey(captchaId));
        bucket.set(value, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 从Redis获取验证码
     *
     * @param captchaId 验证码ID
     * @return 验证码值，如果不存在则返回null
     */
    @Override
    public String get(String captchaId) {
        RBucket<String> bucket = redissonClient.getBucket(buildKey(captchaId));
        return bucket.get();
    }

    /**
     * 从Redis删除验证码
     *
     * @param captchaId 验证码ID
     */
    @Override
    public void remove(String captchaId) {
        redissonClient.getBucket(buildKey(captchaId)).delete();
    }

    /**
     * 验证并删除验证码
     *
     * @param captchaId 验证码ID
     * @param value 待验证的验证码值
     * @return 验证成功返回true，否则返回false
     */
    @Override
    public boolean validateAndRemove(String captchaId, String value) {
        String key = buildKey(captchaId);
        RBucket<String> bucket = redissonClient.getBucket(key);
        String storedValue = bucket.get();
        if (storedValue != null && storedValue.equalsIgnoreCase(value)) {
            bucket.delete();
            return true;
        }
        return false;
    }

    /**
     * 构建Redis键名
     *
     * @param captchaId 验证码ID
     * @return 带前缀的完整键名
     */
    private String buildKey(String captchaId) {
        return CAPTCHA_KEY_PREFIX + captchaId;
    }
}
