package com.banjjoknim.playground.domain.user

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserServiceBaseOnAnnotationEvent(
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun createUser(request: CreateUserRequest) {
        val user = request.toUser()
        userRepository.save(user)
        user.publishAnnotationEvent(eventPublisher)
    }

    fun createUserWithTransactionalEventListener(request: CreateUserRequest) {
        val user = request.toUser()
        userRepository.save(user)
        user.publishWithTransactionalEventListener(eventPublisher)
    }
}
