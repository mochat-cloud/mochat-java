package com.mochat.mochat.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author: Huayu
 * @description: 缓存工具类
 */
@Component
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    /**
     * 添加, 永久有效
     *
     * @param key   键
     * @param value 值
     */
    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 添加并设置有效期
     *
     * @param key    键
     * @param value  值
     * @param millis 有效期, 毫秒. millis 等于 0:永久有效
     */
    public static void set(String key, Object value, long millis) {
        if (millis > 0) {
            redisTemplate.opsForValue().set(key, value, millis, TimeUnit.MILLISECONDS);
        } else {
            set(key, value);
        }
    }

    /**
     * 更新指定 key 的有效期
     *
     * @param key    键
     * @param millis 有效期, 毫秒
     */
    public static void updateExpire(String key, long millis) {
        if (millis > 0) {
            redisTemplate.expire(key, millis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 获取指定 key 的剩余有效期
     *
     * @param key 键 不能为null
     * @return 毫秒 0: 未设置有效期
     */
    public static long getExpire(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (expire == null) {
            return 0;
        } else {
            return expire;
        }
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true: 存在, false: 不存在
     */
    public static boolean hasKey(String key) {
        return redisTemplate.hasKey(key) != null;
    }

    /**
     * 删除 key
     *
     * @param key 可以传一个值或多个
     */
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            redisTemplate.delete(Arrays.asList(key));
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

}
