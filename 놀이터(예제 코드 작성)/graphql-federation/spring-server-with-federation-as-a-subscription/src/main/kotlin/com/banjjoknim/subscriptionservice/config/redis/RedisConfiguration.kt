package com.banjjoknim.subscriptionservice.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.*
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RedisConfiguration {

    @Value("\${spring.data.redis.host}")
    private lateinit var redisHost: String

    @Value("\${spring.data.redis.port}")
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
        return LettuceConnectionFactory(RedisStandaloneConfiguration(redisHost, redisPort.toInt()))
    }

    /**
     * ```java
     *  @Bean
     * 	@ConditionalOnMissingBean(RedisConnectionFactory.class)
     * 	LettuceConnectionFactory redisConnectionFactory(
     * 			ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
     * 			ClientResources clientResources) {
     * 		LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources,
     * 				getProperties().getLettuce().getPool());
     * 		return createLettuceConnectionFactory(clientConfig);
     * 	}
     * 	```
     *
     * 	@see org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration // RedisConnectionFactory 타입 Bean이 존재하지 않을 경우 자동으로 redisConnectionFactory Bean을 등록함.
     */
    //    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(redisHost, redisPort.toInt()))
    }
}
