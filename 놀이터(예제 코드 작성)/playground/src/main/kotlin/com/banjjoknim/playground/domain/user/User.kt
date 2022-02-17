package com.banjjoknim.playground.domain.user

import com.banjjoknim.playground.domain.event.AdminInheritanceEvent
import com.banjjoknim.playground.domain.event.CouponInheritanceEvent
import com.banjjoknim.playground.domain.event.SenderInheritanceEvent
import org.springframework.context.ApplicationEventPublisher
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0L,

    @Column(name = "name")
    val name: String = "",

    @Column(name = "email")
    val email: String = "",

    @Column(name = "phoneNumber")
    val phoneNumber: String = ""
) {
    /**
     * 상속 기반의 이벤트 사용
     */
    fun publishEvent(eventPublisher: ApplicationEventPublisher) {
        publishAdminEvent(eventPublisher)
        publishCouponEvent(eventPublisher)
        publishSenderEvent(eventPublisher)
    }

    private fun publishAdminEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(AdminInheritanceEvent(this, name))
    }

    private fun publishCouponEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(CouponInheritanceEvent(this, email))
    }

    private fun publishSenderEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(SenderInheritanceEvent(this, email, phoneNumber))
    }
}
