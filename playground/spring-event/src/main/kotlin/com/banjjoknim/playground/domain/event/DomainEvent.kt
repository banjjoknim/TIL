package com.banjjoknim.playground.domain.event

import org.hibernate.SessionFactory
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.event.spi.PostInsertEvent
import org.hibernate.event.spi.PostInsertEventListener
import org.hibernate.internal.SessionFactoryImpl
import org.hibernate.persister.entity.EntityPersister
import org.springframework.stereotype.Component

/**
 * ```
 * org.hibernate.event.spi.EventType 을 살펴보면, 다양한 이벤트 발행시점(상태)을 볼 수 있다.
 *
 * 다양한 이벤트 발행시점(상태)과 이벤트를 이용해서 비즈니스 로직을 처리할 수 있다.
 *
 * 이벤트를 심도있게, 더 잘 활용하고 싶다면 Hibernate Session Event (Hibernate Event Session) 를 공부하도록 하자.
 *
 * Hibernate Session 의 이벤트 인터셉트를 이용해서 다양하게 활용할 수 있다.
 * ```
 * @see EventType
 */
@Component
class DomainEvent {

    private lateinit var sessionFactory: SessionFactory

    fun sample() {
        val registry = (sessionFactory as SessionFactoryImpl).serviceRegistry.getService(EventListenerRegistry::class.java)
        registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(CustomEventListener())
    }
}

/**
 *
 */
@Component
class CustomEventListener : PostInsertEventListener {
    override fun requiresPostCommitHanding(persister: EntityPersister?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPostInsert(event: PostInsertEvent?) {
        TODO("Not yet implemented")
    }
}
