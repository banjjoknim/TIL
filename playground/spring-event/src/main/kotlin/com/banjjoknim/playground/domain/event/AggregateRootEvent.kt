package com.banjjoknim.playground.domain.user

import org.springframework.data.domain.AbstractAggregateRoot
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * ```
 * AbstractAggregateRoot<T> 를 이용하면 쉽게 이벤트를 구현할 수 있다.
 *
 * 단, 명시적으로 AggregateRootRepository<T, ID> 에서 save()가 호출되어야 이벤트가 발행된다.
 * ```
 * @see AbstractAggregateRoot
 */
@Entity
class AggregateRootUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) : AbstractAggregateRoot<AggregateRootUser>() {

    // AggregateRootUser Entity를 사용하는 AggregateRootUserRepository 에서 명시적으로 save() 가 호출되면 이벤트가 발행된다.
    fun publishAggregateRootEvent() {
        super.registerEvent(AggregateRootEvent("colt"))
    }

    data class AggregateRootEvent(val name: String)
}
