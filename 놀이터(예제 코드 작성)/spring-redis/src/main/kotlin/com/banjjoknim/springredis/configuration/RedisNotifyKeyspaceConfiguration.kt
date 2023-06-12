package com.banjjoknim.springredis.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Configuration
class RedisNotifyKeyspaceConfiguration(
    private val redisTemplate: StringRedisTemplate,
    private val redisConnectionFactory: RedisConnectionFactory,
) {

    companion object {
        private const val REDIS_NOTIFY_KEYSPACE_EVENT_KEY = "notify-keyspace-events"
        private const val REDIS_NOTIFY_KEYSPACE_EVENT_VALUE = "KEA"
        private const val REDIS_NOTIFY_KEY_EXPIRE_EVENT_TOPIC_PATTERN = "__keyevent@*__:expired"
    }

    @PostConstruct
    fun afterRedisConnection() {
        redisTemplate.execute(RedisCallback { connection ->
            connection.setConfig(REDIS_NOTIFY_KEYSPACE_EVENT_KEY, REDIS_NOTIFY_KEYSPACE_EVENT_VALUE)
            println("redis connection is successfully complete!")
        })
    }

    @Bean
    fun redisMessageListenerContainer(): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(
            SimpleRedisKeyExpireEventListener(redisTemplate),
            PatternTopic(REDIS_NOTIFY_KEY_EXPIRE_EVENT_TOPIC_PATTERN)
        )
        return container
    }
}

class SimpleRedisKeyExpireEventListener(
    private val redisTemplate: StringRedisTemplate,
) : MessageListener {

    companion object {
        private const val REDIS_LOCK_TIME_OUT_SECONDS = 60L
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val messageKey = String(message.body)
        val lockKey = "simple-redis-lock-$messageKey"
        val operations = redisTemplate.opsForValue()
        val isLockAcquired = operations.setIfAbsent(lockKey, "isLock", REDIS_LOCK_TIME_OUT_SECONDS, TimeUnit.SECONDS)
        try {
            when (isLockAcquired) {
                true -> println("get message from redis: $messageKey")
                else -> {}
            }
        } finally {
            redisTemplate.delete(lockKey)
        }
    }
}
