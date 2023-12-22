package com.banjjoknim.springredis.configuration.redis.support

import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Profile("spring-redis")
@Component
class SpringRedisLockManager(
    private val redisTemplate: StringRedisTemplate,
) : RedisLockManager {

    companion object {
        private const val REDIS_LOCK_KEY_PREFIX = "simple-redis-lock"
        private const val REDIS_LOCK_TIME_OUT_SECONDS = 60L
        private const val REDIS_LOCK_VALUE = "isLocked"
    }

    override fun acquireLock(messageKey: String): Boolean {
        val lockKey = "$REDIS_LOCK_KEY_PREFIX-$messageKey"
        val operations = redisTemplate.opsForValue()
        val isLockAcquired =
            operations.setIfAbsent(lockKey, REDIS_LOCK_VALUE, REDIS_LOCK_TIME_OUT_SECONDS, TimeUnit.SECONDS)
        return isLockAcquired ?: false
    }

    override fun releaseLock(messageKey: String): Boolean {
        val isDeleted = redisTemplate.delete("$REDIS_LOCK_KEY_PREFIX-$messageKey")
        when (isDeleted) {
            true -> println("release lock has success. key: $messageKey")
            false -> println("release lock has failed. key: $messageKey")
        }
        return isDeleted
    }
}
