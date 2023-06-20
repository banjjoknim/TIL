package com.banjjoknim.springredis.redis.application

import com.banjjoknim.springredis.redis.api.SetKeyRequest
import com.banjjoknim.springredis.redis.api.SetKeyResponse
import org.redisson.api.RedissonClient
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Profile("redisson")
@Service
class RedissonSetKeyService(
    private val redissonClient: RedissonClient,
) : RedisSetKeyService {

    override fun setKey(request: SetKeyRequest): SetKeyResponse {
        val bucket = redissonClient.getBucket<String>(request.key)
        when (val expireTime = request.expireTime) {
            null -> bucket.set(request.value)
            else -> bucket.set(request.value, expireTime, TimeUnit.SECONDS)
        }

        return SetKeyResponse(bucket.name, bucket.get(), bucket.remainTimeToLive())
    }
}
