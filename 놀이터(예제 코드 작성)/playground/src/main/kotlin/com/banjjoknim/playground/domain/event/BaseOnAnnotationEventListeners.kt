package com.banjjoknim.playground.domain.event

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * ```
 * 기존에는 이벤트 리스너를 사용하기 위해서 ApplicationListener<T> 를 상속(또는 구현)해서 사용해야 했기에, 이벤트가 상속에 종속적이 되어 문제가 될 수 있었다.
 *
 * 하지만 스프링 4.2부터는 이벤트 리스너의 이벤트 처리를 어노테이션 기반으로 작성할 수 있도록 개선되었다.
 *
 * ApplicationListenerMethodAdapter 객체가 @EventListener 어노테이션을 찾아서 실행시켜준다.
 *
 * 빈 단위로 정의된 리스너(ApplicationListener<T> 를 상속하고 Component 로 등록된)에 대해서는 구현체로 SimpleApplicationEventMulticaster 를 사용한다. SimpleApplicationEventMulticaster 는 빈 단위로 정의된 작업을 실행시켜준다.
 *
 * 리스너는 메소드 단위로도 정의할 수 있다. 메소드에 @EventListener 어노테이션을 붙이면 이때는 구현체로 ApplicationListenerMethodAdapter 를 사용한다(구현체가 변경된다).
 * ```
 *
 * @see AbstractApplicationContext
 * @see ApplicationListenerMethodAdapter
 * @see SimpleApplicationEventMulticaster
 * @see ApplicationListenerMethodAdapter
 * @see EventListener(org.springframework.context.event)
 */

@Component
class AdminAnnotationEventListener {
    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun onApplicationEvent(event: AdminAnnotationEvent) {
        log.info("어드민 서비스 : {}님이 가입했습니다.", event.username)
    }
}

@Component
class CouponAnnotationEventListener {
    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun onApplicationEvent(event: CouponAnnotationEvent) {
        log.info("쿠폰 등록 완료 : {}", event.email)
    }
}

@Component
class SenderAnnotationEventListener {
    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handleEmail(event: SenderAnnotationEvent) {
        log.info("환영 이메일 발송 성공 : {}", event.email)
    }

    @EventListener
    fun handleSMS(event: SenderAnnotationEvent) {
        log.info("환영 SMS 발송 성공 : {}", event.phoneNumber)
    }
}

/**
 * 트랜잭션 단위를 이벤트에서 관리(제어)하기 위해서 @TransactionalEventListener 를 사용할 수 있다.
 *
 * 기본 설정은 TransactionPhase.AFTER_COMMIT 이다. 이 외에도 여러 상태가 있으니 참고하여 설계하는데 사용하도록 하자.
 *
 * @see TransactionalEventListener
 */

@Component
class AdminTransactionalEventListener {
    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun onApplicationEvent(event: AdminTransactionalEvent) {
        throw RuntimeException()
//        log.info("어드민 서비스 : {}님이 가입했습니다.", event.username)
    }
}

@Component
class CouponTransactionalEventListener {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 아래의 @TransactionalEventListener 로 인해
     *
     * 트랜잭션 커밋이 정상적으로 성공한 이후에 이벤트가 실행(처리)된다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onApplicationEvent(event: CouponTransactionalEvent) {
        log.info("쿠폰 등록 완료 : {}", event.email)
    }
}

@Component
class SenderTransactionalEventListener {
    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handleEmail(event: SenderTransactionalEvent) {
        log.info("환영 이메일 발송 성공 : {}", event.email)
    }

    @EventListener
    fun handleSMS(event: SenderTransactionalEvent) {
        log.info("환영 SMS 발송 성공 : {}", event.phoneNumber)
    }
}

