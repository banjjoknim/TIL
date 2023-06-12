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
            SimpleRedisKeyExpireEventListener(),
            PatternTopic(REDIS_NOTIFY_KEY_EXPIRE_EVENT_TOPIC_PATTERN)
        )
        container.configureDistributeLock()
        return container
    }

    private fun RedisMessageListenerContainer.configureDistributeLock() {
        this.setTaskExecutor { task ->
            val lockKey = "eventProcessingLock"
            val lockValue = "lockValue"
            val lockTimeoutSeconds = 60L // Timeout for the lock in seconds

            val result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue)
            if (result == true) {
                // Acquired the lock, process the event
                try {
                    redisTemplate.expire(lockKey, lockTimeoutSeconds, TimeUnit.SECONDS)
                    task.run()
                } finally {
                    // Release the lock
                    redisTemplate.delete(lockKey)
                }
            }
        }
    }
}

class SimpleRedisKeyExpireEventListener : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        println("get message from redis: ${String(message.body)}")
    }
}
