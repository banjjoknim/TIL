package com.banjjoknim.springredis.redis.application

import com.banjjoknim.springredis.redis.api.SetKeyRequest
import com.banjjoknim.springredis.redis.api.SetKeyResponse

interface RedisSetKeyService {

    fun setKey(request: SetKeyRequest): SetKeyResponse
}
