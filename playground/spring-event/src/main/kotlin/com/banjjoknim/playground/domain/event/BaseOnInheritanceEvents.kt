package com.banjjoknim.playground.domain.event

import org.springframework.context.ApplicationEvent

/**
 * 상속 기반 이벤트
 *
 * ApplicationEvent 를 상속받아서 사용한다.
 *
 * @see ApplicationEvent
 */

class AdminInheritanceEvent(source: Any, val username: String) : ApplicationEvent(source)

class CouponInheritanceEvent(source: Any, val email: String) : ApplicationEvent(source)

class SenderInheritanceEvent(source: Any, val email: String, val phoneNumber: String) : ApplicationEvent(source)
