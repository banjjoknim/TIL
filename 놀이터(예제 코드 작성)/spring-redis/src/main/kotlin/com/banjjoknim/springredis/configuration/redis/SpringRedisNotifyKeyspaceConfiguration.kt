package com.banjjoknim.springredis.configuration.redis

import com.banjjoknim.springredis.configuration.redis.support.RedisLockManager
import com.banjjoknim.springredis.configuration.redis.support.SimpleSpringRedisKeyExpireEventListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import javax.annotation.PostConstruct

@Profile("spring-redis")
@Configuration
class SpringRedisNotifyKeyspaceConfiguration(
    private val redisLockManager: RedisLockManager,
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
            SimpleSpringRedisKeyExpireEventListener(redisLockManager),
            PatternTopic(REDIS_NOTIFY_KEY_EXPIRE_EVENT_TOPIC_PATTERN)
        )
        return container
    }
}
