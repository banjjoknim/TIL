package com.banjjoknim.playground.domain.user

import com.banjjoknim.playground.domain.event.AdminAnnotationEvent
import com.banjjoknim.playground.domain.event.AdminInheritanceEvent
import com.banjjoknim.playground.domain.event.CouponAnnotationEvent
import com.banjjoknim.playground.domain.event.CouponInheritanceEvent
import com.banjjoknim.playground.domain.event.SenderAnnotationEvent
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
    fun publishInheritanceEvent(eventPublisher: ApplicationEventPublisher) {
        publishInheritanceAdminEvent(eventPublisher)
        publishInheritanceCouponEvent(eventPublisher)
        publishInheritanceSenderEvent(eventPublisher)
    }

    private fun publishInheritanceAdminEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(AdminInheritanceEvent(this, name))
    }

    private fun publishInheritanceCouponEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(CouponInheritanceEvent(this, email))
    }

    private fun publishInheritanceSenderEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(SenderInheritanceEvent(this, email, phoneNumber))
    }

    fun publishAnnotationEvent(eventPublisher: ApplicationEventPublisher) {
        publishAnnotationAdminEvent(eventPublisher)
        publishAnnotationCouponEvent(eventPublisher)
        publishAnnotationSenderEvent(eventPublisher)
    }

    private fun publishAnnotationAdminEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(AdminAnnotationEvent(name))
    }

    private fun publishAnnotationCouponEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(CouponAnnotationEvent(email))
    }

    private fun publishAnnotationSenderEvent(eventPublisher: ApplicationEventPublisher) {
        eventPublisher.publishEvent(SenderAnnotationEvent(email, phoneNumber))
    }
}
