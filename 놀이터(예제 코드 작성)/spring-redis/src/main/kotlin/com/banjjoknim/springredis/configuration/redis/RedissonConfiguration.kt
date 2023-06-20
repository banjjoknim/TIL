package com.banjjoknim.springredis.configuration.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("redisson")
@Configuration
class RedissonConfiguration {

    @Value("\${spring.redis.host}")
    private lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    private lateinit var redisPort: String

    @Value("\${spring.redis.password}")
    private lateinit var redisPassword: String

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
            .setCodec(StringCodec.INSTANCE)
        val serverConfig = config.useSingleServer()
            .setAddress("redis://$redisHost:$redisPort")
        if (redisPassword.isNotBlank()) {
            serverConfig.setPassword(redisPassword)
        }
        return Redisson.create(config)
    }
}
