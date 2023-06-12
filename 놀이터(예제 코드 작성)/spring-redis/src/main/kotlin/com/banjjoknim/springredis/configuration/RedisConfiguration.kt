package com.banjjoknim.springredis.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfiguration {

    @Value("\${spring.redis.host}")
    private lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    private lateinit var redisPort: String

    @Value("\${spring.redis.password}")
    private lateinit var redisPassword: RedisPassword

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.setConnectionFactory(redisConnectionFactory())
        return redisTemplate
    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = redisHost
        redisStandaloneConfiguration.port = redisPort.toInt()
        redisStandaloneConfiguration.password = redisPassword
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }
}
