package com.banjjoknim.springredis.configuration.redis.support

import org.redisson.api.listener.PatternMessageListener

class SimpleRedissonKeyExpireEventListener(
    private val redisLockManager: RedisLockManager,
) : PatternMessageListener<String> {

    override fun onMessage(pattern: CharSequence, channel: CharSequence, msg: String) {
        val messageKey = msg
        val isLockAcquired = redisLockManager.acquireLock(messageKey)
        try {
            when (isLockAcquired) {
                true -> println("get message has success from redis. key: $messageKey")
                else -> println("acquire lock has failed. lock is already occupied. key: $messageKey")
            }
        } finally {
            redisLockManager.releaseLock(messageKey)
        }
    }
}
