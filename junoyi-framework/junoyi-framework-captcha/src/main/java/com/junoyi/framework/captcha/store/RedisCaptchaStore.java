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

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    private final RedissonClient redissonClient;

    public RedisCaptchaStore(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void save(String captchaId, String value, int expireSeconds) {
        RBucket<String> bucket = redissonClient.getBucket(buildKey(captchaId));
        bucket.set(value, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String get(String captchaId) {
        RBucket<String> bucket = redissonClient.getBucket(buildKey(captchaId));
        return bucket.get();
    }

    @Override
    public void remove(String captchaId) {
        redissonClient.getBucket(buildKey(captchaId)).delete();
    }

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

    private String buildKey(String captchaId) {
        return CAPTCHA_KEY_PREFIX + captchaId;
    }
}
