package com.ivay.ivay_app.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate redisTemplate;

    // region -- 读操作
    // 字符串
    public Object get(String key) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    // Hash
    public Object hashGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    // Hash键
    public Set<Object> hashKeys(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.keys(key);
    }

    // 列表
    public Object leftPop(String k) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.leftPop(k);
    }

    // 列表
    public List<Object> listRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    // 集合
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    // 有序集合
    public Set<Object> setRangeByScore(String key, double source, double source1) {
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        return zSet.rangeByScore(key, source, source1);
    }
    // endregion

    // region -- 写操作
    // 字符串
    public boolean set(String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 字符串
    public boolean set(String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime.longValue(), TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // Hash
    public void hashPut(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    // 列表
    public void listLeftPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(k, v);
    }

    // 集合
    public void setAdd(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, new Object[]{value});
    }

    // 有序集合
    public void zSetAdd(String key, Object value, double source) {
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        zSet.add(key, value, source);
    }
    // endregion

    // region -- 删操作
    // 删 单个key
    public void delete(String key) {
        if (hasKey(key)) {
            redisTemplate.delete(key);
        }
    }

    // 删 多个key
    public void delete(String... keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    // 删 正则匹配
    public void deleteByPattern(String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    // 删 Hash键
    public void hashDelete(String masterKey, Object... hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.delete(masterKey, hashKey);
    }
    // endregion

    // region -- 其他操作
    // 判断key是否存在
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key).booleanValue();
    }

    // 设置过期时间
    public boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS).booleanValue();
    }

    // 获取剩余过期时间
    public long getExpire(String key) {
        return redisTemplate.getExpire(key).longValue();
    }
    // endregion
}
