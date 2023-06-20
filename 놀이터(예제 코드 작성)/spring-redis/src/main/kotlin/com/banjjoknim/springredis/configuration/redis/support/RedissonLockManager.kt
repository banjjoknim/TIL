package com.banjjoknim.springredis.configuration.redis.support

import org.redisson.api.RedissonClient
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Profile("redisson")
@Component
class RedissonLockManager(
    private val redissonClient: RedissonClient,
) : RedisLockManager {

    companion object {
        private const val REDIS_LOCK_KEY_PREFIX = "simple-redis-lock"
        private const val REDIS_LOCK_TIME_OUT_SECONDS = 60L
    }

    override fun acquireLock(messageKey: String): Boolean {
        val lockKey = "${REDIS_LOCK_KEY_PREFIX}-$messageKey"
        val lock = redissonClient.getLock(lockKey)
        return lock.tryLock(REDIS_LOCK_TIME_OUT_SECONDS, TimeUnit.SECONDS)
    }

    override fun releaseLock(messageKey: String): Boolean {
        val lock = redissonClient.getLock(messageKey)
        lock.unlock()
        return lock.isLocked
    }
}
