package com.banjjoknim.subgraphservice.domain.book.infrastructure

import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component

@Profile("redis")
@Component
class RedisChannel(
    private val redisTemplate: ReactiveStringRedisTemplate,
) : MessageChannel {

    override fun sendMessage(pubsubMessage: PubSubMessage) {
        val channel = pubsubMessage.destination
        val message = pubsubMessage.content
        redisTemplate.convertAndSend(channel, message).subscribe()
    }

}
