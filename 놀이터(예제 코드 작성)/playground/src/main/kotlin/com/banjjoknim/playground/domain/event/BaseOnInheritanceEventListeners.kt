package com.banjjoknim.playground.domain.event

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * 상속 기반 이벤트 리스너
 *
 * 스프링의 컨텍스트를 사용하기 때문에 빈으로 등록해줘야 사용할 수 있다
 *
 * @see ApplicationListener
 */

@Component
class AdminInheritanceEventListener : ApplicationListener<AdminInheritanceEvent> {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: AdminInheritanceEvent) {
        log.info("어드민 서비스 : {}님이 가입했습니다.", event.username)
    }
}

@Component
class CouponInheritanceEventListener : ApplicationListener<CouponInheritanceEvent> {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: CouponInheritanceEvent) {
        log.info("쿠폰 등록 완료 : {}", event.email)
    }
}

@Component
class SenderInheritanceEventListener : ApplicationListener<SenderInheritanceEvent> {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: SenderInheritanceEvent) {
        log.info("환영 이메일 발송 성공 : {}", event.email)
        log.info("환영 SMS 발송 성공 : {}", event.phoneNumber)
    }
}
