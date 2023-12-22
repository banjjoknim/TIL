package com.banjjoknim.playground.domain.admin

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AdminService {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun alarm(username: String) {
        log.info("어드민 서비스 : {}님이 가입했습니다.", username)
    }
}
