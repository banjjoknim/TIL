package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.application.port.out.UpsertUserPersistencePort
import com.banjjoknim.cleanarchitecture.user.pojo.User
import org.springframework.stereotype.Component

@Component
class UpsertUserPersistenceAdapter(
    private val userEntityRepository: UserEntityRepository
): UpsertUserPersistencePort {
    override fun upsert(user: User) {
        userEntityRepository.save(user.toDomainEntity())
    }
}
