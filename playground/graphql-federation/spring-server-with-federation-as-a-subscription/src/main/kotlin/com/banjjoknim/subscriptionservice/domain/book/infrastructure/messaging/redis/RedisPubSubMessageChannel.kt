package com.banjjoknim.subscriptionservice.domain.book.infrastructure.messaging.redis

import com.banjjoknim.subscriptionservice.domain.book.infrastructure.messaging.PubSubMessageChannel
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Profile("redis")
@Component
class RedisPubSubMessageChannel(
    private val redisMessageListenerContainer: ReactiveRedisMessageListenerContainer,
) : PubSubMessageChannel {

    companion object {
        private val channelTopic = ChannelTopic.of("pickupBook")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun getConnection(): Flux<String> {
        return redisMessageListenerContainer.receive(channelTopic)
            .map { it.message }
            .doOnEach { message -> logger.info("Message has received. message: $message") }
    }
}
