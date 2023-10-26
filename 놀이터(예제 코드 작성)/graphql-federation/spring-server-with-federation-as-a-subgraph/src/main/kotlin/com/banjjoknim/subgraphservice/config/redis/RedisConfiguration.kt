package com.banjjoknim.subgraphservice.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RedisConfiguration {

    @Value("\${REDIS_HOST}")
    private lateinit var redisHost: String

    @Value("\${REDIS_PORT}")
    private lateinit var redisPort: String

    /**
     * ```
     * @Bean 주석처리 이유.
     * -> Parameter 0 of method reactiveStringRedisTemplate in org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration required a single bean, but 2 were found:
     * 	- connectionFactory: defined by method 'connectionFactory' in class path resource [com/banjjoknim/subgraphservice/config/redis/RedisConfiguration.class]
     * 	- redisConnectionFactory: defined by method 'redisConnectionFactory' in class path resource [org/springframework/boot/autoconfigure/data/redis/LettuceConnectionConfiguration.class]
     * 	```
     *
     * @see org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
     * @see org.springframework.data.redis.core.ReactiveRedisTemplate // ReactiveRedisConnectionFactory Bean 존재시 자동으로 Bean 등록.
     * @see org.springframework.data.redis.core.ReactiveStringRedisTemplate  // ReactiveRedisConnectionFactory Bean 존재시 자동으로 Bean 등록.
     * @see org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
     * @see org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration // RedisConnectionFactory 타입 Bean이 존재하지 않을 경우 자동으로 redisConnectionFactory Bean을 등록함.
     */
//    @Bean // 주석처리에 대해서는 상기 doc에 기재된 내용 참조.
    fun connectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(redisHost, redisPort.toInt())
    }
}
