package com.banjjoknim.springredis.redis.application

import com.banjjoknim.springredis.redis.api.SetKeyRequest
import com.banjjoknim.springredis.redis.api.SetKeyResponse
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Profile("spring-redis")
@Service
class SpringRedisSetKeyService(
    private val redisTemplate: StringRedisTemplate,
) : RedisSetKeyService {

    override fun setKey(request: SetKeyRequest): SetKeyResponse {
        val valueOperations = redisTemplate.boundValueOps(request.key)
        when (request.expireTime) {
            null -> valueOperations.set(request.value)
            else -> valueOperations.set(request.value, request.expireTime, TimeUnit.SECONDS)
        }

        return SetKeyResponse(request.key, request.value, request.expireTime)
    }
}
