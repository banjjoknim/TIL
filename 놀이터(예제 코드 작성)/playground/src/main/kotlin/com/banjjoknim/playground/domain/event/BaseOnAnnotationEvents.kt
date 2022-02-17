package com.banjjoknim.playground.domain.event

/**
 * 어노테이션을 사용한 이벤트
 *
 * 어노테이션을 사용하면 상속을 받지 않아도 되기 때문에 스프링에 대한 의존성이 제거된 순수한 자바 객체를 이벤트 객체로 사용할 수 있다.
 *
 * @see EventListener
 */

class AdminAnnotationEvent(val username: String)

class CouponAnnotationEvent(val email: String)

class SenderAnnotationEvent(val email: String, val phoneNumber: String)

class AdminTransactionalEvent(val username: String)

class CouponTransactionalEvent(val email: String)

class SenderTransactionalEvent(val email: String, val phoneNumber: String)
