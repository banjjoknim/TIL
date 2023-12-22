package com.banjjoknim.playground.domain.admin

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CouponService {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun registerCoupon(email: String) {
        log.info("쿠폰 등록 완료 : {}", email)
    }
}
