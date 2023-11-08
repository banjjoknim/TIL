package com.banjjoknim.subscriptionservice.domain.book.infrastructure.messaging.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer

@Profile("redis")
@Configuration
class RedisMessageListenerContainerConfiguration(
    private val connectionFactory: ReactiveRedisConnectionFactory,
) {

    @Bean
    fun redisMessageListenerContainer(): ReactiveRedisMessageListenerContainer {
        return ReactiveRedisMessageListenerContainer(connectionFactory)
    }
}
