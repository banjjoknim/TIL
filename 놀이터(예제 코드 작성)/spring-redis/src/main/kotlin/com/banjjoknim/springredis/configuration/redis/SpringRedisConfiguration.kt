package com.banjjoknim.springredis.configuration.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Profile("spring-redis")
@Configuration
class SpringRedisConfiguration {

    @Value("\${spring.redis.host}")
    private lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    private lateinit var redisPort: String

    @Value("\${spring.redis.password}")
    private lateinit var redisPassword: RedisPassword

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = redisHost
        redisStandaloneConfiguration.port = redisPort.toInt()
        redisStandaloneConfiguration.password = redisPassword
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }
}
