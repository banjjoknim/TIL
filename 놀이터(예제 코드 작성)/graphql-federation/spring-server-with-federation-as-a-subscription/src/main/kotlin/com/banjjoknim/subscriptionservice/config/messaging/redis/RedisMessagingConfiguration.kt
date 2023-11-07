package com.banjjoknim.subscriptionservice.config.messaging.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer

@Configuration
class RedisMessagingConfiguration(
    private val connectionFactory: ReactiveRedisConnectionFactory,
) {

    @Bean
    fun redisMessageListenerContainer(): ReactiveRedisMessageListenerContainer {
        return ReactiveRedisMessageListenerContainer(connectionFactory)
    }
}
