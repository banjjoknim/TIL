package com.banjjoknim.springredis.configuration.redis.support

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener

class SimpleSpringRedisKeyExpireEventListener(
    private val redisLockManager: RedisLockManager,
) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val messageKey = String(message.body)
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
