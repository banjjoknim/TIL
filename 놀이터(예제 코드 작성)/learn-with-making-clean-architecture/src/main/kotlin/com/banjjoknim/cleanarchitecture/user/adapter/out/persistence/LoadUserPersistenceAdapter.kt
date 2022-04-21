package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.application.port.out.LoadUserPersistencePort
import com.banjjoknim.cleanarchitecture.user.domain.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class LoadUserPersistenceAdapter(
    private val userEntityRepository: UserEntityRepository
) : LoadUserPersistencePort {
    override fun loadUser(userId: Long): User {
        val userEntity = userEntityRepository.findByIdOrNull(userId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. userId: $userId")
        return userEntity.toDomainModel()
    }
}
