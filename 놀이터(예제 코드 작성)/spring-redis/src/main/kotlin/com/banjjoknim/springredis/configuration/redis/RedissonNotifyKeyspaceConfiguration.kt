package com.banjjoknim.springredis.configuration.redis

import com.banjjoknim.springredis.configuration.redis.support.RedisLockManager
import com.banjjoknim.springredis.configuration.redis.support.SimpleRedissonKeyExpireEventListener
import org.redisson.api.RedissonClient
import org.redisson.api.redisnode.RedisNode
import org.redisson.api.redisnode.RedisNodes
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

    /**
     * @see org.redisson.api.redisnode.RedisNode
     * @see org.redisson.api.redisnode.RedisNodes
     * @see org.redisson.api.redisnode.BaseRedisNodes
     * @see org.redisson.api.redisnode.RedisNodes.SINGLE
     * @see org.redisson.api.redisnode.RedisNodes.CLUSTER
     * @see org.redisson.api.redisnode.RedisNodes.MASTER_SLAVE
     * @see org.redisson.api.redisnode.RedisNodes.SENTINEL_MASTER_SLAVE
     */
    @PostConstruct
    fun afterRedisConnection() {
        val redisNodes = redissonClient.getRedisNodes(RedisNodes.SINGLE)

        val redisMasterNode = redisNodes.instance
        redisMasterNode.setConfig(REDIS_NOTIFY_KEYSPACE_EVENT_KEY, REDIS_NOTIFY_KEYSPACE_EVENT_VALUE)

        val topic = redissonClient.getPatternTopic(REDIS_NOTIFY_KEY_EXPIRE_EVENT_TOPIC_PATTERN)
        topic.addListener(String::class.java, SimpleRedissonKeyExpireEventListener(redisLockManager))

        val redisInfo = redisMasterNode.info(RedisNode.InfoSection.ALL)
        println("redisInfo: [$redisInfo]")
        println("redis connection is successfully complete!")
    }
}
