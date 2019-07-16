package com.ivay.ivay_common.utils;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 分布式锁工具类
 * @Author:
 * @CreateDate:
 */
@Component
@Slf4j
public class RedisLock {
    @Resource
    private RedisTemplate redisTemplate;

    public static final String UNLOCK_LUA;

    public static long EXPIRE_TIME = 1 * 60 * 1000;

    /**
     * 释放锁脚本，原子操作
     */
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 获取分布式锁，原子操作
     *
     * @param lockKey
     * @param requestId
     *            唯一ID, 可以使用UUID.randomUUID().toString();
     * @param expire
     * @param timeUnit
     * @return
     */
    public boolean tryLock(String lockKey, String requestId, long expire, TimeUnit timeUnit) {
        try {
            RedisCallback<Boolean> callback = connection -> connection.set(lockKey.getBytes(Charset.forName("UTF-8")),
                requestId.getBytes(Charset.forName("UTF-8")), Expiration.seconds(timeUnit.toSeconds(expire)),
                RedisStringCommands.SetOption.SET_IF_ABSENT);
            return (Boolean)redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("redis lock error.", e);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey
     * @param requestId
     *            唯一ID
     * @return
     */
    public boolean releaseLock(String lockKey, String requestId) {
        RedisCallback<Boolean> callback = connection -> connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1,
            lockKey.getBytes(Charset.forName("UTF-8")), requestId.getBytes(Charset.forName("UTF-8")));
        return (Boolean)redisTemplate.execute(callback);
    }

    public boolean tryBorrowLock(String userGid) {
        String lockKey = SysVariable.REDIS_BORROW_MONEY_PREFIX + userGid;
        return tryLock(lockKey, userGid, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseBorrowLock(String userGid) {
        String lockKey = SysVariable.REDIS_BORROW_MONEY_PREFIX + userGid;
        return releaseLock(lockKey, userGid);
    }

    public boolean tryOverdueFeeLock(String date) {
        String lockKey = SysVariable.REDIS_OVERDUE_FEE_PREFIX + date;
        return tryLock(lockKey, date, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseOverdueFeeLock(String date) {
        String lockKey = SysVariable.REDIS_OVERDUE_FEE_PREFIX + date;
        return releaseLock(lockKey, date);
    }

    public boolean tryAutoCreateVALock(String date) {
        String lockKey = SysVariable.REDIS_AUTO_CREATEVA_PREFIX + date;
        return tryLock(lockKey, date, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseAutoCreateVALock(String date) {
        String lockKey = SysVariable.REDIS_AUTO_CREATEVA_PREFIX + date;
        return releaseLock(lockKey, date);
    }

    public boolean tryExpireNoticeLock(String date) {
        String lockKey = SysVariable.REDIS_EXPIRE_NOTICE_PREFIX + date;
        return tryLock(lockKey, date, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseExpireNoticeLock(String date) {
        String lockKey = SysVariable.REDIS_EXPIRE_NOTICE_PREFIX + date;
        return releaseLock(lockKey, date);
    }

    public boolean tryeOverdueNoticeLock(String date) {
        String lockKey = SysVariable.REDIS_OVERDUE_NOTICE_PREFIX + date;
        return tryLock(lockKey, date, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseOverdueNoticeLock(String date) {
        String lockKey = SysVariable.REDIS_OVERDUE_NOTICE_PREFIX + date;
        return releaseLock(lockKey, date);
    }

    public boolean tryAppNumLock(String updateDate, String userGid) {
        String requestId = updateDate + ":" + userGid;
        String lockKey = SysVariable.REDIS_RISK_APPNUM_PREFIX + requestId;
        return tryLock(lockKey, requestId, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseAppNumLock(String updateDate, String userGid) {
        String requestId = updateDate + ":" + userGid;
        String lockKey = SysVariable.REDIS_RISK_APPNUM_PREFIX + requestId;
        return releaseLock(lockKey, requestId);
    }

    public boolean tryGpsLock(String userGid) {
        String lockKey = SysVariable.REDIS_RISK_GPS_PREFIX + userGid;
        return tryLock(lockKey, userGid, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseGpsLock(String userGid) {
        String lockKey = SysVariable.REDIS_RISK_GPS_PREFIX + userGid;
        return releaseLock(lockKey, userGid);
    }

    public boolean tryOtherRiskLock(String userGid) {
        String lockKey = SysVariable.REDIS_RISK_OTHER_PREFIX + userGid;
        return tryLock(lockKey, userGid, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean releaseOtherRiskLock(String userGid) {
        String lockKey = SysVariable.REDIS_RISK_OTHER_PREFIX + userGid;
        return releaseLock(lockKey, userGid);
    }

    /**
     * 获取Redis锁的value值
     *
     * @param lockKey
     * @return
     */
    public String get(String lockKey) {
        try {
            RedisCallback<String> callback =
                connection -> new String(connection.get(lockKey.getBytes()), Charset.forName("UTF-8"));
            return (String)redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("get redis occurred an exception", e);
        }
        return null;
    }
}
