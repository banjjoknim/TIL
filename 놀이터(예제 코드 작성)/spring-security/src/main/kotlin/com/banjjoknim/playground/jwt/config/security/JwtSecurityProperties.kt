package com.banjjoknim.playground.jwt.config.security

object JwtSecurityProperties {
    const val SECRET = "banjjoknim"
    const val EXPIRATION_TIME_SECONDS = 60 * 1000 * 10 // 10분 (1/1000 초를 기준으로 한다)
    const val BEARER_TOKEN_PREFIX = "Bearer "
}
