package com.banjjoknim.springredis.configuration.redis.support

interface RedisLockManager {

    fun acquireLock(messageKey: String): Boolean

    fun releaseLock(messageKey: String): Boolean
}
