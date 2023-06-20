package com.banjjoknim.springredis.configuration.redis

import com.banjjoknim.springredis.configuration.redis.support.RedisLockManager
import com.banjjoknim.springredis.configuration.redis.support.SimpleRedissonKeyExpireEventListener
import org.redisson.api.RedissonClient
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.annotation.PostConstruct

@Profile("redisson")
@Configuration
class RedissonNotifyKeyspaceConfiguration(
    private val redisLockManager: RedisLockManager,
    private val redissonClient: RedissonClient,
) {

    companion object {
        private const val REDIS_NOTIFY_KEYSPACE_EVENT_KEY = "notify-keyspace-events"
        private const val REDIS_NOTIFY_KEYSPACE_EVENT_VALUE = "KEA"
        private const val REDIS_NOTIFY_KEY_EXPIRE_EVENT_TOPIC_PATTERN = "__keyevent@*__:expired"
    }

    @PostConstruct
    fun afterRedisConnection() {
        val topic = redissonClient.getPatternTopic(REDIS_NOTIFY_KEY_EXPIRE_EVENT_TOPIC_PATTERN)
        topic.addListener(String::class.java, SimpleRedissonKeyExpireEventListener(redisLockManager))
    }
}
