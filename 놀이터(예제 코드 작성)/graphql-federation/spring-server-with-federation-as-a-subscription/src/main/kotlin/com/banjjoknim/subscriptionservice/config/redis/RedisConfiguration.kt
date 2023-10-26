package com.banjjoknim.subgraphservice.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.*
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RedisConfiguration {

    @Value("\${REDIS_HOST}")
    private lateinit var redisHost: String

    @Value("\${REDIS_PORT}")
    private lateinit var redisPort: String

    /**
     * @see org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
     * @see org.springframework.data.redis.core.ReactiveRedisTemplate // ReactiveRedisConnectionFactory Bean 존재시 자동으로 Bean 등록.
     * @see org.springframework.data.redis.core.ReactiveStringRedisTemplate  // ReactiveRedisConnectionFactory Bean 존재시 자동으로 Bean 등록.
     * @see org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
     * @see org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration
     */
//    @Bean // 다수의 Bean 으로 인한 autoconfigure 실패로 인해 주석처리
    fun connectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(redisHost, redisPort.toInt())
    }
}
