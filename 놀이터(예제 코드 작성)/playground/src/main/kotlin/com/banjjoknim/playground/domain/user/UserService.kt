package com.banjjoknim.playground.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(request: CreateUserRequest) {
        val user = request.toUser()
        userRepository.save(user)
    }
}
