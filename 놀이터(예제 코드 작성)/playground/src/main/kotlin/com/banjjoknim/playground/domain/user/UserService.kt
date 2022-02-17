package com.banjjoknim.playground.domain.user

import org.springframework.data.repository.findByIdOrNull
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

    fun retrieveUser(userId: Long): RetrieveUserResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw NoSuchElementException("can not found user. userId: $userId")
        return RetrieveUserResponse(user)
    }
}
