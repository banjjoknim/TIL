package com.banjjoknim.playground.domain.user

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 상속 기반의 이벤트를 사용하는 서비스
 *
 * @see BaseOnInheritanceEventListeners
 * @see BaseOnInheritanceEvents
 */
@Transactional
@Service
class UserServiceBaseOnInheritanceEvent(
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun createUser(request: CreateUserRequest) {
        val user = request.toUser()
        userRepository.save(user)
        user.publishInheritanceEvent(eventPublisher)
    }
}
