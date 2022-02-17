package com.banjjoknim.playground.domain.event

import org.hibernate.SessionFactory
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.event.spi.PostInsertEvent
import org.hibernate.event.spi.PostInsertEventListener
import org.hibernate.internal.SessionFactoryImpl
import org.hibernate.persister.entity.EntityPersister
import org.springframework.stereotype.Component

@Component
class DomainEvent {

    private lateinit var sessionFactory: SessionFactory

    fun sample() {
        val registry = (sessionFactory as SessionFactoryImpl).serviceRegistry.getService(EventListenerRegistry::class.java)
        registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(CustomEventListener())
    }
}

@Component
class CustomEventListener : PostInsertEventListener {
    override fun requiresPostCommitHanding(persister: EntityPersister?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPostInsert(event: PostInsertEvent?) {
        TODO("Not yet implemented")
    }
}
