package com.banjjoknim.subscriptionservice.config.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer

@Configuration
class RedisMessagingConfiguration(
    private val connectionFactory: ReactiveRedisConnectionFactory,
) {

    companion object {
        private val channelTopic = ChannelTopic.of("pickupBook")
    }

    @Bean
    fun redisMessageListenerContainer(): ReactiveRedisMessageListenerContainer {
        val container = ReactiveRedisMessageListenerContainer(connectionFactory)
        container.receive(channelTopic)
            .map { it.message }
            .subscribe { message -> println("Message has received. message: $message")}
        return container
    }
}
