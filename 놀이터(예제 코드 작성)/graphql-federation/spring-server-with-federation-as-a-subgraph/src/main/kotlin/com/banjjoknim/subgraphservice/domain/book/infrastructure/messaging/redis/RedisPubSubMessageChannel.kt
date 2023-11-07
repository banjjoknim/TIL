package com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.redis

import com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.PubSubMessage
import com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.PubSubMessageChannel
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component

@Profile("redis")
@Component
class RedisPubSubMessageChannel(
    private val redisTemplate: ReactiveStringRedisTemplate,
) : PubSubMessageChannel {

    override fun sendMessage(pubsubMessage: PubSubMessage) {
        val channel = pubsubMessage.destination
        val message = pubsubMessage.content
        redisTemplate.convertAndSend(channel, message).subscribe()
    }

}
