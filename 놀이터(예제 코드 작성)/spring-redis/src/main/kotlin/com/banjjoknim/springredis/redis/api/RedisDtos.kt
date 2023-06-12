package com.banjjoknim.springredis.redis.api

data class SetKeyRequest(
    val key: String,
    val value: String,
    val expireTime: Long? = null,
)

data class SetKeyResponse(
    val key: String,
    val value: String,
    val expireTime: Long? = null,
)
