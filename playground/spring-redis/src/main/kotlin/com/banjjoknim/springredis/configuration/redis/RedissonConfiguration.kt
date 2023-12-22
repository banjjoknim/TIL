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

    /**
     * @see org.redisson.Redisson
     * @see org.redisson.api.RedissonClient
     * @see org.redisson.config.Config
     * @see org.redisson.config.RedissonNodeConfig
     * @see org.redisson.config.RedissonNodeFileConfig
     *
     * @see org.redisson.config.BaseConfig
     * @see org.redisson.config.BaseMasterSlaveServersConfig
     * @see org.redisson.config.SingleServerConfig
     * @see org.redisson.config.ClusterServersConfig
     * @see org.redisson.config.SentinelServersConfig
     * @see org.redisson.config.ReplicatedServersConfig
     * @see org.redisson.config.MasterSlaveServersConfig
     *
     * @see org.redisson.client.codec.Codec
     * @see org.redisson.client.codec.StringCodec
     */
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
